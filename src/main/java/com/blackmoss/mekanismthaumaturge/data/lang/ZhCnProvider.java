package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCnProvider extends LanguageProvider {
    public ZhCnProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.mekanismthaumaturge", "通用神秘");
    }
}
