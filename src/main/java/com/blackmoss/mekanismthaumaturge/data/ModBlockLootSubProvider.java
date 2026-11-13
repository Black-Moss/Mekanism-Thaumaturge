package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider {

    public ModBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
        return MTBlocks.BLOCKS.getPrimaryEntries().stream().map(holder -> (Block) holder.value()).toList();
    }

    @Override
    protected void generate() {
        for (DeferredHolder<Block, ?> holder : MTBlocks.BLOCKS.getPrimaryEntries()) {
            dropSelf(holder.value());
        }
    }
}
