package com.blackmoss.mekanismthaumaturge.block;

import com.blackmoss.mekanismthaumaturge.network.PacketEssentiaNetworkSync;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.network.PacketUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class EssentiaNetwork extends DynamicBufferedNetwork<IEssentiaTransport, EssentiaNetwork, EssentiaBuffer, EssentiaPipe> {
    private final EssentiaContainer container;
    @Nullable
    private Holder<IAspect> lastSyncType;
    private int lastSyncAmount;

    public EssentiaNetwork(@NonNull UUID networkID) {
        super(networkID);
        this.container = new EssentiaContainer(this::getCapacity);
    }

    public EssentiaNetwork(@NonNull Collection<EssentiaNetwork> networks) {
        this(UUID.randomUUID());
        adoptAllAndRegister(networks);
    }

    public int getRate() {
        int rate = 0;
        for (EssentiaPipe pipe : getTransmitters()) {
            rate = Math.max(rate, pipe.getTransferRate());
        }
        return rate > 0 ? rate : 1;
    }

    @Nullable
    public Holder<IAspect> getStoredType() {
        return container.resource();
    }

    public int getStored() {
        return container.amountAsInt();
    }

    public void setClientContents(@Nullable Holder<IAspect> type, int amount) {
        container.setContents(type, amount, null);
    }

    @Override
    protected float computeContentScale() {
        long capacity = getCapacity();
        if (capacity <= 0) {
            return 0;
        }
        float raw = (float) Math.min(1.0D, container.amountAsLong() / (double) capacity);
        return Math.clamp(Math.max((float) Math.cbrt(raw), 0.3F), 0F, 1F);
    }

    @Override
    protected void forceScaleUpdate() {
        currentScale = computeContentScale();
    }

    @Override
    public @NonNull EssentiaBuffer getBuffer() {
        return container.asStack();
    }

    @Override
    public void absorbBuffer(@NonNull EssentiaPipe transmitter, @NonNull TransactionContext transaction) {
        EssentiaBuffer share = transmitter.releaseShare();
        if (share.isEmpty()) {
            return;
        }
        int moved = addEssentia(share.type(), share.amount(), transaction);
        if (moved < share.amount()) {
            transmitter.setBuffer(share.type(), share.amount() - moved);
        }
    }

    @Override
    public void clampBuffer() {
        int capacity = maxAmount();
        int amount = container.amountAsInt();
        if (amount > capacity) {
            container.setContents(container.resource(), capacity, null);
            onContentsChanged();
        }
    }

    @Override
    public boolean isCompatibleWith(@NonNull EssentiaNetwork other) {
        if (!super.isCompatibleWith(other)) {
            return false;
        }
        Holder<IAspect> mine = container.resource();
        Holder<IAspect> theirs = other.container.resource();
        return mine == null || theirs == null || mine.equals(theirs);
    }

    @Override
    public @NonNull List<EssentiaPipe> adoptTransmittersAndAcceptorsFrom(@NonNull EssentiaNetwork net) {
        List<EssentiaPipe> transmitters = super.adoptTransmittersAndAcceptorsFrom(net);
        Holder<IAspect> type = net.container.resource();
        int amount = net.container.amountAsInt();
        if (type != null && amount > 0) {
            int moved = addEssentia(type, amount);
            net.takeEssentia(type, moved);
        }
        clampBuffer();
        return transmitters;
    }

    @Override
    protected void updateSaveShares(@Nullable EssentiaPipe triggerTransmitter, @NonNull TransactionContext transaction) {
        super.updateSaveShares(triggerTransmitter, transaction);
        Collection<EssentiaPipe> transmitters = getTransmitters();
        for (EssentiaPipe pipe : transmitters) {
            pipe.clearSaveShare();
        }
        Holder<IAspect> type = container.resource();
        if (type == null || container.amountAsInt() <= 0 || transmitters.isEmpty()) {
            return;
        }
        int totalCapacity = 0;
        for (EssentiaPipe pipe : transmitters) {
            totalCapacity += pipe.getSaveShareCapacity();
        }
        if (totalCapacity <= 0) {
            return;
        }
        int undistributed = container.amountAsInt();
        int index = 0;
        int last = transmitters.size() - 1;
        for (EssentiaPipe pipe : transmitters) {
            if (undistributed <= 0) {
                break;
            }
            int capacity = pipe.getSaveShareCapacity();
            int share;
            if (index == last) {
                share = Math.min(capacity, undistributed);
            } else {
                share = Math.min(capacity, (int) Math.round(undistributed * (capacity / (double) totalCapacity)));
            }
            if (share > 0) {
                pipe.setSaveShare(new EssentiaBuffer(type, share));
                undistributed -= share;
            }
            index++;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tickEmit();
        currentScale = computeContentScale();
        try (Transaction transaction = Transaction.openRoot()) {
            updateSaveShares(null, transaction);
            transaction.commit();
        }
        if (container.amountAsInt() != lastSyncAmount || !Objects.equals(container.resource(), lastSyncType)) {
            lastSyncAmount = container.amountAsInt();
            lastSyncType = container.resource();
            for (EssentiaPipe pipe : getTransmitters()) {
                pipe.requestsUpdate();
            }
            PacketUtils.sendToAllTracking(this, new PacketEssentiaNetworkSync(this));
        }
    }

    private void tickEmit() {
        Holder<IAspect> type = container.resource();
        int amount = container.amountAsInt();
        if (type == null || amount <= 0) {
            return;
        }
        List<IEssentiaTransport> targets = new ArrayList<>();
        List<Direction> facings = new ArrayList<>();
        for (EssentiaPipe pipe : getTransmitters()) {
            AcceptorCache<IEssentiaTransport> cache = pipe.getAcceptorCache();
            for (Direction side : Direction.values()) {
                if (pipe.isConnectionType(side, ConnectionType.PULL)) {
                    continue;
                }
                IEssentiaTransport acceptor = cache.getConnectedAcceptor(side);
                if (acceptor == null || acceptor instanceof EssentiaPipeBlockEntity) {
                    continue;
                }
                Direction facing = side.getOpposite();
                if (acceptor.spaceFor(type, facing) <= 0) {
                    continue;
                }
                targets.add(acceptor);
                facings.add(facing);
            }
        }
        if (targets.isEmpty()) {
            return;
        }
        int remaining = amount;
        try (Transaction transaction = Transaction.openRoot()) {
            EssentiaTransferJournal journal = new EssentiaTransferJournal();
            while (remaining > 0) {
                boolean progressed = false;
                int quota = Math.max(1, remaining / targets.size());
                for (int i = 0; i < targets.size() && remaining > 0; i++) {
                    IEssentiaTransport acceptor = targets.get(i);
                    int accepted = journal.insert(acceptor, type, Math.min(quota, remaining), facings.get(i), transaction);
                    if (accepted > 0) {
                        remaining -= accepted;
                        progressed = true;
                    }
                }
                if (!progressed) {
                    break;
                }
            }
            int sent = amount - remaining;
            if (sent > 0) {
                removeEssentiaAmount(sent, transaction);
            }
            transaction.commit();
        }
    }

    public boolean canAcceptEssentia(@Nullable Holder<IAspect> type) {
        if (type == null) {
            return false;
        }
        Holder<IAspect> current = container.resource();
        if (current != null && !current.equals(type)) {
            return false;
        }
        return container.amountAsInt() < maxAmount();
    }

    public boolean hasOutputTarget(Holder<IAspect> type) {
        for (EssentiaPipe pipe : getTransmitters()) {
            AcceptorCache<IEssentiaTransport> cache = pipe.getAcceptorCache();
            for (Direction side : Direction.values()) {
                if (pipe.isConnectionType(side, ConnectionType.PULL)) {
                    continue;
                }
                IEssentiaTransport acceptor = cache.getConnectedAcceptor(side);
                if (acceptor == null) {
                    continue;
                }
                if (acceptor.spaceFor(type, side.getOpposite()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int addEssentia(@Nullable Holder<IAspect> type, int count) {
        return addEssentia(type, count, null);
    }

    public int addEssentia(@Nullable Holder<IAspect> type, int count, @Nullable TransactionContext transaction) {
        int added = container.insert(type, count, transaction);
        if (added > 0) {
            onContentsChanged();
        }
        return added;
    }

    public int takeEssentia(@Nullable Holder<IAspect> type, int count) {
        return takeEssentia(type, count, null);
    }

    public int takeEssentia(@Nullable Holder<IAspect> type, int count, @Nullable TransactionContext transaction) {
        int removed = container.extract(type, count, transaction);
        if (removed > 0) {
            onContentsChanged();
        }
        return removed;
    }

    public void removeEssentiaAmount(int amount) {
        removeEssentiaAmount(amount, null);
    }

    public void removeEssentiaAmount(int amount, @Nullable TransactionContext transaction) {
        if (amount <= 0 || container.isEmpty()) {
            return;
        }
        Holder<IAspect> type = container.resource();
        int left = Math.max(0, container.amountAsInt() - amount);
        container.setContents(type, left, transaction);
        onContentsChanged();
    }

    private int maxAmount() {
        return (int) Math.clamp(getCapacity(), 0L, Integer.MAX_VALUE);
    }

    @Override
    public @NonNull Component getTextComponent() {
        return Component.translatable("network.mekanismthaumaturge.essentia");
    }
}
