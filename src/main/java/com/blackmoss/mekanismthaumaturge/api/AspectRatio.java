package com.blackmoss.mekanismthaumaturge.api;

import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectKnowledgeAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.datamaps.chemical.attribute.IChemicalAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record AspectRatio(int volume, AspectList aspects) implements IChemicalAttribute {
    public static final Codec<AspectRatio> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("volume").forGetter(AspectRatio::volume),
            AspectList.CODEC.fieldOf("aspects").forGetter(AspectRatio::aspects)
    ).apply(instance, AspectRatio::new));

    public AspectRatio {
        if (volume < 1) {
            throw new IllegalArgumentException("aspect ratio volume must be positive");
        }
        if (aspects.isEmpty()) {
            throw new IllegalArgumentException("aspect ratio needs at least one aspect");
        }
    }

    public AspectList resolve(int amount) {
        int batches = amount / this.volume;
        if (batches <= 0) {
            return AspectList.EMPTY;
        }
        AspectList result = AspectList.EMPTY;
        for (AspectInstance entry : this.aspects.entries()) {
            result = result.add(entry.aspect(), entry.amount() * batches);
        }
        return result;
    }

    @Override
    public void collectTooltips(@NonNull TooltipContext context, List<Component> tooltips, @NonNull TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("tooltip.thaumaturge.aspects.header")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0xB59ED9))));
        for (AspectInstance entry : this.aspects.sortedByAmount().reversed()) {
            var aspect = entry.aspect();
            Component named = AspectKnowledgeAccess.isKnown(aspect)
                    ? Component.translatable("tooltip.thaumaturge.amount", AspectComponents.name(aspect), entry.amount())
                    : AspectComponents.name(aspect);
            MutableComponent line = Component.literal("  ").append(named);
            line.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(aspect.value().color())));
            tooltips.add(line);
        }
    }
}
