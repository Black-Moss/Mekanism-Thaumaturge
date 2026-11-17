package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.lazycat.recipe.LazyCatRecipeProvider;
import com.blackmoss.lazycat.recipe.mekanism.ItemStackChemicalToItemStackBuilder;
import com.blackmoss.lazycat.recipe.thaumaturge.ArcaneShapedBuilder;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTItemTags;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.leclowndu93150.thaumaturge.registry.TTItemTags;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import mekanism.api.MekanismAPITags;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends LazyCatRecipeProvider {
    private ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output, String space) {
        super(provider, output, space);
    }

    private static ResearchGate mtGate(String path) {
        return new ResearchGate(MekanismThaumaturge.identifier(path), Optional.empty(), false);
    }

    private static ResearchGate tcGate(String path) {
        return new ResearchGate(TTIds.rl(path), Optional.empty(), false);
    }

    @Override
    protected void addRecipes() {
        new ArcaneShapedBuilder(context, MTBlocks.BASIC_ESSENTIA_TUBE, 5)
                .pattern("SMS")
                .define('S', MTItemTags.INGOTS_THAUMSTEEL)
                .define('M', TTItems.SALIS_MUNDUS)
                .count(8)
                .save();
        tube(MTBlocks.ADVANCED_ESSENTIA_TUBE, MTBlocks.BASIC_ESSENTIA_TUBE, MekanismTags.Items.ALLOYS_INFUSED);
        tube(MTBlocks.ELITE_ESSENTIA_TUBE, MTBlocks.ADVANCED_ESSENTIA_TUBE, MekanismTags.Items.ALLOYS_REINFORCED);
        tube(MTBlocks.ULTIMATE_ESSENTIA_TUBE, MTBlocks.ELITE_ESSENTIA_TUBE, MekanismTags.Items.ALLOYS_ATOMIC);

        new ItemStackChemicalToItemStackBuilder(context,
                MTItems.THAUMSTEEL)
                .chemical(MekanismAPITags.Chemicals.CARBON, 10)
                .mainItem(TTItemTags.INGOTS_THAUMIUM)
                .save();
    }

    private void tube(Object result, Object source, Object alloys) {
        new ArcaneShapedBuilder(context, result, 5)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PPP")
                .define('P', source)
                .define('A', alloys)
                .count(8)
                .save();
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new ModRecipeProvider(provider, output, MekanismThaumaturge.MODID);
        }

        @Override
        public @NonNull String getName() {
            return "Mekanism Thaumaturge Recipes";
        }
    }
}