package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaPipeBlockEntity;
import com.blackmoss.mekanismthaumaturge.item.EssentiaPipeItem;
import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;

public final class MTBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismThaumaturge.MODID);

    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaPipeBlockEntity>, BlockItem> BASIC_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlockTypes.BASIC_ESSENTIA_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaPipeBlockEntity>, BlockItem> ADVANCED_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlockTypes.ADVANCED_ESSENTIA_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaPipeBlockEntity>, BlockItem> ELITE_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlockTypes.ELITE_ESSENTIA_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<EssentiaPipeBlockEntity>, BlockItem> ULTIMATE_ESSENTIA_PIPE =
            registerEssentiaPipe(MTBlockTypes.ULTIMATE_ESSENTIA_PIPE);

    private static BlockRegistryObject<BlockLargeTransmitter<EssentiaPipeBlockEntity>, BlockItem> registerEssentiaPipe(
            BlockTypeTile<EssentiaPipeBlockEntity> type) {
        ITier tier = type.getOrThrow(AttributeTier.class).tier();
        return BLOCKS.register(tier.getBaseTier().getLowerName() + "_essentia_pipe",
                properties -> new BlockLargeTransmitter<>(type, properties), EssentiaPipeItem::new);
    }
}
