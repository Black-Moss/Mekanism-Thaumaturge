package com.blackmoss.mekanismthaumaturge.block;

import com.blackmoss.mekanismthaumaturge.common.MTTransmission;
import com.blackmoss.mekanismthaumaturge.network.PacketEssentiaNetworkSync;
import com.leclowndu93150.thaumaturge.api.aspect.Aspects;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.essentia.EssentiaCapabilities;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import com.mojang.serialization.Codec;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.content.network.transmitter.BufferedTransmitter;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.tier.TubeTier;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EssentiaTube extends BufferedTransmitter<IEssentiaTransport, EssentiaNetwork, EssentiaBuffer, EssentiaTube> {
    private static final String ESSENTIA_TYPE_KEY = "EssentiaType";
    private static final String ESSENTIA_AMOUNT_KEY = "EssentiaAmount";
    private static final String ESSENTIA_SCALE_KEY = "EssentiaScale";
    private static final Codec<ResourceKey<IAspect>> ASPECT_KEY_CODEC = ResourceKey.codec(IAspect.REGISTRY_KEY);

    public final TubeTier tier;
    @Nullable
    private Holder<IAspect> bufferType;
    private int bufferAmount;
    @Nullable
    private Holder<IAspect> saveShareType;
    private int saveShareAmount;
    @Nullable
    private ResourceKey<IAspect> pendingTypeKey;
    private int pendingTypeAmount;

    public EssentiaTube(@NonNull Holder<Block> blockProvider, @NonNull TileEntityTransmitter tile) {
        super(tile, MTTransmission.ESSENTIA);
        this.tier = Attribute.getTierNN(blockProvider, TubeTier.class);
    }

    @Override
    protected @NonNull AcceptorCache<IEssentiaTransport> createAcceptorCache() {
        return new AcceptorCache<>(getTransmitterTile(), EssentiaCapabilities.TRANSPORT);
    }

    @Override
    public @NonNull EssentiaNetwork createEmptyNetworkWithID(@NonNull UUID networkID) {
        return new EssentiaNetwork(networkID);
    }

    @Override
    public @NonNull EssentiaNetwork createNetworkByMerging(@NonNull Collection<EssentiaNetwork> networks) {
        return new EssentiaNetwork(networks);
    }

    @Override
    public long getCapacity() {
        return tier.getCapacity() / 100;
    }

    public int getTransferRate() {
        return Math.max(1, tier.getTransferRate() / 10);
    }

    @Override
    public @NonNull EssentiaBuffer getBufferWithFallback() {
        EssentiaBuffer share = getShare();
        if (!share.isEmpty()) {
            return share;
        }
        return hasTransmitterNetwork() ? getTransmitterNetworkNN().getBuffer() : EssentiaBuffer.EMPTY;
    }

    @Override
    public boolean noBufferOrFallback() {
        return getBufferWithFallback().isEmpty();
    }

    @Override
    public @NonNull EssentiaBuffer getShare() {
        resolvePending();
        return new EssentiaBuffer(bufferType, bufferAmount);
    }

    @Override
    public @NonNull EssentiaBuffer releaseShare() {
        EssentiaBuffer share = getShare();
        bufferType = null;
        bufferAmount = 0;
        return share;
    }

    public void setBuffer(@Nullable Holder<IAspect> type, int amount) {
        if (type == null || amount <= 0) {
            bufferType = null;
            bufferAmount = 0;
            return;
        }
        bufferType = type;
        bufferAmount = amount;
        getTransmitterTile().setChanged();
    }

    @Override
    public void takeShare(@Nullable TransactionContext transaction) {
        Holder<IAspect> type = saveShareType;
        int amount = saveShareAmount;
        if (type == null || amount <= 0) {
            return;
        }
        if (hasTransmitterNetwork()) {
            int movable = Math.min(amount, getTransmitterNetworkNN().getStored());
            getTransmitterNetworkNN().removeEssentiaAmount(movable, transaction);
            amount = movable;
        }
        if (amount > 0) {
            bufferType = type;
            bufferAmount = amount;
            getTransmitterTile().setChanged();
        }
        saveShareAmount = amount;
    }

    @Override
    public void read(@NonNull ValueInput input) {
        super.read(input);
        int amount = input.getIntOr(ESSENTIA_AMOUNT_KEY, 0);
        ResourceKey<IAspect> key = input.read(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC).orElse(null);
        bufferType = null;
        bufferAmount = 0;
        clearSaveShare();
        pendingTypeKey = null;
        pendingTypeAmount = 0;
        if (key == null || amount <= 0) {
            return;
        }
        Holder<IAspect> holder = Aspects.resolve(getTransmitterTile().getLevel(), key);
        if (holder != null) {
            bufferType = holder;
            bufferAmount = amount;
        } else {
            pendingTypeKey = key;
            pendingTypeAmount = amount;
        }
    }

    @Override
    public void write(@NonNull ValueOutput output) {
        super.write(output);
        if (hasTransmitterNetwork()) {
            getTransmitterNetworkNN().validateSaveShares(this, null);
            ResourceKey<IAspect> key = saveShareType == null
                    ? null
                    : saveShareType.unwrapKey().orElse(null);
            output.storeNullable(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC, key);
            output.putInt(ESSENTIA_AMOUNT_KEY, saveShareAmount);
        } else {
            ResourceKey<IAspect> key = bufferType == null
                    ? null
                    : bufferType.unwrapKey().orElse(null);
            output.storeNullable(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC, key);
            output.putInt(ESSENTIA_AMOUNT_KEY, bufferAmount);
        }
    }

    public void writeStateTo(@NonNull ValueOutput output) {
        Holder<IAspect> type = getStoredType();
        int amount = getStored();
        ResourceKey<IAspect> key = type == null ? null : type.unwrapKey().orElse(null);
        output.storeNullable(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC, key);
        output.putInt(ESSENTIA_AMOUNT_KEY, amount);
        if (hasTransmitterNetwork()) {
            output.putFloat(ESSENTIA_SCALE_KEY, getTransmitterNetworkNN().currentScale);
        }
    }

    @Override
    public void writeReducedUpdatedTag(@NonNull ValueOutput output) {
        super.writeReducedUpdatedTag(output);
        writeStateTo(output);
    }

    @Override
    public boolean handleUpdateTag(@NonNull ValueInput input) {
        boolean updated = super.handleUpdateTag(input);
        if (hasTransmitterNetwork()) {
            handleContentsUpdateTag(getTransmitterNetworkNN(), input);
            PacketEssentiaNetworkSync.applyPending(getTransmitterNetworkNN().getUUID());
        }
        return updated;
    }

    @Override
    protected void handleContentsUpdateTag(@NonNull EssentiaNetwork network, @NonNull ValueInput input) {
        ResourceKey<IAspect> key = input.read(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC).orElse(null);
        int amount = input.getIntOr(ESSENTIA_AMOUNT_KEY, 0);
        float scale = input.getFloatOr(ESSENTIA_SCALE_KEY, -1F);
        if (scale >= 0F) {
            network.currentScale = scale;
        }
        Holder<IAspect> holder = key == null ? null : Aspects.resolve(getTransmitterTile().getLevel(), key);
        if (holder != null) {
            network.setClientContents(holder, amount);
        } else if (key == null) {
            network.setClientContents(null, 0);
        }
    }

    public void resolvePendingType(@Nullable Level level) {
        if (pendingTypeKey == null) {
            return;
        }
        Holder<IAspect> holder = Aspects.resolve(level, pendingTypeKey);
        if (holder != null) {
            bufferType = holder;
            bufferAmount = pendingTypeAmount;
            pendingTypeKey = null;
            pendingTypeAmount = 0;
        }
    }

    private void resolvePending() {
        if (pendingTypeKey != null) {
            resolvePendingType(getTransmitterTile().getLevel());
        }
    }

    public void clearSaveShare() {
        saveShareType = null;
        saveShareAmount = 0;
    }

    public void setSaveShare(@NonNull EssentiaBuffer share) {
        saveShareType = share.type();
        saveShareAmount = share.amount();
    }

    public int getSaveShareCapacity() {
        return maxAmount();
    }

    @Override
    public void pullFromAcceptors(@NonNull ServerLevel level) {
        if (!hasPullSide) {
            return;
        }
        int space = getStoredCapacity() - getStored();
        if (space <= 0) {
            return;
        }
        Holder<IAspect> wanted = getStoredType();
        AcceptorCache<IEssentiaTransport> cache = getAcceptorCache();
        try (Transaction transaction = Transaction.openRoot()) {
            EssentiaTransferJournal journal = new EssentiaTransferJournal();
            for (Direction side : Direction.values()) {
                if (space <= 0) {
                    break;
                }
                if (!isConnectionType(side, ConnectionType.PULL)) {
                    continue;
                }
                IEssentiaTransport acceptor = cache.getConnectedAcceptor(side);
                if (acceptor == null) {
                    continue;
                }
                Direction facing = side.getOpposite();
                Holder<IAspect> type = wanted != null ? wanted : acceptor.getEssentiaType(facing);
                if (type == null) {
                    continue;
                }
                if (hasTransmitterNetwork() && !getTransmitterNetworkNN().canAcceptEssentia(type)) {
                    continue;
                }
                int spaceFor = acceptor.spaceFor(type, facing);
                if (spaceFor <= 0) {
                    continue;
                }
                int want = Math.min(Math.min(getTransferRate(), space), spaceFor);
                int jarBefore = acceptor.getEssentiaAmount(facing);
                int removed = journal.extract(acceptor, type, want, facing, transaction);
                int jarAfter = acceptor.getEssentiaAmount(facing);
                if (removed <= 0) {
                    continue;
                }
                int inserted = insertEssentia(type, removed, transaction);
                if (inserted < removed) {
                    journal.insert(acceptor, type, removed - inserted, facing, transaction);
                }
                space -= inserted;
                wanted = getStoredType();
            }
            transaction.commit();
        }
    }

    public @Nullable Holder<IAspect> getStoredType() {
        resolvePending();
        if (hasTransmitterNetwork()) {
            Holder<IAspect> type = getTransmitterNetworkNN().getBuffer().type();
            if (type != null) {
                return type;
            }
        }
        return bufferType;
    }

    public int getStored() {
        if (hasTransmitterNetwork()) {
            int amount = getTransmitterNetworkNN().getBuffer().amount();
            if (amount > 0) {
                return amount;
            }
        }
        return bufferAmount;
    }

    public int getStoredCapacity() {
        long capacity = hasTransmitterNetwork() ? getTransmitterNetworkNN().getCapacity() : getCapacity();
        return (int) Math.clamp(capacity, 0L, Integer.MAX_VALUE);
    }

    public int insertEssentia(@Nullable Holder<IAspect> type, int count) {
        return insertEssentia(type, count, null);
    }

    public int insertEssentia(@Nullable Holder<IAspect> type, int count, @Nullable TransactionContext transaction) {
        if (type == null || count <= 0) {
            return 0;
        }
        Holder<IAspect> currentType = getStoredType();
        if (currentType != null && !currentType.equals(type)) {
            return 0;
        }
        if (hasTransmitterNetwork()) {
            int inserted = getTransmitterNetworkNN().addEssentia(type, count, transaction);
            if (inserted > 0) {
                getTransmitterTile().setChanged();
            }
            return inserted;
        }
        int accepted = Math.clamp(maxAmount() - bufferAmount, 0, count);
        if (accepted > 0) {
            if (bufferType == null) {
                bufferType = type;
            }
            bufferAmount += accepted;
            getTransmitterTile().setChanged();
        }
        return accepted;
    }

    public int extractEssentia(@Nullable Holder<IAspect> type, int count) {
        return extractEssentia(type, count, null);
    }

    public int extractEssentia(@Nullable Holder<IAspect> type, int count, @Nullable TransactionContext transaction) {
        if (type == null || count <= 0) {
            return 0;
        }
        int removed;
        if (hasTransmitterNetwork()) {
            removed = getTransmitterNetworkNN().takeEssentia(type, count, transaction);
        } else {
            if (bufferType == null || !bufferType.equals(type)) {
                return 0;
            }
            removed = Math.min(count, bufferAmount);
            bufferAmount -= removed;
            if (bufferAmount <= 0) {
                bufferType = null;
            }
        }
        if (removed > 0) {
            getTransmitterTile().setChanged();
        }
        return removed;
    }

    private int maxAmount() {
        return (int) Math.clamp(getCapacity(), 0L, Integer.MAX_VALUE);
    }
}
