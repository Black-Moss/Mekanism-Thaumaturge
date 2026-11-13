package com.blackmoss.mekanismthaumaturge.mixin.client;

import mekanism.client.SpecialColors;
import mekanism.client.gui.element.tab.GuiConfigTypeTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.lib.transmitter.TransmissionType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiConfigTypeTab.class)
public class GuiConfigTypeTabMixin {
    @Unique
    private static final int VANILLA_TRANSMISSION_TYPE_COUNT = 5;

    @Final
    @Shadow(remap = false)
    private TransmissionType transmission;

    @Inject(
            method = "getTabColor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)I",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void mekanismthaumaturge$getTabColor(GuiGraphicsExtractor guiGraphics, CallbackInfoReturnable<Integer> cir) {
        if (transmission.ordinal() == VANILLA_TRANSMISSION_TYPE_COUNT) {
            cir.setReturnValue(MekanismRenderer.color(SpecialColors.TAB_ENERGY_CONFIG));
        }
    }
}
