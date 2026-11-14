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
        jade();
        gui();

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "Basic Essentia Tube");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "Advanced Essentia Tube");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "Elite Essentia Tube");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "Ultimate Essentia Tube");

        add("description.mekanismthaumaturge.essentia_tube", "Transports essentia between connected blocks.");
        add("transmission.mekanismthaumaturge.essentia", "Essentia");
        add("network.mekanismthaumaturge.essentia", "Essentia Network");
    }

    private void jade() {
        add("config.jade.plugin_mekanismthaumaturge.essentia_tube", "Essentia Tube");
    }

    private void gui() {
        add("gui.mekanismthaumaturge.essentia", "Essentia: %1$s");
        add("gui.mekanismthaumaturge.essentia_empty", "None");
        add("gui.mekanismthaumaturge.essentia_local", "Local buffer: %1$d");
        add("gui.mekanismthaumaturge.essentia_no_network", " (not in a network)");
        add("gui.mekanismthaumaturge.essentia_amount", "%1$d / %2$d");
        add("gui.mekanismthaumaturge.essentia_narration", "Essentia");
    }
}
