package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MTCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MekanismThaumaturge.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MEKANISM_THAUMATURGE = CREATIVE_MODE_TABS.register(MekanismThaumaturge.MODID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mekanismthaumaturge"))
                    .icon(() -> MTBlocks.BASIC_ESSENTIA_TUBE.asItem().getDefaultInstance())
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(MTBlocks.BASIC_ESSENTIA_TUBE.asItem());
                        output.accept(MTBlocks.ADVANCED_ESSENTIA_TUBE.asItem());
                        output.accept(MTBlocks.ELITE_ESSENTIA_TUBE.asItem());
                        output.accept(MTBlocks.ULTIMATE_ESSENTIA_TUBE.asItem());
                    }).build());
}
