package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsProvider extends LanguageProvider {
    public EnUsProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.mekanismthaumaturge", "Mekanism Thaumaturge");
    }
}
