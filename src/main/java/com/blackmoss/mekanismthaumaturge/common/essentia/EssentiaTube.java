package com.blackmoss.mekanismthaumaturge.common.essentia;

import com.blackmoss.mekanismthaumaturge.common.MTTransmission;
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
import mekanism.common.util.EnumUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EssentiaTube extends BufferedTransmitter<IEssentiaTransport, EssentiaNetwork, EssentiaBuffer, EssentiaTube> {
    private static final String ESSENTIA_TYPE_KEY = "EssentiaType";
    private static final String ESSENTIA_AMOUNT_KEY = "EssentiaAmount";
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
        return tier.getCapacity();
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

    @Override
    public void takeShare(@Nullable TransactionContext transaction) {
        Holder<IAspect> type = saveShareType;
        int amount = saveShareAmount;
        if (type == null || amount <= 0) {
            return;
        }
        if (hasTransmitterNetwork()) {
            amount = getTransmitterNetworkNN().takeEssentia(type, amount);
        }
        if (amount > 0) {
            bufferType = type;
            bufferAmount = amount;
            getTransmitterTile().setChanged();
        }
        clearSaveShare();
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
        }
        Holder<IAspect> type = saveShareType;
        int amount = saveShareAmount;
        if (type == null || amount <= 0) {
            EssentiaBuffer fallback = getShare();
            type = fallback.type();
            amount = fallback.amount();
        }
        ResourceKey<IAspect> key = type == null ? null : type.unwrapKey().orElse(null);
        output.storeNullable(ESSENTIA_TYPE_KEY, ASPECT_KEY_CODEC, key);
        output.putInt(ESSENTIA_AMOUNT_KEY, amount);
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
        int needed = Math.max(0, maxAmount() - bufferAmount);
        if (needed <= 0) {
            return;
        }
        Holder<IAspect> wanted = bufferType;
        if (wanted == null && hasTransmitterNetwork()) {
            wanted = getTransmitterNetworkNN().getBuffer().type();
        }
        AcceptorCache<IEssentiaTransport> cache = getAcceptorCache();
        for (Direction side : EnumUtils.DIRECTIONS) {
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
            int received = acceptor.takeEssentia(type, needed, facing);
            if (received > 0) {
                bufferType = type;
                bufferAmount += received;
                needed -= received;
                getTransmitterTile().setChanged();
            }
            if (needed <= 0) {
                break;
            }
        }
    }

    public @Nullable Holder<IAspect> getStoredType() {
        resolvePending();
        return hasTransmitterNetwork() ? getTransmitterNetworkNN().getBuffer().type() : bufferType;
    }

    public int getStored() {
        return hasTransmitterNetwork() ? getTransmitterNetworkNN().getBuffer().amount() : bufferAmount;
    }

    public int getStoredCapacity() {
        long capacity = hasTransmitterNetwork() ? getTransmitterNetworkNN().getCapacity() : getCapacity();
        return (int) Math.clamp(capacity, 0L, Integer.MAX_VALUE);
    }

    public int insertEssentia(@Nullable Holder<IAspect> type, int count) {
        if (type == null || count <= 0) {
            return 0;
        }
        Holder<IAspect> currentType = getStoredType();
        if (currentType != null && !currentType.equals(type)) {
            return 0;
        }
        int accepted = Math.clamp(getStoredCapacity() - getStored(), 0, count);
        if (accepted <= 0) {
            return 0;
        }
        if (hasTransmitterNetwork()) {
            accepted = getTransmitterNetworkNN().addEssentia(type, accepted);
        } else {
            bufferType = type;
            bufferAmount += accepted;
        }
        getTransmitterTile().setChanged();
        return accepted;
    }

    public int extractEssentia(@Nullable Holder<IAspect> type, int count) {
        if (type == null || count <= 0) {
            return 0;
        }
        int removed;
        if (hasTransmitterNetwork()) {
            removed = getTransmitterNetworkNN().takeEssentia(type, count);
        } else {
            if (bufferType == null || !bufferType.equals(type)) {
                return 0;
            }
            removed = Math.min(count, bufferAmount);
            bufferAmount -= removed;
            if (bufferAmount == 0) {
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
