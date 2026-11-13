package com.blackmoss.mekanismthaumaturge.common.essentia;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EssentiaNetwork extends DynamicBufferedNetwork<IEssentiaTransport, EssentiaNetwork, EssentiaBuffer, EssentiaTube> {
    @Nullable
    private Holder<IAspect> essentiaType;
    private int essentiaAmount;

    public EssentiaNetwork(@NonNull UUID networkID) {
        super(networkID);
    }

    public EssentiaNetwork(@NonNull Collection<EssentiaNetwork> networks) {
        this(UUID.randomUUID());
        adoptAllAndRegister(networks);
    }

    @Override
    protected float computeContentScale() {
        long capacity = getCapacity();
        return capacity <= 0 ? 0 : (float) Math.min(1.0D, essentiaAmount / (double) capacity);
    }

    @Override
    protected void forceScaleUpdate() {
        currentScale = computeContentScale();
    }

    @Override
    public @NonNull EssentiaBuffer getBuffer() {
        return new EssentiaBuffer(essentiaType, essentiaAmount);
    }

    @Override
    public void absorbBuffer(@NonNull EssentiaTube transmitter, @NonNull TransactionContext transaction) {
        EssentiaBuffer share = transmitter.releaseShare();
        addEssentia(share.type(), share.amount());
    }

    @Override
    public void clampBuffer() {
        essentiaAmount = Math.clamp(essentiaAmount, 0, maxAmount());
        if (essentiaAmount == 0) {
            essentiaType = null;
        }
    }

    @Override
    public @NonNull List<EssentiaTube> adoptTransmittersAndAcceptorsFrom(@NonNull EssentiaNetwork net) {
        List<EssentiaTube> transmitters = super.adoptTransmittersAndAcceptorsFrom(net);
        Holder<IAspect> type = net.essentiaType;
        if (type != null && net.essentiaAmount > 0) {
            addEssentia(type, net.takeEssentia(type, net.essentiaAmount));
        }
        clampBuffer();
        return transmitters;
    }

    @Override
    protected void updateSaveShares(@Nullable EssentiaTube triggerTransmitter, @NonNull TransactionContext transaction) {
        super.updateSaveShares(triggerTransmitter, transaction);
        Collection<EssentiaTube> transmitters = getTransmitters();
        for (EssentiaTube tube : transmitters) {
            tube.clearSaveShare();
        }
        Holder<IAspect> type = essentiaType;
        int remaining = essentiaAmount;
        if (type == null || remaining <= 0) {
            return;
        }
        for (EssentiaTube tube : transmitters) {
            if (remaining <= 0) {
                break;
            }
            int share = Math.min(remaining, tube.getSaveShareCapacity());
            if (share > 0) {
                tube.setSaveShare(new EssentiaBuffer(type, share));
                remaining -= share;
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tickEmit();
    }

    private void tickEmit() {
        Holder<IAspect> type = essentiaType;
        int remaining = essentiaAmount;
        if (type == null || remaining <= 0) {
            return;
        }
        for (Map<Direction, IEssentiaTransport> acceptors : acceptorCache.getAcceptorValues()) {
            for (Map.Entry<Direction, IEssentiaTransport> entry : acceptors.entrySet()) {
                Direction side = entry.getKey();
                IEssentiaTransport acceptor = entry.getValue();
                if (side != null && acceptor != null && remaining > 0) {
                    int accepted = acceptor.addEssentia(type, remaining, side);
                    remaining -= Math.clamp(remaining, 0, accepted);
                }
            }
        }
        if (remaining != essentiaAmount) {
            essentiaAmount = remaining;
            if (essentiaAmount <= 0) {
                essentiaType = null;
            }
            onContentsChanged();
        }
    }

    public int addEssentia(@Nullable Holder<IAspect> type, int count) {
        if (type == null || count <= 0) {
            return 0;
        }
        if (essentiaType != null && !essentiaType.equals(type)) {
            return 0;
        }
        int accepted = Math.clamp(count, 0, maxAmount() - essentiaAmount);
        if (accepted > 0) {
            essentiaType = type;
            essentiaAmount += accepted;
            onContentsChanged();
        }
        return accepted;
    }

    public int takeEssentia(@Nullable Holder<IAspect> type, int count) {
        if (count <= 0 || essentiaType == null || !essentiaType.equals(type)) {
            return 0;
        }
        int removed = Math.min(count, essentiaAmount);
        essentiaAmount -= removed;
        if (essentiaAmount == 0) {
            essentiaType = null;
        }
        if (removed > 0) {
            onContentsChanged();
        }
        return removed;
    }

    private int maxAmount() {
        return (int) Math.clamp(getCapacity(), 0L, Integer.MAX_VALUE);
    }

    @Override
    public @NonNull Component getTextComponent() {
        return Component.translatable("network.mekanismthaumaturge.essentia");
    }
}
