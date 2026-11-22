package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaPipeBlockEntity;
import com.leclowndu93150.thaumaturge.api.essentia.EssentiaCapabilities;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class MTBlockEntities {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MekanismThaumaturge.MODID);

    public static final TileEntityTypeRegistryObject<EssentiaPipeBlockEntity> BASIC_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlocks.BASIC_ESSENTIA_PIPE);
    public static final TileEntityTypeRegistryObject<EssentiaPipeBlockEntity> ADVANCED_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlocks.ADVANCED_ESSENTIA_PIPE);
    public static final TileEntityTypeRegistryObject<EssentiaPipeBlockEntity> ELITE_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlocks.ELITE_ESSENTIA_PIPE);
    public static final TileEntityTypeRegistryObject<EssentiaPipeBlockEntity> ULTIMATE_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlocks.ULTIMATE_ESSENTIA_PIPE);

    private static TileEntityTypeRegistryObject<EssentiaPipeBlockEntity> registerEssentiaPipe(DeferredHolder<Block, ?> block) {
        return TILE_ENTITY_TYPES.builder(block, (pos, state) -> new EssentiaPipeBlockEntity(block, pos, state))
                .serverTicker(TileEntityTransmitter::tickServer)
                .withSimple(EssentiaCapabilities.TRANSPORT)
                .with(Capabilities.CONFIGURABLE, TileEntityTransmitter.CONFIGURABLE_PROVIDER)
                .withSimple(Capabilities.ALLOY_INTERACTION)
                .build();
    }
}
