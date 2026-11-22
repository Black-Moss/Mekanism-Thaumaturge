package com.blackmoss.mekanismthaumaturge.common.contributor;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public interface AspectRecipeRule {
    boolean supports(Recipe<?> recipe);

    void collect(Recipe<?> recipe, Map<Item, List<AspectCandidate>> candidates);

    default void collectDynamic(HolderLookup.Provider registries, Map<Item, List<AspectCandidate>> candidates) {
    }
}
