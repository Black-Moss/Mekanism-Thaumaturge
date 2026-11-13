package com.blackmoss.mekanismthaumaturge.data.tag;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MekanismThaumaturge.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {

    }

    @Override
    public @NonNull String getName() {
        return "Mekanism Thaumaturge Block Tags";
    }
}
