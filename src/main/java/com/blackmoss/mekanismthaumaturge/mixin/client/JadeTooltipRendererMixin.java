package com.blackmoss.mekanismthaumaturge.mixin.client;

import com.blackmoss.mekanismthaumaturge.client.MekanismThaumaturgeClient;
import com.blackmoss.mekanismthaumaturge.compat.jade.EssentiaTubeDataProvider;
import mekanism.common.integration.lookingat.jade.JadeTooltipRenderer;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.Accessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(JadeTooltipRenderer.class)
public class JadeTooltipRendererMixin {
    @Inject(method = "appendTooltip", at = @At("TAIL"), remap = false)
    private void mekanismthaumaturge$appendEssentia(
            ITooltip tooltip,
            Accessor<?> accessor,
            IPluginConfig config,
            CallbackInfo ci) {
        Tag tag = accessor.getServerData().get(EssentiaTubeDataProvider.KEY_DISPLAY);
        if (tag == null) {
            return;
        }
        accessor.decodeFromNbt(EssentiaTubeDataProvider.Display.CODEC, tag)
                .ifPresent(display -> tooltip.add(MekanismThaumaturgeClient.EssentiaElement.create(display)));
    }
}
