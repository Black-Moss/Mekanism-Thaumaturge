package com.blackmoss.mekanismthaumaturge;

import com.blackmoss.mekanismthaumaturge.data.ModBlockLootSubProvider;
import com.blackmoss.mekanismthaumaturge.data.ModModelProvider;
import com.blackmoss.mekanismthaumaturge.data.ModRecipeProvider;
import com.blackmoss.mekanismthaumaturge.data.lang.EnUsProvider;
import com.blackmoss.mekanismthaumaturge.data.lang.ZhCnProvider;
import com.blackmoss.mekanismthaumaturge.data.tag.ModBlockTagsProvider;
import com.blackmoss.mekanismthaumaturge.data.tag.ModItemTagsProvider;
import com.blackmoss.mekanismthaumaturge.registry.MTBlockEntities;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTCreativeModeTabs;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
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
        MTBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MTBlocks.BLOCKS.register(modEventBus);
        MTCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        MTItems.ITEMS.register(modEventBus);
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
