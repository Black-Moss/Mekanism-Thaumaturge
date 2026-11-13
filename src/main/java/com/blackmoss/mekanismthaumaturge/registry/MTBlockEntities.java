package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.common.essentia.TileEntityEssentiaTube;
import com.leclowndu93150.thaumaturge.api.essentia.EssentiaCapabilities;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class MTBlockEntities {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MekanismThaumaturge.MODID);

    public static final TileEntityTypeRegistryObject<TileEntityEssentiaTube> BASIC_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlocks.BASIC_ESSENTIA_TUBE);
    public static final TileEntityTypeRegistryObject<TileEntityEssentiaTube> ADVANCED_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlocks.ADVANCED_ESSENTIA_TUBE);
    public static final TileEntityTypeRegistryObject<TileEntityEssentiaTube> ELITE_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlocks.ELITE_ESSENTIA_TUBE);
    public static final TileEntityTypeRegistryObject<TileEntityEssentiaTube> ULTIMATE_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlocks.ULTIMATE_ESSENTIA_TUBE);

    private static TileEntityTypeRegistryObject<TileEntityEssentiaTube> registerEssentiaTube(DeferredHolder<Block, ?> block) {
        return TILE_ENTITY_TYPES.builder(block, (pos, state) -> new TileEntityEssentiaTube(block, pos, state))
                .serverTicker(TileEntityTransmitter::tickServer)
                .withSimple(EssentiaCapabilities.TRANSPORT)
                .with(Capabilities.CONFIGURABLE, TileEntityTransmitter.CONFIGURABLE_PROVIDER)
                .withSimple(Capabilities.ALLOY_INTERACTION)
                .build();
    }
}
