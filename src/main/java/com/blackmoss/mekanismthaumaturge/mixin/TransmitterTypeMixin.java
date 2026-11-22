package com.blackmoss.mekanismthaumaturge.mixin;

import mekanism.common.block.states.TransmitterType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransmitterType.class)
public enum TransmitterTypeMixin {
    MEKANISM_THAUMATURGE_ESSENTIA_PIPE(TransmitterType.Size.LARGE);

    @Shadow
    TransmitterTypeMixin(TransmitterType.Size size) {
    }
}
