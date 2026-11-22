package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicSmeltingRecipe;
import mekanism.common.recipe.WrappedSmelterRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public final class SmeltingRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicSmeltingRecipe || recipe instanceof WrappedSmelterRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicSmeltingRecipe smelting) {
            AspectCandidates.add(candidates, smelting.getOutputDefinition(),
                    AspectCandidates.representations(smelting.getInput()), List.of(), List.of());
        } else if (recipe instanceof WrappedSmelterRecipe smelting) {
            AspectCandidates.add(candidates, smelting.getOutputDefinition(),
                    AspectCandidates.representations(smelting.getInput()), List.of(), List.of());
        }
    }
}
