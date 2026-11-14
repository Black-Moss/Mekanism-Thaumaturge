package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTube;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class EssentiaTubeClientProvider implements IBlockComponentProvider {
    public static final EssentiaTubeClientProvider INSTANCE = new EssentiaTubeClientProvider();

    @Override
    public @NonNull Identifier getUid() {
        return MekanismThaumaturge.identifier("essentia_tube");
    }

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof EssentiaTubeBlockEntity tube)) {
            return;
        }
        CompoundTag data = accessor.getServerData();
        String type = data.getString("EssentiaType").get();
        int amount = data.getInt("EssentiaAmount").get();
        int capacity = data.getInt("EssentiaCapacity").get();
        int local = data.getInt("EssentiaLocal").get();
        boolean inNetwork = data.getBoolean("EssentiaInNetwork").get();
        if (capacity <= 0) {
            EssentiaTube transmitter = tube.getTransmitter();
            capacity = transmitter.getStoredCapacity();
            amount = transmitter.getStored();
            local = transmitter.getShare().amount();
            inNetwork = transmitter.hasTransmitterNetwork();
            Holder<IAspect> held = transmitter.getStoredType();
            type = held == null ? "" : held.unwrapKey().map(key -> key.identifier().toString()).orElse("");
        }
        Component typeText;
        if (type.isEmpty()) {
            typeText = Component.translatable("gui.mekanismthaumaturge.essentia_empty");
        } else {
            int colon = type.indexOf(':');
            String namespace = colon < 0 ? "thaumaturge" : type.substring(0, colon);
            String path = colon < 0 ? type : type.substring(colon + 1);
            typeText = Component.translatable("gui.mekanismthaumaturge.essentia",
                    Component.translatable("aspect." + namespace + "." + path));
        }
        tooltip.add(typeText);
        Component localText = Component.translatable("gui.mekanismthaumaturge.essentia_local", local);
        if (!inNetwork) {
            localText = localText.copy().append(Component.translatable("gui.mekanismthaumaturge.essentia_no_network"));
        }
        tooltip.add(localText);
    }
}
