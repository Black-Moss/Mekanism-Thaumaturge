package com.blackmoss.mekanismthaumaturge.client;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.client.render.essentia.RenderEssentiaTube;
import com.blackmoss.mekanismthaumaturge.registry.MTBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MekanismThaumaturge.MODID, value = Dist.CLIENT)
public class MekanismThaumaturgeClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MTBlockEntities.BASIC_ESSENTIA_TUBE.get(), RenderEssentiaTube::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ADVANCED_ESSENTIA_TUBE.get(), RenderEssentiaTube::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ELITE_ESSENTIA_TUBE.get(), RenderEssentiaTube::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ULTIMATE_ESSENTIA_TUBE.get(), RenderEssentiaTube::new);
    }
}
