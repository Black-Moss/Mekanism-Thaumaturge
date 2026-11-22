package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import com.blackmoss.mekanismthaumaturge.item.EssentiaTubeItem;
import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;

public final class MTBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismThaumaturge.MODID);

    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaTubeBlockEntity>, BlockItem> BASIC_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlockTypes.BASIC_ESSENTIA_TUBE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaTubeBlockEntity>, BlockItem> ADVANCED_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlockTypes.ADVANCED_ESSENTIA_TUBE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaTubeBlockEntity>, BlockItem> ELITE_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlockTypes.ELITE_ESSENTIA_TUBE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaTubeBlockEntity>, BlockItem> ULTIMATE_ESSENTIA_TUBE =
            registerEssentiaTube(MTBlockTypes.ULTIMATE_ESSENTIA_TUBE);

    private static BlockRegistryObject<BlockLargeTransmitter<EssentiaTubeBlockEntity>, BlockItem> registerEssentiaTube(
            BlockTypeTile<EssentiaTubeBlockEntity> type) {
        ITier tier = type.getOrThrow(AttributeTier.class).tier();
        return BLOCKS.register(tier.getBaseTier().getLowerName() + "_essentia_tube",
                properties -> new BlockLargeTransmitter<>(type, properties), EssentiaTubeItem::new);
    }
}
