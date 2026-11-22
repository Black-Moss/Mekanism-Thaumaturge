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
        configuration();

        addBlock(MTBlocks.BASIC_ESSENTIA_PIPE, "基础源质管道");
        addBlock(MTBlocks.ADVANCED_ESSENTIA_PIPE, "高级源质管道");
        addBlock(MTBlocks.ELITE_ESSENTIA_PIPE, "精英源质管道");
        addBlock(MTBlocks.ULTIMATE_ESSENTIA_PIPE, "终极源质管道");

        addItem(MTItems.THAUMSTEEL, "神秘钢");

        addAspect(MTAspects.ELECTRICA,
                "电力",
                "电动，电能，电力",
                "有电的东西");

        add("description.mekanismthaumaturge.essentia_pipe", "一种利用传输网络来传输源质的简单办法。");
        add("transmission.mekanismthaumaturge.essentia", "源质");
        add("transmitter.mekanismthaumaturge.essentia", "- 源质（%1$s）");
        add("capacity.mekanismthaumaturge.essentia", "容量：%1$s");
        add("transfer_rate.mekanismthaumaturge.essentia", "传输速率：%1$s/t");
        add("network.mekanismthaumaturge.essentia", "源质网络");

        add("chemical.mekanismthaumaturge.mundus", "世界盐");
    }

    private void jade() {
        add("config.jade.plugin_mekanismthaumaturge.essentia_pipe", "源质管道");
    }

    private void gui() {
        add("gui.mekanismthaumaturge.essentia", "源质：%1$s");
        add("gui.mekanismthaumaturge.essentia_empty", "无");
        add("gui.mekanismthaumaturge.essentia_local", "本地缓存：%1$s");
        add("gui.mekanismthaumaturge.essentia_no_network", "（未接入网络）");
        add("gui.mekanismthaumaturge.essentia_amount", "%1$s：%2$s");
        add("gui.mekanismthaumaturge.essentia_narration", "源质");
    }

    private void configuration() {
        addConfiguration("replace_essentia_display_format", "替换源质显示样式");
        addConfiguration("replace_essentia_display_format.tooltip", "将Thaumaturge自身的源质显示替换成通用神秘的样式");
    }
}
