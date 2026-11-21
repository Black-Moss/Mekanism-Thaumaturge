package com.blackmoss.mekanismthaumaturge.data.lang;

import com.blackmoss.lazycat.lang.LazyCatLanguageProvider;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTCreativeModeTabs;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import net.minecraft.data.PackOutput;

public class ZhCnProvider extends LazyCatLanguageProvider {
    public ZhCnProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        addCreativeModeTab(MTCreativeModeTabs.MEKANISM_THAUMATURGE.get(), "通用神秘");
        jade();
        gui();

        addBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "基础源质管道");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "高级源质管道");
        addBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "精英源质管道");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "终极源质管道");

        addItem(MTItems.THAUMSTEEL, "神秘钢");

        addAspect(MTAspects.ELECTRICA,
                "电力",
                "电动，电能，电力",
                "有电的东西");

        add("description.mekanismthaumaturge.essentia_tube", "在相连的方块之间传输源质。");
        add("transmission.mekanismthaumaturge.essentia", "源质");
        add("network.mekanismthaumaturge.essentia", "源质网络");

        add("chemical.mekanismthaumaturge.mundus", "世界盐");
    }

    private void jade() {
        add("config.jade.plugin_mekanismthaumaturge.essentia_tube", "源质管道");
    }

    private void gui() {
        add("gui.mekanismthaumaturge.essentia", "源质：%1$s");
        add("gui.mekanismthaumaturge.essentia_empty", "无");
        add("gui.mekanismthaumaturge.essentia_local", "本地缓存：%1$s");
        add("gui.mekanismthaumaturge.essentia_no_network", "（未接入网络）");
        add("gui.mekanismthaumaturge.essentia_amount", "%1$s：%2$s");
        add("gui.mekanismthaumaturge.essentia_narration", "源质");
    }
}
