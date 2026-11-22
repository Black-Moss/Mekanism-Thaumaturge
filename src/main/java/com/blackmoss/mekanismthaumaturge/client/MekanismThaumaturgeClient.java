package com.blackmoss.mekanismthaumaturge.client;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.client.render.EssentiaTubeRender;
import com.blackmoss.mekanismthaumaturge.compat.jade.EssentiaTubeDataProvider;
import com.blackmoss.mekanismthaumaturge.registry.MTBlockEntities;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectChipsTooltip;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.config.ThaumaturgeClientConfig;
import com.mojang.datafixers.util.Either;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.resource.LargeResourceStack;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.util.ItemAccessUtils;
import mekanism.common.util.text.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.ui.Element;

@EventBusSubscriber(modid = MekanismThaumaturge.MODID, value = Dist.CLIENT)
public class MekanismThaumaturgeClient {
    private static final Identifier GLOW_TEXTURE = TTIds.rl("block/animatedglow");

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MTBlockEntities.BASIC_ESSENTIA_TUBE.get(), EssentiaTubeRender::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ADVANCED_ESSENTIA_TUBE.get(), EssentiaTubeRender::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ELITE_ESSENTIA_TUBE.get(), EssentiaTubeRender::new);
        event.registerBlockEntityRenderer(MTBlockEntities.ULTIMATE_ESSENTIA_TUBE.get(), EssentiaTubeRender::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get()
                .getModContainerById(MekanismThaumaturge.MODID)
                .ifPresent(container ->
                        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new));
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || shouldHideAspects()) {
            return;
        }
        AspectList aspects = readAspects(stack);
        if (aspects.isEmpty()) {
            return;
        }
        event.getTooltipElements().add(Either.right(new AspectChipsTooltip(aspects)));
    }

    private static AspectList readAspects(ItemStack stack) {
        var access = ItemAccessUtils.sideEffectFreeAccess(stack);
        AspectList aspects = AspectList.EMPTY;

        LargeResourceStack<FluidResource> fluid = ContainerType.FLUID.getStoredContentsFromAttachment(access);
        if (!fluid.isEmpty() && !fluid.resource().isEmpty()) {
            AspectRatio ratio = fluid.resource().typeHolder().getData(MTDataMaps.FLUID_ASPECT);
            if (ratio != null) {
                aspects = aspects.add(ratio.resolve(fluid.amountAsInt()));
            }
        }

        LargeResourceStack<ChemicalResource> chemical = ContainerType.CHEMICAL.getStoredContentsFromAttachment(access);
        if (!chemical.isEmpty() && !chemical.resource().isEmpty()) {
            AspectRatio ratio = chemical.resource().getData(MTDataMaps.CHEMICAL_ASPECT);
            if (ratio != null) {
                aspects = aspects.add(ratio.resolve(chemical.amountAsInt()));
            }
        }
        return aspects;
    }

    private static boolean shouldHideAspects() {
        boolean shift = Minecraft.getInstance().hasShiftDown();
        return shift == ThaumaturgeClientConfig.showAspectsByDefault();
    }

    public static class EssentiaElement extends Element {
        private static final int WIDTH = 100;
        private static final int HEIGHT = 13;
        private final EssentiaTubeDataProvider.Display display;

        public EssentiaElement(EssentiaTubeDataProvider.Display display) {
            this.display = display;
        }

        public static Element create(EssentiaTubeDataProvider.Display display) {
            return new EssentiaElement(display).size(WIDTH, HEIGHT);
        }

        private Component typeComponent() {
            String type = display.typeName();
            int colon = type.indexOf(':');
            String namespace = colon < 0 ? "thaumaturge" : type.substring(0, colon);
            String path = colon < 0 ? type : type.substring(colon + 1);
            return Component.translatable("aspect.%s.%s".formatted(namespace, path));
        }

        @Override
        public @Nullable Component getNarration() {
            if (display.typeName().isEmpty()) {
                return Component.translatable("gui.mekanismthaumaturge.essentia_narration")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
            }
            return Component.translatable("gui.mekanismthaumaturge.essentia", typeComponent())
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
            int x = getX();
            int y = getY();
            int width = WIDTH;
            int height = HEIGHT;
            graphics.fill(x, y, x + width - 1, y + 1, 0xFF000000);
            graphics.fill(x, y, x + 1, y + height - 1, 0xFF000000);
            graphics.fill(x + width - 1, y, x + width, y + height - 1, 0xFF000000);
            graphics.fill(x, y + height - 1, x + width, y + height, 0xFF000000);

            int scale = display.getScaledLevel(width - 2);
            if (scale > 0) {
                GuiUtils.drawTiledSprite(graphics,
                        x + 1,
                        y + 1,
                        height - 2,
                        scale,
                        height - 2,
                        MekanismRenderer.getSprite(GLOW_TEXTURE),
                        16, 16,
                        0, GuiUtils.TilingDirection.DOWN_RIGHT,
                        0xFF000000 | display.color());
            }
            graphics.text(Minecraft.getInstance().font,
                    Component.translatable("gui.mekanismthaumaturge.essentia_amount", typeComponent(), TextUtils.format(display.amount())),
                    x + 3, y + 3, 0xFFFFFFFF, false);
        }
    }
}
