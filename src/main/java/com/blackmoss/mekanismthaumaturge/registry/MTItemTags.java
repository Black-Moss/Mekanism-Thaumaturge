package com.blackmoss.mekanismthaumaturge.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class MTItemTags {
    public static final TagKey<Item> INGOTS_THAUMSTEEL = commonTag("ingots/thaumsteel");

    private static TagKey<Item> commonTag(String name) {
        return ItemTags.create(Identifier.fromNamespaceAndPath("c", name));
    }
}
