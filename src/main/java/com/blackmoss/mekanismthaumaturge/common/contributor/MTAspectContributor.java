package com.blackmoss.mekanismthaumaturge.common.contributor;

import com.blackmoss.mekanismthaumaturge.common.contributor.rule.*;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectIndex;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectRecipeContributor;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

public final class MTAspectContributor implements IAspectRecipeContributor {
    private static final List<AspectRecipeRule> RULES = List.of(
            new CrushingRule(),
            new EnrichingRule(),
            new SmeltingRule(),
            new CompressingRule(),
            new PurifyingRule(),
            new InjectingRule(),
            new MetallurgicInfusingRule(),
            new PaintingRule(),
            new NucleosynthesizingRule(),
            new CombiningRule(),
            new CrystallizingRule(),
            new ReactionRule(),
            new SawingRule(),
            new NutritionalLiquificationRule()
    );

    private Map<Item, List<AspectCandidate>> candidates = Map.of();

    @Override
    public void beginBuild(RecipeManager recipes, HolderLookup.Provider registries) {
        Objects.requireNonNull(recipes, "recipes");
        Objects.requireNonNull(registries, "registries");
        Map<Item, List<AspectCandidate>> map = new HashMap<>();
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            for (AspectRecipeRule rule : RULES) {
                if (rule.supports(holder.value())) {
                    rule.collect(holder.value(), map);
                    break;
                }
            }
        }
        for (AspectRecipeRule rule : RULES) {
            rule.collectDynamic(registries, map);
        }
        candidates = map;
    }

    @Override
    public Optional<AspectList> derive(
            Item item,
            RecipeManager recipes,
            HolderLookup.Provider registries,
            IAspectIndex partial) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(partial, "partial");
        List<AspectCandidate> pool = candidates.get(item);
        if (pool == null || pool.isEmpty()) {
            return Optional.empty();
        }
        return pool.stream()
                .map(candidate -> AspectCandidates.resolve(item, candidate, partial))
                .filter(aspects -> !aspects.isEmpty())
                .min(Comparator.comparingInt(AspectList::totalAmount));
    }
}
