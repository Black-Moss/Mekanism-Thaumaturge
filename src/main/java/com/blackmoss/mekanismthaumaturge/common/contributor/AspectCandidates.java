package com.blackmoss.mekanismthaumaturge.common.contributor;

import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectIndex;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AspectCandidates {
    private static final float INGREDIENT_SHARE = 0.75F;
    private static final float ROUNDED_UP_FROM = 0.75F;
    private static final int MIN_OUTPUT_COUNT = 1;

    public static List<ItemStack> representations(ItemStackIngredient input) {
        Objects.requireNonNull(input, "input");
        return input.getRepresentations(ContextMap.EMPTY);
    }

    public static List<ChemicalStack> representations(ChemicalStackIngredient input) {
        Objects.requireNonNull(input, "input");
        return input.getRepresentations(ContextMap.EMPTY);
    }

    public static List<FluidStack> representations(FluidStackIngredient input) {
        Objects.requireNonNull(input, "input");
        return input.getRepresentations(ContextMap.EMPTY);
    }

    public static void add(
            Map<Item, List<AspectCandidate>> candidates,
            List<ItemStackTemplate> outputs,
            List<ItemStack> itemInputs,
            List<ChemicalStack> chemicalInputs,
            List<FluidStack> fluidInputs) {
        Objects.requireNonNull(candidates, "candidates");
        for (ItemStackTemplate template : outputs) {
            ItemStack output = template.create();
            if (output.isEmpty()) {
                continue;
            }
            candidates.computeIfAbsent(output.getItem(), _ -> new ArrayList<>())
                    .add(new AspectCandidate(List.copyOf(itemInputs), List.copyOf(chemicalInputs),
                            List.copyOf(fluidInputs), Math.max(MIN_OUTPUT_COUNT, output.getCount())));
        }
    }

    public static AspectList resolve(Item item, AspectCandidate candidate, IAspectIndex partial) {
        AspectList totals = AspectList.EMPTY;
        for (ItemStack input : candidate.itemInputs()) {
            if (!input.isEmpty() && !input.is(item)) {
                totals = totals.add(partial.of(input));
            }
        }
        for (ChemicalStack input : candidate.chemicalInputs()) {
            AspectRatio ratio = input.typeHolder().getData(MTDataMaps.CHEMICAL_ASPECT);
            if (ratio != null) {
                totals = totals.add(ratio.resolve(input.amount()));
            }
        }
        for (FluidStack input : candidate.fluidInputs()) {
            //noinspection deprecation
            AspectRatio ratio = input.getFluid().builtInRegistryHolder().getData(MTDataMaps.FLUID_ASPECT);
            if (ratio != null) {
                totals = totals.add(ratio.resolve(input.getAmount()));
            }
        }
        return scale(totals, candidate.count());
    }

    private static AspectList scale(AspectList totals, int outputCount) {
        int count = Math.max(MIN_OUTPUT_COUNT, outputCount);
        AspectList result = AspectList.EMPTY;
        for (AspectInstance entry : totals.entries()) {
            float value = entry.amount() * INGREDIENT_SHARE / count;
            int amount = value >= ROUNDED_UP_FROM && value < 1.0F ? 1 : (int) value;
            if (amount > 0) {
                result = result.add(entry.aspect(), amount);
            }
        }
        return result;
    }
}
