package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.lazycat.datamap.LazyCatDataMapProvider;
import com.blackmoss.lazycat.datamap.mekanismthaumaturge.ChemicalAspectDataMapBuilder;
import com.blackmoss.lazycat.datamap.mekanismthaumaturge.FluidAspectDataMapBuilder;
import com.blackmoss.lazycat.datamap.thaumaturge.BaseAspectBuilder;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTChemicals;
import com.leclowndu93150.thaumaturge.api.aspect.TTAspects;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public final class ModDataMapProvider extends LazyCatDataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, MekanismThaumaturge.MODID);
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
                .aspect(TTAspects.PRAECANTATIO, 5)
                .aspect(TTAspects.POTENTIA, 5)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.REDSTONE.getKey())
                .aspect(TTAspects.POTENTIA)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.DIAMOND.getKey())
                .aspect(TTAspects.VITREUS, 15)
                .aspect(TTAspects.DESIDERIUM, 15)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.CARBON.getKey())
                .aspect(TTAspects.POTENTIA)
                .aspect(TTAspects.IGNIS)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.GOLD.getKey())
                .aspect(TTAspects.METALLUM)
                .aspect(TTAspects.DESIDERIUM)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.BIO.getKey())
                .volume(5)
                .aspect(TTAspects.HERBA)
                .save();

        new ChemicalAspectDataMapBuilder(context, MekanismChemicals.FUNGI.getKey())
                .aspect(TTAspects.HERBA)
                .aspect(TTAspects.TENEBRAE, 3)
                .save();
    }
}
