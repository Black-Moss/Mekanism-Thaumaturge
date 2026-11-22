package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.basic.BasicChemicalCrystallizerRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public final class CrystallizingRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicChemicalCrystallizerRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicChemicalCrystallizerRecipe crystallizing) {
            AspectCandidates.add(candidates, crystallizing.getOutputDefinition(), List.of(),
                    AspectCandidates.representations(crystallizing.getInput()), List.of());
        }
    }
}
