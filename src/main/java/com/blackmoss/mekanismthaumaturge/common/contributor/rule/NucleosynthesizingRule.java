package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicNucleosynthesizingRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public final class NucleosynthesizingRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicNucleosynthesizingRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicNucleosynthesizingRecipe nucleosynthesizing) {
            AspectCandidates.add(candidates, nucleosynthesizing.getOutputDefinition(),
                    AspectCandidates.representations(nucleosynthesizing.getItemInput()),
                    AspectCandidates.representations(nucleosynthesizing.getChemicalInput()), List.of());
        }
    }
}
