package com.blackmoss.mekanismthaumaturge.client.tooltip;

import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.config.ThaumaturgeClientConfig;
import net.minecraft.client.Minecraft;

public final class PendingAspects {
    private static final ThreadLocal<AspectList> PENDING = new ThreadLocal<>();

    public static void set(AspectList aspects) {
        PENDING.set(aspects);
    }

    public static AspectList take() {
        AspectList aspects = PENDING.get();
        PENDING.remove();
        return aspects;
    }

    public static void clear() {
        PENDING.remove();
    }

    public static boolean shouldHideAspects() {
        boolean shift = Minecraft.getInstance().hasShiftDown();
        return shift == ThaumaturgeClientConfig.showAspectsByDefault();
    }
}
