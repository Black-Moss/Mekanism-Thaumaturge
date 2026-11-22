package com.blackmoss.mekanismthaumaturge.common.contributor;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record AspectCandidate(
        List<ItemStack> itemInputs,
        List<ChemicalStack> chemicalInputs,
        List<FluidStack> fluidInputs,
        int count) {
}
