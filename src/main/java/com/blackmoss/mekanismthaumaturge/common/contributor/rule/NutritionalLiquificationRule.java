package com.blackmoss.mekanismthaumaturge.common.contributor.rule;

import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidate;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectCandidates;
import com.blackmoss.mekanismthaumaturge.common.contributor.AspectRecipeRule;
import mekanism.api.recipes.ItemStackToFluidOptionalItemRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NutritionalLiquificationRule implements AspectRecipeRule {
    @Override
    public boolean supports(Recipe<?> recipe) {
        return recipe instanceof ItemStackToFluidOptionalItemRecipe;
    }

    @Override
    public void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates) {
        if (recipe instanceof ItemStackToFluidOptionalItemRecipe liquifying) {
            List<ItemStackTemplate> outputs = liquifying.getOutputDefinition().stream()
                    .map(ItemStackToFluidOptionalItemRecipe.FluidOptionalItemOutput::optionalItem)
                    .filter(Objects::nonNull)
                    .toList();
            AspectCandidates.add(candidates, outputs,
                    AspectCandidates.representations(liquifying.getInput()), List.of(), List.of());
        }
    }

    @Override
    public void collectDynamic(HolderLookup.Provider registries, Map<Item, List<AspectCandidate>> candidates) {
        Objects.requireNonNull(registries, "registries");
        for (Item item : registries.lookupOrThrow(Registries.ITEM).listElements()
                .map(Holder.Reference::value).toList()) {
            ItemStack input = new ItemStack(item);
            FoodProperties food = input.get(DataComponents.FOOD);
            if (food == null || food.nutrition() <= 0) {
                continue;
            }
            UseRemainder remainder = input.get(DataComponents.USE_REMAINDER);
            if (remainder == null) {
                continue;
            }
            ItemStackTemplate container = remainder.convertInto();
            AspectCandidates.add(candidates, List.of(container), List.of(input), List.of(), List.of());
        }
    }
}
