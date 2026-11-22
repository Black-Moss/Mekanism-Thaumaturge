package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicCombinerRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CombiningRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicCombinerRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicCombinerRecipe combining) {
            List<ItemStack> itemInputs = new ArrayList<>(
                    AspectCandidates.representations(combining.getMainInput()));
            itemInputs.addAll(AspectCandidates.representations(combining.getExtraInput()));
            AspectCandidates.add(candidates, combining.getOutputDefinition(), itemInputs, List.of(), List.of());
        }
    }
}
