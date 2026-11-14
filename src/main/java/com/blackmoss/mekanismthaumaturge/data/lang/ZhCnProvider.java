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
        jade();
        gui();

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "基础源质管道");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "高级源质管道");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "精英源质管道");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "终极源质管道");

        add("description.mekanismthaumaturge.essentia_tube", "在相连的方块之间传输源质。");
        add("transmission.mekanismthaumaturge.essentia", "源质");
        add("network.mekanismthaumaturge.essentia", "源质网络");
    }

    private void jade() {
        add("config.jade.plugin_mekanismthaumaturge.essentia_tube", "源质管道");
    }

    private void gui() {
        add("gui.mekanismthaumaturge.essentia", "源质：%1$s");
        add("gui.mekanismthaumaturge.essentia_empty", "无");
        add("gui.mekanismthaumaturge.essentia_local", "本地缓存：%1$d");
        add("gui.mekanismthaumaturge.essentia_no_network", "（未接入网络）");
        add("gui.mekanismthaumaturge.essentia_amount", "%1$d / %2$d");
        add("gui.mekanismthaumaturge.essentia_narration", "源质");
    }
}
