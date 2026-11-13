package com.blackmoss.mekanismthaumaturge.mixin;

import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.slot.BaseSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TileComponentConfig.class)
public class TileComponentConfigMixin {
    @Unique
    private static final int VANILLA_TRANSMISSION_TYPE_COUNT = 5;

    @Inject(
            method = "createInfo(Lmekanism/common/lib/transmitter/TransmissionType;ZZLjava/util/List;)Lmekanism/common/tile/component/config/slot/BaseSlotInfo;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void mekanismthaumaturge$createInfo(
            TransmissionType type,
            boolean input,
            boolean output,
            List<?> containers,
            CallbackInfoReturnable<BaseSlotInfo> cir) {
        if (type.ordinal() == VANILLA_TRANSMISSION_TYPE_COUNT) {
            cir.setReturnValue(new InventorySlotInfo(input, output, List.of()));
        }
    }
}
