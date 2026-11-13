package com.blackmoss.averylooongmodname;

import com.blackmoss.averylooongmodname.data.ModBlockLootSubProvider;
import com.blackmoss.averylooongmodname.data.ModModelProvider;
import com.blackmoss.averylooongmodname.data.ModRecipeProvider;
import com.blackmoss.averylooongmodname.data.lang.EnUsProvider;
import com.blackmoss.averylooongmodname.data.lang.ZhCnProvider;
import com.blackmoss.averylooongmodname.data.tag.ModBlockTagsProvider;
import com.blackmoss.averylooongmodname.data.tag.ModItemTagsProvider;
import com.blackmoss.averylooongmodname.registry.MTBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = MekanismThaumaturge.MODID)
@Mod(MekanismThaumaturge.MODID)
public class MekanismThaumaturge {
    public static final String MODID = "mekanismthaumaturge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MekanismThaumaturge(IEventBus modEventBus, ModContainer modContainer) {
        MTBlocks.BLOCKS.register(modEventBus);
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(EnUsProvider::new);
        event.createProvider(ZhCnProvider::new);

        event.createProvider(ModRecipeProvider.Runner::new);

        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModBlockTagsProvider::new);

        event.createProvider(ModModelProvider::new);

        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider));
    }
}
