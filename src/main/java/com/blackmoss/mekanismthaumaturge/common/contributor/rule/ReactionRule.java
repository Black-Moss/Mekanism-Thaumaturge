package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.api.recipes.basic.BasicPressurizedReactionRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ReactionRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof BasicPressurizedReactionRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof BasicPressurizedReactionRecipe reaction) {
            List<ItemStackTemplate> outputs = reaction.getOutputDefinition().stream()
                    .map(PressurizedReactionRecipe.PressurizedReactionRecipeOutput::item)
                    .filter(Objects::nonNull)
                    .toList();
            AspectCandidates.add(candidates, outputs,
                    AspectCandidates.representations(reaction.getInputSolid()),
                    AspectCandidates.representations(reaction.getInputChemical()),
                    AspectCandidates.representations(reaction.getInputFluid()));
        }
    }
}
