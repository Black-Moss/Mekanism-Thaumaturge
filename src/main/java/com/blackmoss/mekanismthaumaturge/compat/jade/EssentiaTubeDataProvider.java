package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTube;
import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class EssentiaTubeDataProvider implements IServerDataProvider<BlockAccessor> {
    public static final EssentiaTubeDataProvider INSTANCE = new EssentiaTubeDataProvider();

    static final String KEY_TYPE = "EssentiaType";
    static final String KEY_AMOUNT = "EssentiaAmount";
    static final String KEY_CAPACITY = "EssentiaCapacity";
    static final String KEY_LOCAL = "EssentiaLocal";
    static final String KEY_IN_NETWORK = "EssentiaInNetwork";

    @Override
    public @NonNull Identifier getUid() {
        return MekanismThaumaturge.identifier("essentia_tube");
    }

    @Override
    public void appendServerData(@NonNull CompoundTag data, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof EssentiaTubeBlockEntity tube)) {
            return;
        }
        EssentiaTube transmitter = tube.getTransmitter();
        Holder<IAspect> type = transmitter.getStoredType();
        data.putString(KEY_TYPE, type == null ? "" : type.unwrapKey().map(key -> key.identifier().toString()).orElse(""));
        data.putInt(KEY_AMOUNT, transmitter.getStored());
        data.putInt(KEY_CAPACITY, transmitter.getStoredCapacity());
        data.putInt(KEY_LOCAL, transmitter.getShare().amount());
        data.putBoolean(KEY_IN_NETWORK, transmitter.hasTransmitterNetwork());
    }
}
