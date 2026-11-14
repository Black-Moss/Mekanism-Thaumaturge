package com.blackmoss.mekanismthaumaturge.block;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

final class EssentiaTransferJournal extends SnapshotJournal<List<EssentiaTransferJournal.Entry>> {
    private final List<Entry> entries = new ArrayList<>();

    int insert(
            IEssentiaTransport acceptor,
            Holder<IAspect> type,
            int amount,
            Direction side,
            TransactionContext transaction) {
        Entry entry = entry(acceptor, type, side);
        updateSnapshots(transaction);
        int accepted = acceptor.addEssentia(type, amount, side);
        entry.inserted += accepted;
        return accepted;
    }

    int extract(
            IEssentiaTransport acceptor,
            Holder<IAspect> type,
            int amount,
            Direction side,
            TransactionContext transaction) {
        Entry entry = entry(acceptor, type, side);
        updateSnapshots(transaction);
        int removed = acceptor.takeEssentia(type, amount, side);
        entry.extracted += removed;
        return removed;
    }

    private Entry entry(IEssentiaTransport acceptor, Holder<IAspect> type, Direction side) {
        for (Entry entry : entries) {
            if (entry.acceptor == acceptor && entry.side == side && entry.type.equals(type)) {
                return entry;
            }
        }
        Entry entry = new Entry(acceptor, type, side);
        entries.add(entry);
        return entry;
    }

    @Override
    protected List<Entry> createSnapshot() {
        List<Entry> snapshot = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            snapshot.add(new Entry(entry.acceptor, entry.type, entry.side, entry.inserted, entry.extracted));
        }
        return snapshot;
    }

    @Override
    protected void revertToSnapshot(List<Entry> snapshot) {
        if (snapshot != null) {
            for (int i = 0; i < snapshot.size(); i++) {
                Entry saved = snapshot.get(i);
                Entry current = entries.get(i);
                if (current.inserted > saved.inserted) {
                    current.acceptor.takeEssentia(current.type, current.inserted - saved.inserted, current.side);
                }
                if (current.extracted > saved.extracted) {
                    current.acceptor.addEssentia(current.type, current.extracted - saved.extracted, current.side);
                }
            }
        }
        entries.clear();
        if (snapshot != null) {
            entries.addAll(snapshot);
        }
    }

    static final class Entry {
        private final IEssentiaTransport acceptor;
        private final Holder<IAspect> type;
        private final Direction side;
        private int inserted;
        private int extracted;

        Entry(IEssentiaTransport acceptor, Holder<IAspect> type, Direction side) {
            this(acceptor, type, side, 0, 0);
        }

        Entry(IEssentiaTransport acceptor, Holder<IAspect> type, Direction side, int inserted, int extracted) {
            this.acceptor = acceptor;
            this.type = type;
            this.side = side;
            this.inserted = inserted;
            this.extracted = extracted;
        }
    }
}
