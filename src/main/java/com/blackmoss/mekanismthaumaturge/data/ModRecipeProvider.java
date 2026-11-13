package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.leclowndu93150.thaumaturge.data.recipe.builders.CrucibleRecipeBuilder;
import com.leclowndu93150.thaumaturge.data.recipe.builders.InfusionRecipeBuilder;
import com.leclowndu93150.thaumaturge.data.recipe.builders.workbench.ArcaneWorkbenchShapedRecipeBuilder;
import com.leclowndu93150.thaumaturge.data.recipe.builders.workbench.ArcaneWorkbenchShapelessRecipeBuilder;
import com.leclowndu93150.thaumaturge.registry.TTDataComponents;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider {
    private final HolderLookup.Provider lookupProvider;
    private final HolderGetter<IAspect> aspects;

    private ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
        this.lookupProvider = provider;
        this.aspects = registries.lookupOrThrow(IAspect.REGISTRY_KEY);
    }

    private static ResearchGate mtGate(String path) {
        return new ResearchGate(MekanismThaumaturge.identifier(path), Optional.empty(), false);
    }

    private static ResearchGate tcGate(String path) {
        return new ResearchGate(TTIds.rl(path), Optional.empty(), false);
    }

    @Override
    protected void buildRecipes() {
        essentiaTube();
    }

    private void essentiaTube() {
        arcaneShaped(MTBlocks.BASIC_ESSENTIA_TUBE.asItem(), 8, 5)
                .pattern("SMS")
                .define('S', MekanismTags.Items.INGOTS_STEEL)
                .define('M', TTItems.SALIS_MUNDUS.get())
                .save(output);
        arcaneShaped(MTBlocks.ADVANCED_ESSENTIA_TUBE.asItem(), 8, 5)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PPP")
                .define('P', MTBlocks.BASIC_ESSENTIA_TUBE.asItem())
                .define('A', MekanismTags.Items.ALLOYS_INFUSED)
                .save(output);
        arcaneShaped(MTBlocks.ELITE_ESSENTIA_TUBE.asItem(), 8, 5)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PPP")
                .define('P', MTBlocks.ADVANCED_ESSENTIA_TUBE.asItem())
                .define('A', MekanismTags.Items.ALLOYS_REINFORCED)
                .save(output);
        arcaneShaped(MTBlocks.ULTIMATE_ESSENTIA_TUBE.asItem(), 8, 5)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PPP")
                .define('P', MTBlocks.ELITE_ESSENTIA_TUBE.asItem())
                .define('A', MekanismTags.Items.ALLOYS_ATOMIC)
                .save(output);
    }


    private CrucibleRecipeBuilder crucible(ItemLike result, ItemLike catalyst) {
        return (CrucibleRecipeBuilder) new CrucibleRecipeBuilder(
                aspects,
                RecipeCategory.MISC,
                new ItemStackTemplate(result.asItem()),
                Ingredient.of(catalyst))
                .unlockedBy("has", this.has(result.asItem()));
    }

    private HolderSet<Item> tag(String space, String path) {
        return this.lookupProvider.getOrThrow(TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath(
                        space, path)));
    }

    private Ingredient crystal(ResourceKey<IAspect> aspect) {
        return DataComponentIngredient.of(
                TTDataComponents.CRYSTAL_ASPECT.get(),
                new AspectInstance(aspects.getOrThrow(aspect), 1),
                TTItems.ESSENTIA_CRYSTAL.get());
    }

    private Ingredient commonTag(String path) {
        return Ingredient.of(tag("c", path));
    }

    private InfusionRecipeBuilder infusion(ItemLike result, ItemLike catalyst) {
        return (InfusionRecipeBuilder) new InfusionRecipeBuilder(
                aspects,
                RecipeCategory.MISC,
                new ItemStackTemplate(result.asItem()),
                Ingredient.of(catalyst))
                .unlockedBy("has", this.has(result));
    }


    private CrucibleRecipeBuilder crucible(ItemLike result, RecipeCategory category, ItemLike catalyst) {
        return (CrucibleRecipeBuilder) new CrucibleRecipeBuilder(
                aspects,
                category,
                new ItemStackTemplate(result.asItem()),
                Ingredient.of(catalyst))
                .unlockedBy("has", this.has(result));
    }

    private ArcaneWorkbenchShapelessRecipeBuilder arcaneShapeless(ItemLike result, int vis) {
        return (ArcaneWorkbenchShapelessRecipeBuilder) new ArcaneWorkbenchShapelessRecipeBuilder(
                RecipeCategory.MISC,
                new ItemStackTemplate(result.asItem()),
                aspects,
                vis,
                registries.lookupOrThrow(Registries.ITEM))
                .unlockedBy("has", this.has(result.asItem()));

    }

    private ArcaneWorkbenchShapedRecipeBuilder arcaneShaped(ItemLike result, int vis) {
        return arcaneShaped(result, 1, vis);
    }

    private ArcaneWorkbenchShapedRecipeBuilder arcaneShaped(ItemLike result, int count, int vis) {
        return (ArcaneWorkbenchShapedRecipeBuilder) new ArcaneWorkbenchShapedRecipeBuilder(
                RecipeCategory.MISC,
                new ItemStackTemplate(result.asItem(), count),
                this.items,
                aspects,
                vis)
                .unlockedBy("has", this.has(result.asItem()));
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return "Mekanism Thaumaturge Recipes";
        }
    }
}