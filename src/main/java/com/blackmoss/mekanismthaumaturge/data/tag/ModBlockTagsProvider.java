package com.blackmoss.mekanismthaumaturge.data.tag;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MekanismThaumaturge.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(MekanismTags.Blocks.CARDBOARD_BLACKLIST)
                .add(MTBlocks.BASIC_ESSENTIA_PIPE.get())
                .add(MTBlocks.ADVANCED_ESSENTIA_PIPE.get())
                .add(MTBlocks.ELITE_ESSENTIA_PIPE.get())
                .add(MTBlocks.ULTIMATE_ESSENTIA_PIPE.get());

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED)
                .add(MTBlocks.BASIC_ESSENTIA_PIPE.get())
                .add(MTBlocks.ADVANCED_ESSENTIA_PIPE.get())
                .add(MTBlocks.ELITE_ESSENTIA_PIPE.get())
                .add(MTBlocks.ULTIMATE_ESSENTIA_PIPE.get());
    }

    @Override
    public @NonNull String getName() {
        return "Mekanism Thaumaturge Block Tags";
    }
}
