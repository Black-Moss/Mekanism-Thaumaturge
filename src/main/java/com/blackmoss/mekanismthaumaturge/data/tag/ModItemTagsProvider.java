package com.blackmoss.mekanismthaumaturge.data.tag;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTItemTags;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MekanismThaumaturge.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(Tags.Items.INGOTS)
                .add(MTItems.THAUMSTEEL.get());
        tag(MTItemTags.INGOTS_THAUMSTEEL)
                .add(MTItems.THAUMSTEEL.get());
    }

    @Override
    public @NonNull String getName() {
        return "Mekanism Thaumaturge Item Tags";
    }
}
