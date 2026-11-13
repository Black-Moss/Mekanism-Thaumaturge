package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCnProvider extends LanguageProvider {
    public ZhCnProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.mekanismthaumaturge", "通用神秘");

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "基础源质管道");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "高级源质管道");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "精英源质管道");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "终极源质管道");

        add("description.mekanismthaumaturge.essentia_tube", "在相连的方块之间传输源质。");
        add("transmission.mekanismthaumaturge.essentia", "源质");
        add("network.mekanismthaumaturge.essentia", "源质网络");
    }
}
