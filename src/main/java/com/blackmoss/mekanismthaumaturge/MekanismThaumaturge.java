package com.blackmoss.mekanismthaumaturge;

import com.blackmoss.mekanismthaumaturge.data.ModBlockLootSubProvider;
import com.blackmoss.mekanismthaumaturge.data.ModModelProvider;
import com.blackmoss.mekanismthaumaturge.data.ModRecipeProvider;
import com.blackmoss.mekanismthaumaturge.data.datamap.ModDataMapProvider;
import com.blackmoss.mekanismthaumaturge.data.lang.EnUsProvider;
import com.blackmoss.mekanismthaumaturge.data.lang.ZhCnProvider;
import com.blackmoss.mekanismthaumaturge.data.tag.ModBlockTagsProvider;
import com.blackmoss.mekanismthaumaturge.data.tag.ModChemicalTagsProvider;
import com.blackmoss.mekanismthaumaturge.data.tag.ModItemTagsProvider;
import com.blackmoss.mekanismthaumaturge.network.PacketEssentiaNetworkSync;
import com.blackmoss.mekanismthaumaturge.registry.*;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.data.worldgen.aspect.AspectBootstrap;
import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistrySetBuilder;
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
        MTBlockEntities.TILE_ENTITY_TYPES.register(modEventBus);
        MTBlocks.BLOCKS.register(modEventBus);
        MTChemicals.CHEMICALS.register(modEventBus);
        MTCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        MTItems.ITEMS.register(modEventBus);

        modEventBus.addListener(PacketEssentiaNetworkSync::registerPayloads);
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        RegistrySetBuilder registries = new RegistrySetBuilder()
                .add(IAspect.REGISTRY_KEY, ctx -> {
                    AspectBootstrap.bootstrap(ctx);
                    MTAspects.bootstrap(ctx);
                });
        event.createDatapackRegistryObjects(registries);

        event.createProvider(EnUsProvider::new);
        event.createProvider(ZhCnProvider::new);

        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModDataMapProvider::new);

        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModChemicalTagsProvider::new);
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
