package com.blackmoss.mekanismthaumaturge.data.tag;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTChemicalTags;
import com.blackmoss.mekanismthaumaturge.registry.MTChemicals;
import mekanism.api.datagen.tag.ChemicalTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class ModChemicalTagsProvider extends ChemicalTagsProvider {
    public ModChemicalTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, MekanismThaumaturge.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(MTChemicalTags.MUNDUS)
                .add(MTChemicals.MUNDUS.get());
    }

    @Override
    public @NonNull String getName() {
        return "Mekanism Thaumaturge Chemical Tags";
    }
}
