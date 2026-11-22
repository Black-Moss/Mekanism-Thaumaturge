package com.blackmoss.mekanismthaumaturge.mixin.client;

import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.client.tooltip.PendingAspects;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectChipsTooltip;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.resource.IResourceContainer;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.gauge.GuiTankGauge;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGauge.class)
public abstract class GuiGaugeAspectIconMixin {
    @Unique
    private static GuiTankGauge<?, ?> mekanismthaumaturge$asTankGauge(Object self) {
        return self instanceof GuiTankGauge<?, ?> gauge ? gauge : null;
    }

    @Unique
    private static void mekanismthaumaturge$installTooltip(Object self, Tooltip tooltip) {
        ((GuiGauge<?>) self).setTooltip(tooltip);
    }

    @Unique
    private static AspectList mekanismthaumaturge$readAspects(GuiTankGauge<?, ?> gauge) {
        IResourceContainer<?> gaugeContainer = gauge.getContainer();
        IResourceContainer<?> container = null;
        if (gaugeContainer != null) {
            container = gaugeContainer;
        }
        if (container == null || container.isEmpty()) {
            return AspectList.EMPTY;
        }
        long amount = container.amountAsLong();
        int intAmount = (int) Math.min(amount, Integer.MAX_VALUE);
        if (gauge instanceof GuiFluidGauge) {
            FluidResource type = (FluidResource) container.resource();
            AspectRatio ratio = type.typeHolder().getData(MTDataMaps.FLUID_ASPECT);
            return ratio == null ? AspectList.EMPTY : ratio.resolve(intAmount);
        }
        if (gauge instanceof GuiChemicalGauge) {
            ChemicalResource type = (ChemicalResource) container.resource();
            AspectRatio ratio = type.getData(MTDataMaps.CHEMICAL_ASPECT);
            return ratio == null ? AspectList.EMPTY : ratio.resolve(intAmount);
        }
        return AspectList.EMPTY;
    }

    @Inject(method = "updateTooltip", at = @At("TAIL"))
    private void mekanismthaumaturge$attachAspectIcon(int mouseX, int mouseY, CallbackInfo ci) {
        GuiTankGauge<?, ?> gauge = mekanismthaumaturge$asTankGauge(this);
        if (PendingAspects.shouldHideAspects() || gauge == null) {
            return;
        }
        AspectList aspects = mekanismthaumaturge$readAspects(gauge);
        if (aspects.isEmpty()) {
            return;
        }
        List<Component> lines = gauge.getTooltipText();
        MutableComponent message = MutableComponent.create(new LiteralContents(""));
        for (Component line : lines) {
            if (!message.getSiblings().isEmpty()) {
                message.append(MutableComponent.create(new LiteralContents("\n")));
            }
            message.append(line);
        }
        Tooltip tooltip = Tooltip.create(message, Optional.of(new AspectChipsTooltip(aspects)), null);
        mekanismthaumaturge$installTooltip(this, tooltip);
    }
}
