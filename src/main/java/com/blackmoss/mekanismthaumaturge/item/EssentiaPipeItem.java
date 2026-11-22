package com.blackmoss.mekanismthaumaturge.item;

import com.blackmoss.mekanismthaumaturge.block.EssentiaPipeBlockEntity;
import com.blackmoss.mekanismthaumaturge.common.MekanismThaumaturgeLang;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.tier.TubeTier;
import mekanism.common.util.text.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class EssentiaPipeItem extends ItemBlockTooltip<BlockLargeTransmitter<EssentiaPipeBlockEntity>> {
    private final TubeTier tier;

    public EssentiaPipeItem(BlockLargeTransmitter<EssentiaPipeBlockEntity> block, Item.Properties properties) {
        super(block, true, properties);
        this.tier = Attribute.getTierNN(block, TubeTier.class);
    }

    @Override
    protected void addStats(
            @NonNull ItemStack stack,
            @NonNull ItemAccess itemAccess,
            Item.@NonNull TooltipContext context,
            @NonNull TooltipDisplay tooltipDisplay,
            @NonNull Consumer<Component> tooltipAdder,
            @NonNull TooltipFlag flag) {
        super.addStats(stack, itemAccess, context, tooltipDisplay, tooltipAdder, flag);
        tooltipAdder.accept(MekanismThaumaturgeLang.TRANSMITTER_CAPACITY.translateColored(
                EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(capacity())));
        tooltipAdder.accept(MekanismThaumaturgeLang.TRANSMITTER_TRANSFER_RATE.translateColored(
                EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(transferRate())));
    }

    @Override
    protected void addDetails(
            @NonNull ItemStack stack,
            @NonNull ItemAccess itemAccess,
            Item.@NonNull TooltipContext context,
            @NonNull TooltipDisplay tooltipDisplay,
            @NonNull Consumer<Component> tooltipAdder,
            @NonNull TooltipFlag flag) {
        super.addDetails(stack, itemAccess, context, tooltipDisplay, tooltipAdder, flag);
        tooltipAdder.accept(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltipAdder.accept(Component.translatable(
                MekanismThaumaturgeLang.TRANSMITTER_ESSENTIA.getTranslationKey(),
                Component.literal("Thaumaturge")).withColor(EnumColor.PURPLE.getColor().getValue()));
    }

    private long capacity() {
        return tier.getCapacity() / 100;
    }

    private int transferRate() {
        return switch (tier.getBaseTier()) {
            case ADVANCED -> 4;
            case ELITE -> 16;
            case ULTIMATE -> 64;
            default -> 1;
        };
    }
}
