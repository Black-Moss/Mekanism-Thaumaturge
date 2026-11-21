package com.blackmoss.mekanismthaumaturge.mixin;

import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.client.tooltip.PendingAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChemicalStack.class)
public abstract class ChemicalTooltipMixin {
    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void mekanismthaumaturge$stashAspect(
            TooltipContext context,
            List<Component> tooltips,
            TooltipFlag tooltipFlag, CallbackInfo ci) {
        ChemicalStack stack = (ChemicalStack) (Object) this;
        if (stack.isEmpty() || PendingAspects.shouldHideAspects()) {
            return;
        }
        AspectRatio ratio = stack.typeHolder().getData(MTDataMaps.CHEMICAL_ASPECT);
        if (ratio == null) {
            return;
        }
        AspectList aspects = ratio.resolve(stack.amount());
        if (!aspects.isEmpty()) {
            PendingAspects.set(aspects);
        }
    }
}
