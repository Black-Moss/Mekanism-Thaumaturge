package com.blackmoss.mekanismthaumaturge.mixin.client;

import com.blackmoss.mekanismthaumaturge.compat.jade.EssentiaJadeFormat;
import com.blackmoss.mekanismthaumaturge.compat.jade.EssentiaTubeDataProvider;
import com.blackmoss.mekanismthaumaturge.config.MekanismThaumaturgeClientConfig;
import com.leclowndu93150.thaumaturge.compat.jade.BlockDetailsComponentProvider;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.Accessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(BlockDetailsComponentProvider.class)
public class BlockDetailsComponentProviderMixin {
    @Inject(method = "appendTooltip*", at = @At("HEAD"), remap = false)
    private void mekanismthaumaturge$replaceEssentiaDisplayFormat(
            ITooltip iTooltip,
            @Coerce Accessor accessor,
            IPluginConfig iPluginConfig,
            CallbackInfo ci) {
        if (!MekanismThaumaturgeClientConfig.SPEC.isLoaded()
                || !MekanismThaumaturgeClientConfig.REPLACE_ESSENTIA_DISPLAY_FORMAT.get()) {
            return;
        }
        CompoundTag serverData = accessor.getServerData();
        if (serverData.get(EssentiaTubeDataProvider.KEY_DISPLAY) != null) {
            EssentiaJadeFormat.removeEssentiaLines(serverData);
        } else {
            EssentiaJadeFormat.rewrite(serverData);
        }
    }
}
