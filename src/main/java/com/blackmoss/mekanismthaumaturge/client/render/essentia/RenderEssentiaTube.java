package com.blackmoss.mekanismthaumaturge.client.render.essentia;

import com.blackmoss.mekanismthaumaturge.common.essentia.EssentiaTube;
import com.blackmoss.mekanismthaumaturge.common.essentia.TileEntityEssentiaTube;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.RenderResizableCuboid;
import mekanism.client.render.RenderResizableCuboid.SideRender;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.client.render.transmitter.TransmitterRenderState.PipeRenderState;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.util.EnumUtils;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RenderEssentiaTube extends RenderTransmitterBase<TileEntityEssentiaTube, PipeRenderState> {
    private static final int STAGES = 100;
    private static final float HEIGHT = 0.45F;
    private static final float OFFSET = 0.02F;
    private static final Identifier GLOW_TEXTURE = Identifier.tryParse("thaumaturge:block/animatedglow");

    public RenderEssentiaTube(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull PipeRenderState createRenderState() {
        return new PipeRenderState();
    }

    @Override
    public void extractRenderState(
            @NonNull TileEntityEssentiaTube tile,
            @NonNull PipeRenderState state,
            float partialTick,
            @NonNull Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);
        EssentiaTube transmitter = tile.getTransmitter();
        Holder<IAspect> type = transmitter.getStoredType();
        if (type == null) {
            return;
        }
        int amount = transmitter.getStored();
        long capacity = transmitter.getCapacity();
        if (capacity <= 0) {
            capacity = 1;
        }
        if (amount <= 0) {
            return;
        }
        float rawScale = (float) amount / (float) capacity;
        float scale = Math.max((float) Math.cbrt(rawScale), 0.3F);
        state.currentScale = scale;
        TextureAtlasSprite glowSprite = null;
        if (GLOW_TEXTURE != null) {
            glowSprite = MekanismRenderer.getSprite(GLOW_TEXTURE);
        }
        if (glowSprite != null) {
            state.fluidTexture = MekanismRenderer.getSinglePicker(glowSprite);
        }
        int color = type.value().color();
        state.fluidTint = 0xFF000000 | color;
        state.stage = Math.max(3, (int) (scale * (STAGES - 1)));
        state.glow = state.lightCoords;

        List<String> connectionContents = new ArrayList<>();
        boolean[] renderSides = new boolean[6];
        boolean hasHorizontalSide = false;
        int verticalSides = 0;
        for (Direction side : EnumUtils.DIRECTIONS) {
            ConnectionType connectionType = transmitter.getConnectionType(side);
            if (connectionType == ConnectionType.PUSH || connectionType == ConnectionType.PULL) {
                connectionContents.add(side.getSerializedName() + connectionType.getSerializedName().toUpperCase(Locale.ROOT));
            }
            renderSides[side.ordinal()] = connectionType != ConnectionType.NORMAL;
            if (connectionType != ConnectionType.NONE) {
                if (side.getAxis().isHorizontal()) {
                    hasHorizontalSide = true;
                } else {
                    verticalSides++;
                }
            }
        }
        state.connectionContents = connectionContents;
        boolean renderBase = hasHorizontalSide || verticalSides < 2;
        state.renderBase = renderBase;
        @SideRender.SideRenderFlags byte coreSideRender = 0;
        for (Direction side : EnumUtils.DIRECTIONS) {
            if (renderSides[side.ordinal()] || (renderBase && state.stage != STAGES - 1 && side.getAxis().isVertical())) {
                coreSideRender |= SideRender.of(side);
            }
        }
        state.coreSideRender = coreSideRender;
        Arrays.fill(state.renderSideModel, false);
        for (Direction side : EnumUtils.DIRECTIONS) {
            ConnectionType connectionType = transmitter.getConnectionType(side);
            if (connectionType == ConnectionType.NORMAL) {
                state.renderSideModel[side.ordinal()] = true;
            }
        }
    }

    @Override
    public void submit(
            @NonNull PipeRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector nodeCollector,
            @NonNull CameraRenderState camera) {
        if (state.fluidTexture == null) {
            return;
        }

        float stageRatio = (state.stage / (float) STAGES) * HEIGHT;

        for (Direction side : EnumUtils.DIRECTIONS) {
            if (!state.renderSideModel[side.ordinal()]) {
                continue;
            }
            @SideRender.SideRenderFlags
            byte sideRenderCheck = (byte) (SideRender.ALL_FACES ^ SideRender.of(side) ^ SideRender.of(side.getOpposite()));

            float minX, minY, minZ;
            float maxX, maxY, maxZ;

            if (side.getAxis().isHorizontal()) {
                minY = 0.25F + OFFSET;
                maxY = 0.25F + OFFSET + stageRatio;
                if (side.getAxis() == Axis.Z) {
                    minX = 0.25F + OFFSET;
                    maxX = 0.75F - OFFSET;
                    if (side.getAxisDirection() == AxisDirection.POSITIVE) {
                        minZ = 0.75F - OFFSET;
                        maxZ = 1;
                    } else {
                        minZ = 0;
                        maxZ = 0.25F + OFFSET;
                    }
                } else {
                    minZ = 0.25F + OFFSET;
                    maxZ = 0.75F - OFFSET;
                    if (side.getAxisDirection() == AxisDirection.POSITIVE) {
                        minX = 0.75F - OFFSET;
                        maxX = 1;
                    } else {
                        minX = 0;
                        maxX = 0.25F + OFFSET;
                    }
                }
            } else {
                float min = 0.5F - stageRatio / 2;
                float max = 0.5F + stageRatio / 2;
                minX = min;
                maxX = max;
                minZ = min;
                maxZ = max;
                if (side == Direction.DOWN) {
                    minY = 0;
                    maxY = 0.25F + OFFSET;
                } else {
                    minY = 0.25F + OFFSET + stageRatio;
                    maxY = 1;
                }
            }
            RenderResizableCuboid.renderCube(sideRenderCheck,
                    minX, minY, minZ,
                    maxX, maxY, maxZ,
                    poseStack,
                    Sheets.translucentBlockSheet(),
                    nodeCollector,
                    state.fluidTint, state.glow,
                    OverlayTexture.NO_OVERLAY,
                    RenderResizableCuboid.FaceDisplay.FRONT,
                    camera.pos,
                    Vec3.atLowerCornerOf(state.blockPos),
                    state.fluidTexture);
        }

        {
            float min;
            float max;
            if (state.renderBase) {
                min = 0.25F + OFFSET;
                max = 0.75F - OFFSET;
            } else {
                min = 0.5F - stageRatio / 2;
                max = 0.5F + stageRatio / 2;
            }
            RenderResizableCuboid.renderCube(
                    state.coreSideRender, min,
                    0.25F + OFFSET,
                    min, max,
                    0.25F + OFFSET + stageRatio,
                    max, poseStack,
                    Sheets.translucentBlockSheet(),
                    nodeCollector,
                    state.fluidTint, state.glow,
                    OverlayTexture.NO_OVERLAY,
                    RenderResizableCuboid.FaceDisplay.FRONT,
                    camera.pos,
                    Vec3.atLowerCornerOf(state.blockPos),
                    state.fluidTexture);
        }
    }

    @Override
    protected @NonNull String getProfilerSection() {
        return ProfilerConstants.MECHANICAL_PIPE;
    }

    @Override
    protected boolean shouldRenderTransmitter(@NonNull TileEntityEssentiaTube tile, @NonNull Vec3 camera) {
        EssentiaTube tube = tile.getTransmitter();
        return super.shouldRenderTransmitter(tile, camera)
            && tube.getStoredType() != null
            && tube.getStored() > 0;
    }
}
