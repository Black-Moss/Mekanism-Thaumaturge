package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import mekanism.client.model.blockstate.TransmitterBlockStateModel;
import mekanism.client.model.data.TransmitterModelData.VisualConnectionStatus;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModModelProvider extends ModelProvider {

    /** 物品模型只显示 NONE 连接部件，与 mekanism 的 TRANSMITTER_ITEM_PART_VISIBILITY 一致 */
    private static final Map<String, Boolean> TRANSMITTER_ITEM_PART_VISIBILITY = new HashMap<>();
    private static final TextureMapping NO_TEXTURES = new TextureMapping();

    static {
        for (String partGroup : TransmitterBlockStateModel.ALL_PART_GROUPS) {
            TRANSMITTER_ITEM_PART_VISIBILITY.put(partGroup, partGroup.contains(VisualConnectionStatus.NONE.partName()));
        }
    }

    public ModModelProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        flatItem(itemModels, MTItems.THAUMSTEEL);
        tubeBlock(blockModels, MTBlocks.BASIC_ESSENTIA_TUBE, "basic");
        tubeBlock(blockModels, MTBlocks.ADVANCED_ESSENTIA_TUBE, "advanced");
        tubeBlock(blockModels, MTBlocks.ELITE_ESSENTIA_TUBE, "elite");
        tubeBlock(blockModels, MTBlocks.ULTIMATE_ESSENTIA_TUBE, "ultimate");
    }

    /**
     * 生成源质管道的 blockstate 与物品模型。
     * <p>
     * blockstate 使用 mekanism 的 {@code special/transmitter} 自定义模型，
     * 引用 {@code models/block/transmitter/large/essentia_tube/<tier>} 里手写的基础模型；
     * 物品模型则以它作为父模型、只保留 NONE 连接部件。
     */
    private static void tubeBlock(@NonNull BlockModelGenerators blockModels,
            BlockRegistryObject<?, ?> registryObject, String tier) {
        Block block = registryObject.value();
        Identifier baseModelLocation = MekanismThaumaturge.identifier("block/transmitter/large/essentia_tube/" + tier);
        Variant baseModel = BlockModelGenerators.plainModel(baseModelLocation);

        MultiVariant multiVariant = MultiVariant.of(new CustomBlockStateModelBuilder.Simple(
                new TransmitterBlockStateModel.Unbaked(baseModel, Optional.empty(), true)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, multiVariant));

        ModelTemplate template = ExtendedModelTemplateBuilder.builder()
                .parent(baseModel.modelLocation())
                .partVisibilities(TRANSMITTER_ITEM_PART_VISIBILITY)
                .build();
        blockModels.itemModelOutput.accept(registryObject.asItem(),
                ItemModelUtils.plainModel(template.create(registryObject.asItem(), NO_TEXTURES, blockModels.modelOutput)));
    }

    private static void flatItem(@NonNull ItemModelGenerators itemModels, @NonNull ItemLike itemLike) {
        Item item = itemLike.asItem();
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        Identifier modelId = MekanismThaumaturge.identifier("item/" + itemId.getPath());
        ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(item), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelId));
    }
}
