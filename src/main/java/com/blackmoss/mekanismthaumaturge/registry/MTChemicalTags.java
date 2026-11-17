package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.tags.TagKey;

public final class MTChemicalTags {
    public static final TagKey<Chemical> MUNDUS = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME,
            MekanismThaumaturge.identifier("mundus"));
}
