package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = MekanismThaumaturge.MODID)
public final class MTDataMaps {
    public static final DataMapType<Chemical, AspectRatio> CHEMICAL_ASPECT =
            DataMapType.builder(
                            MekanismThaumaturge.identifier("chemical_aspect"),
                            MekanismAPI.CHEMICAL_REGISTRY_NAME,
                            AspectRatio.CODEC)
                    .synced(AspectRatio.CODEC, false)
                    .build();

    public static final DataMapType<Fluid, AspectRatio> FLUID_ASPECT =
            DataMapType.builder(
                            MekanismThaumaturge.identifier("fluid_aspect"),
                            Registries.FLUID,
                            AspectRatio.CODEC)
                    .synced(AspectRatio.CODEC, false)
                    .build();

    @SubscribeEvent
    public static void onRegister(RegisterDataMapTypesEvent event) {
        event.register(CHEMICAL_ASPECT);
        event.register(FLUID_ASPECT);
    }
}
