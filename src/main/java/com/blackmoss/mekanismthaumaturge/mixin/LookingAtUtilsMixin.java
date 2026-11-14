package com.blackmoss.mekanismthaumaturge.mixin;

import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import mekanism.common.integration.lookingat.LookingAtHelper;
import mekanism.common.integration.lookingat.LookingAtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LookingAtUtils.class)
public class LookingAtUtilsMixin {

    @Inject(method = "addInfoOrRedirect", at = @At("TAIL"), remap = false)
    private static void mekanismthaumaturge$addEssentiaType(
            LookingAtHelper info,
            Level level,
            BlockPos pos,
            BlockState state,
            BlockEntity tile,
            boolean displayTanks,
            boolean displayFluidTanks,
            CallbackInfo ci) {
        if (!(tile instanceof EssentiaTubeBlockEntity tube)) {
            return;
        }
        Holder<IAspect> type = tube.getTransmitter().getStoredType();
        if (type == null) {
            return;
        }
        ResourceKey<IAspect> key = type.unwrapKey().orElse(null);
        if (key == null) {
            return;
        }
        Component aspectName = Component.translatable("aspect.%s.%s".formatted(key.identifier().getNamespace(), key.identifier().getPath()));
        info.addText(Component.translatable("gui.mekanismthaumaturge.essentia", aspectName));
    }
}
