package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.block.EssentiaPipeBlockEntity;
import com.blackmoss.mekanismthaumaturge.common.MekanismThaumaturgeLang;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.TubeTier;

import java.util.function.Supplier;

public final class MTBlockTypes {
    public static final BlockTypeTile<EssentiaPipeBlockEntity> BASIC_ESSENTIA_PIPE =
            createEssentiaPipe(TubeTier.BASIC, () -> MTBlockEntities.BASIC_ESSENTIA_PIPE);
    public static final BlockTypeTile<EssentiaPipeBlockEntity> ADVANCED_ESSENTIA_PIPE =
            createEssentiaPipe(TubeTier.ADVANCED, () -> MTBlockEntities.ADVANCED_ESSENTIA_PIPE);
    public static final BlockTypeTile<EssentiaPipeBlockEntity> ELITE_ESSENTIA_PIPE =
            createEssentiaPipe(TubeTier.ELITE, () -> MTBlockEntities.ELITE_ESSENTIA_PIPE);
    public static final BlockTypeTile<EssentiaPipeBlockEntity> ULTIMATE_ESSENTIA_PIPE =
            createEssentiaPipe(TubeTier.ULTIMATE, () -> MTBlockEntities.ULTIMATE_ESSENTIA_PIPE);

    private static BlockTypeTile<EssentiaPipeBlockEntity> createEssentiaPipe(
            TubeTier tier,
            Supplier<TileEntityTypeRegistryObject<EssentiaPipeBlockEntity>> tile) {
        return BlockTileBuilder.createBlock(tile, MekanismThaumaturgeLang.DESCRIPTION_ESSENTIA_PIPE)
                .with(new AttributeTier<>(tier))
                .build();
    }
}
