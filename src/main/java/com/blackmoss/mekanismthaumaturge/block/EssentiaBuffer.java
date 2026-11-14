package com.blackmoss.mekanismthaumaturge.block;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import org.jspecify.annotations.Nullable;

public record EssentiaBuffer(@Nullable Holder<IAspect> type, int amount) {
    public static final EssentiaBuffer EMPTY = new EssentiaBuffer(null, 0);

    public EssentiaBuffer {
        if (type == null || amount <= 0) {
            type = null;
            amount = 0;
        }
    }

    public boolean isEmpty() {
        return amount == 0;
    }
}
