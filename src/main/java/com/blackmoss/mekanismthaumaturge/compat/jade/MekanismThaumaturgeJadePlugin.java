package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.blackmoss.mekanismthaumaturge.block.EssentiaTubeBlockEntity;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MekanismThaumaturgeJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EssentiaTubeDataProvider.INSTANCE, EssentiaTubeBlockEntity.class);
        registration.registerBlockDataProvider(EssentiaContainerDataProvider.INSTANCE, Block.class);
    }

    @Override
    public void registerClient(@NonNull IWailaClientRegistration registration) {
    }
}
