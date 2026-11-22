package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaPipe;
import com.blackmoss.mekanismthaumaturge.block.EssentiaPipeBlockEntity;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import mekanism.common.util.text.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class EssentiaPipeClientProvider implements IBlockComponentProvider {
    public static final EssentiaPipeClientProvider INSTANCE = new EssentiaPipeClientProvider();

    @Override
    public @NonNull Identifier getUid() {
        return MekanismThaumaturge.identifier("essentia_pipe");
    }

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof EssentiaPipeBlockEntity pipe)) {
            return;
        }
        CompoundTag data = accessor.getServerData();
        String type = data.getString("EssentiaType").get();
        int amount = data.getInt("EssentiaAmount").get();
        int capacity = data.getInt("EssentiaCapacity").get();
        int local = data.getInt("EssentiaLocal").get();
        boolean inNetwork = data.getBoolean("EssentiaInNetwork").get();
        if (capacity <= 0) {
            EssentiaPipe transmitter = pipe.getTransmitter();
            capacity = transmitter.getStoredCapacity();
            amount = transmitter.getStored();
            local = transmitter.getShare().amount();
            inNetwork = transmitter.hasTransmitterNetwork();
            Holder<IAspect> held = transmitter.getStoredType();
            type = held == null ? "" : held.unwrapKey().map(key -> key.identifier().toString()).orElse("");
        }
        Component typeText;
        if (type.isEmpty()) {
            typeText = Component.translatable("gui.mekanismthaumaturge.essentia_empty").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        } else {
            int colon = type.indexOf(':');
            String namespace = colon < 0 ? "thaumaturge" : type.substring(0, colon);
            String path = colon < 0 ? type : type.substring(colon + 1);
            typeText = Component.translatable("gui.mekanismthaumaturge.essentia",
                            Component.translatable("aspect." + namespace + "." + path))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        }
        tooltip.add(typeText);
        Component localText = Component.translatable("gui.mekanismthaumaturge.essentia_local", TextUtils.format(local));
        if (!inNetwork) {
            localText = localText.copy().append(Component.translatable("gui.mekanismthaumaturge.essentia_no_network"));
        }
        tooltip.add(localText);
    }
}
