package com.blackmoss.mekanismthaumaturge.data;

import com.blackmoss.lazycat.api.model.ModelContext;
import com.blackmoss.lazycat.model.LazyCatModelProvider;
import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.blackmoss.mekanismthaumaturge.registry.MTItems;
import mekanism.client.model.blockstate.TransmitterBlockStateModel;
import mekanism.client.model.data.TransmitterModelData.VisualConnectionStatus;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModModelProvider extends LazyCatModelProvider {
    private static final Map<String, Boolean> TRANSMITTER_ITEM_PART_VISIBILITY = new HashMap<>();

    static {
        for (String partGroup : TransmitterBlockStateModel.ALL_PART_GROUPS) {
            TRANSMITTER_ITEM_PART_VISIBILITY.put(partGroup, partGroup.contains(VisualConnectionStatus.NONE.partName()));
        }
    }

    private ModelContext context;

    public ModModelProvider(PackOutput output) {
        super(output, MekanismThaumaturge.MODID);
    }

    @Override
    protected void addModels(ModelContext context) {
        this.context = context;

        itemFlat(MTItems.THAUMSTEEL.get());
        tubeBlock(MTBlocks.BASIC_ESSENTIA_TUBE, "basic");
        tubeBlock(MTBlocks.ADVANCED_ESSENTIA_TUBE, "advanced");
        tubeBlock(MTBlocks.ELITE_ESSENTIA_TUBE, "elite");
        tubeBlock(MTBlocks.ULTIMATE_ESSENTIA_TUBE, "ultimate");
    }

    private void tubeBlock(BlockRegistryObject<?, ?> registryObject, String tier) {
        var blockModels = this.context.blockModels();
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
                ItemModelUtils.plainModel(template.create(registryObject.asItem(), new TextureMapping(), blockModels.modelOutput)));
    }
}
