package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicCompressingRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public final class CompressingRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicCompressingRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicCompressingRecipe compressing) {
            AspectCandidates.add(candidates, compressing.getOutputDefinition(),
                    AspectCandidates.representations(compressing.getItemInput()),
                    AspectCandidates.representations(compressing.getChemicalInput()), List.of());
        }
    }
}
