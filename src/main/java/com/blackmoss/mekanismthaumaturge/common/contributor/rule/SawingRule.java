package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicSawmillRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SawingRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicSawmillRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicSawmillRecipe sawing) {
            List<ItemStackTemplate> outputs = new ArrayList<>(sawing.getMainOutputDefinition());
            outputs.addAll(sawing.getSecondaryOutputDefinition());
            AspectCandidates.add(candidates, outputs,
                    AspectCandidates.representations(sawing.getInput()), List.of(), List.of());
        }
    }
}
