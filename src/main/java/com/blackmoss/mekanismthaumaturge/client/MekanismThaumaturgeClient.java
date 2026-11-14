package com.blackmoss.mekanismthaumaturge.client;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.client.render.EssentiaTubeRender;
import com.blackmoss.mekanismthaumaturge.compat.jade.EssentiaTubeDataProvider;
import com.blackmoss.mekanismthaumaturge.registry.MTBlockEntities;
import com.leclowndu93150.thaumaturge.TTIds;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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

        @Override
        public @Nullable Component getNarration() {
            return Component.literal(display.typeName().isEmpty() ? "源质" : display.typeName());
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
                    Component.literal(display.amount() + "/" + display.capacity()),
                    x + 3, y + 3, 0xFFFFFFFF, false);
        }
    }
}
