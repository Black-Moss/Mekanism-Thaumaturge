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

    private static final String PREFIX = "jade.mekanismthaumaturge.essentia_tube.";

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
        tooltip.add(Component.literal("源质: " + (type.isEmpty() ? "无" : type)));
        tooltip.add(Component.literal("网络存量: " + amount + " / " + capacity));
        tooltip.add(Component.literal("本地缓存: " + local + (inNetwork ? "" : " (未接入网络)")));
    }
}
