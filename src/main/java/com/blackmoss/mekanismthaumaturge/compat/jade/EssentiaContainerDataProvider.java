package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectContainer;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class EssentiaContainerDataProvider implements IServerDataProvider<BlockAccessor> {
    public static final EssentiaContainerDataProvider INSTANCE = new EssentiaContainerDataProvider();
    public static final Identifier UID = MekanismThaumaturge.identifier("essentia_container");

    @Override
    public @NonNull Identifier getUid() {
        return UID;
    }

    @Override
    public void appendServerData(@NonNull CompoundTag data, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity == null || blockEntity instanceof EssentiaTubeBlockEntity) {
            return;
        }
        EssentiaTubeDataProvider.Display display = read(blockEntity);
        if (display != null) {
            data.put(EssentiaTubeDataProvider.KEY_DISPLAY,
                    accessor.encodeAsNbt(EssentiaTubeDataProvider.Display.CODEC, display));
        }
    }

    private static EssentiaTubeDataProvider.@Nullable Display read(BlockEntity blockEntity) {
        Holder<IAspect> aspect = null;
        int amount = 0;
        if (blockEntity instanceof IEssentiaTransport transport) {
            aspect = transport.getEssentiaType(null);
            amount = transport.getEssentiaAmount(null);
            if (aspect == null || amount <= 0) {
                for (Direction side : Direction.values()) {
                    aspect = transport.getEssentiaType(side);
                    amount = transport.getEssentiaAmount(side);
                    if (aspect != null && amount > 0) {
                        break;
                    }
                }
            }
        }
        if ((aspect == null || amount <= 0) && blockEntity instanceof IAspectContainer container) {
            for (AspectInstance entry : container.getAspects().entries()) {
                if (entry.amount() > 0) {
                    aspect = entry.aspect();
                    amount = entry.amount();
                    break;
                }
            }
        }
        if (aspect == null || amount <= 0) {
            return null;
        }
        Holder<IAspect> found = aspect;
        String typeName = found.unwrapKey()
                .map(key -> key.identifier().getNamespace() + ":" + found.value().tag())
                .orElse("");
        return new EssentiaTubeDataProvider.Display(amount, amount, amount,
                found.value().color(), typeName);
    }
}
