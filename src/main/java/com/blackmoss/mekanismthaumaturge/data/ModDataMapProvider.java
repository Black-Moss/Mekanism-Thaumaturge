package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.lazycat.datamap.LazyCatDataMapProvider;
import com.blackmoss.lazycat.datamap.mekanismthaumaturge.ChemicalAspectDataMapBuilder;
import com.blackmoss.lazycat.datamap.mekanismthaumaturge.FluidAspectDataMapBuilder;
import com.blackmoss.lazycat.datamap.thaumaturge.BaseAspectBuilder;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTChemicals;
import com.leclowndu93150.thaumaturge.api.aspect.TTAspects;
import mekanism.common.registries.MekanismItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public final class ModDataMapProvider extends LazyCatDataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, MekanismThaumaturge.MODID);
    }

    @Override
    protected void buildDataMaps() {
        new BaseAspectBuilder(context,
                MekanismItems.ENERGY_TABLET.asItem())
                .aspect(MTAspects.ELECTRICA)
                .save();

        new FluidAspectDataMapBuilder(context, Fluids.WATER)
                .volume(100)
                .aspect(TTAspects.AQUA)
                .save();

        new ChemicalAspectDataMapBuilder(context, MTChemicals.MUNDUS.getKey())
                .volume(10)
                .aspect(TTAspects.PRAECANTATIO, 5)
                .aspect(TTAspects.POTENTIA, 5)
                .save();
    }
}
