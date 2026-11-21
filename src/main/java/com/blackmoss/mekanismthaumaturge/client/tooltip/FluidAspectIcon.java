package com.blackmoss.mekanismthaumaturge.client.tooltip;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectChipsTooltip;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.mojang.datafixers.util.Either;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.FluidTooltipEvent;

@EventBusSubscriber(modid = MekanismThaumaturge.MODID, value = Dist.CLIENT)
public final class FluidAspectIcon {
    @SubscribeEvent
    public static void onFluidTooltip(FluidTooltipEvent event) {
        PendingAspects.clear();
        var fluidStack = event.getFluidStack();
        if (fluidStack.isEmpty() || PendingAspects.shouldHideAspects()) {
            return;
        }
        //noinspection deprecation
        AspectRatio ratio = fluidStack.getFluid().builtInRegistryHolder().getData(MTDataMaps.FLUID_ASPECT);
        if (ratio == null) {
            return;
        }
        AspectList aspects = ratio.resolve(fluidStack.getAmount());
        if (!aspects.isEmpty()) {
            PendingAspects.set(aspects);
        }
    }

    @SubscribeEvent
    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        AspectList aspects = PendingAspects.take();
        if (aspects == null || aspects.isEmpty()) {
            return;
        }
        event.getTooltipElements().add(Either.right(new AspectChipsTooltip(aspects)));
    }
}
