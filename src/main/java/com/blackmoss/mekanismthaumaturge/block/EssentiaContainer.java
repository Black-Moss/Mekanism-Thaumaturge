package com.blackmoss.mekanismthaumaturge.block;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.function.LongSupplier;

public class EssentiaContainer extends SnapshotJournal<EssentiaBuffer> {
    private final LongSupplier capacitySupplier;
    private EssentiaBuffer current = EssentiaBuffer.EMPTY;

    public EssentiaContainer(LongSupplier capacitySupplier) {
        this.capacitySupplier = capacitySupplier;
    }

    public long capacity() {
        return capacitySupplier.getAsLong();
    }

    public EssentiaBuffer asStack() {
        return current;
    }

    @Nullable
    public Holder<IAspect> resource() {
        return current.type();
    }

    public int amountAsInt() {
        return current.amount();
    }

    public long amountAsLong() {
        return current.amount();
    }

    public boolean isEmpty() {
        return current.isEmpty();
    }

    @Override
    protected EssentiaBuffer createSnapshot() {
        return current;
    }

    @Override
    protected void revertToSnapshot(EssentiaBuffer snapshot) {
        current = snapshot;
    }

    public int insert(@Nullable Holder<IAspect> type, int amount, @Nullable TransactionContext transaction) {
        if (type == null || amount <= 0) {
            return 0;
        }
        if (current.type() != null && !current.isEmpty() && !current.type().equals(type)) {
            return 0;
        }
        int capacity = (int) Math.clamp(capacity(), 0L, Integer.MAX_VALUE);
        int toAdd = Math.clamp(capacity - current.amount(), 0, amount);
        if (toAdd <= 0) {
            return 0;
        }
        setContents(new EssentiaBuffer(type, current.amount() + toAdd), transaction);
        return toAdd;
    }

    public int extract(@Nullable Holder<IAspect> type, int amount, @Nullable TransactionContext transaction) {
        if (type == null || amount <= 0) {
            return 0;
        }
        if (current.type() != null && (current.isEmpty() || !current.type().equals(type))) {
            return 0;
        }
        int toRemove = Math.min(amount, current.amount());
        if (toRemove <= 0) {
            return 0;
        }
        setContents(new EssentiaBuffer(type, current.amount() - toRemove), transaction);
        return toRemove;
    }

    public void setContents(@Nullable Holder<IAspect> type, int amount, @Nullable TransactionContext transaction) {
        setContents(type == null || amount <= 0
                ? EssentiaBuffer.EMPTY
                : new EssentiaBuffer(type, amount), transaction);
    }

    public void setContents(EssentiaBuffer target, @Nullable TransactionContext transaction) {
        if (target.equals(current)) {
            return;
        }
        if (transaction == null) {
            current = target;
        } else {
            updateSnapshots(transaction);
            current = target;
        }
    }
}
