package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.lazycat.lang.LazyCatLanguageProvider;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTCreativeModeTabs;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import net.minecraft.data.PackOutput;

public class EnUsProvider extends LazyCatLanguageProvider {
    public EnUsProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addCreativeModeTab(MTCreativeModeTabs.MEKANISM_THAUMATURGE.get(), "Mekanism Thaumaturge");
        jade();
        gui();
        configuration();

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "Basic Essentia Tube");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "Advanced Essentia Tube");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "Elite Essentia Tube");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "Ultimate Essentia Tube");

        addItem(MTItems.THAUMSTEEL, "Thaumsteel");

        addAspect(MTAspects.ELECTRICA,
                "Electrica",
                "Electric, Energy, Electricity",
                "Electricity thing");

        add("description.mekanismthaumaturge.essentia_tube", "Transports essentia between connected blocks.");
        add("transmission.mekanismthaumaturge.essentia", "Essentia");
        add("network.mekanismthaumaturge.essentia", "Essentia Network");

        add("chemical.mekanismthaumaturge.mundus", "Mundus");
    }

    private void jade() {
        add("config.jade.plugin_mekanismthaumaturge.essentia_tube", "Essentia Tube");
    }

    private void gui() {
        add("gui.mekanismthaumaturge.essentia", "Essentia: %1$s");
        add("gui.mekanismthaumaturge.essentia_empty", "None");
        add("gui.mekanismthaumaturge.essentia_local", "Local buffer: %1$s");
        add("gui.mekanismthaumaturge.essentia_no_network", " (not in a network)");
        add("gui.mekanismthaumaturge.essentia_amount", "%1$s: %2$s");
        add("gui.mekanismthaumaturge.essentia_narration", "Essentia");
    }

    private void configuration() {
        addConfiguration("replace_essentia_display_format", "Replace Essentia Display Format");
        addConfiguration("replace_essentia_display_format.tooltip", "When true, Replace Thaumaturge's own essentia display with a Mekanism Thaumaturge style");
    }
}
