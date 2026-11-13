package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.common.MekanismThaumaturgeLang;
import com.blackmoss.mekanismthaumaturge.common.essentia.TileEntityEssentiaTube;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.TubeTier;

import java.util.function.Supplier;

public final class MTBlockTypes {
    public static final BlockTypeTile<TileEntityEssentiaTube> BASIC_ESSENTIA_TUBE =
            createEssentiaTube(TubeTier.BASIC, () -> MTBlockEntities.BASIC_ESSENTIA_TUBE);
    public static final BlockTypeTile<TileEntityEssentiaTube> ADVANCED_ESSENTIA_TUBE =
            createEssentiaTube(TubeTier.ADVANCED, () -> MTBlockEntities.ADVANCED_ESSENTIA_TUBE);
    public static final BlockTypeTile<TileEntityEssentiaTube> ELITE_ESSENTIA_TUBE =
            createEssentiaTube(TubeTier.ELITE, () -> MTBlockEntities.ELITE_ESSENTIA_TUBE);
    public static final BlockTypeTile<TileEntityEssentiaTube> ULTIMATE_ESSENTIA_TUBE =
            createEssentiaTube(TubeTier.ULTIMATE, () -> MTBlockEntities.ULTIMATE_ESSENTIA_TUBE);

    private static BlockTypeTile<TileEntityEssentiaTube> createEssentiaTube(TubeTier tier,
                                                                            Supplier<TileEntityTypeRegistryObject<TileEntityEssentiaTube>> tile) {
        return BlockTileBuilder.createBlock(tile, MekanismThaumaturgeLang.DESCRIPTION_ESSENTIA_TUBE)
                .with(new AttributeTier<>(tier))
                .build();
    }
}
