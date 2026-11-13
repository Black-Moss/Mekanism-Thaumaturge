package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsProvider extends LanguageProvider {
    public EnUsProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.mekanismthaumaturge", "Mekanism Thaumaturge");

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "Basic Essentia Tube");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "Advanced Essentia Tube");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "Elite Essentia Tube");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "Ultimate Essentia Tube");

        add("description.mekanismthaumaturge.essentia_tube", "Transports essentia between connected blocks.");
        add("transmission.mekanismthaumaturge.essentia", "Essentia");
        add("network.mekanismthaumaturge.essentia", "Essentia Network");
    }
}
