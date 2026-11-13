package com.blackmoss.mekanismthaumaturge.mixin;

import com.blackmoss.mekanismthaumaturge.common.MekanismThaumaturgeLang;
import mekanism.api.text.ILangEntry;
import mekanism.common.lib.transmitter.TransmissionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransmissionType.class)
public enum TransmissionTypeMixin {
    MEKANISM_THAUMATURGE_ESSENTIA("EssentiaNetwork", "essentia", MekanismThaumaturgeLang.TRANSMISSION_TYPE_ESSENTIA);

    @Shadow
    TransmissionTypeMixin(String name, String transmission, ILangEntry langEntry) {
    }
}
