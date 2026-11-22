package com.blackmoss.mekanismthaumaturge.compat.jade;

import com.leclowndu93150.thaumaturge.compat.jade.BlockDetailsDataProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class EssentiaJadeFormat {
    private static final String KEY_FILL = "jade.thaumaturge.essentia.fill";
    private static final String KEY_AMOUNT = "jade.thaumaturge.aspect_amount";
    private static final String KEY_EMPTY = "jade.thaumaturge.essentia.empty";
    private static final String KEY_EMPTY_CAPACITY = "jade.thaumaturge.essentia.empty_capacity";

    private static final String FIELD_SUMMARY = "summary";
    private static final String FIELD_DETAILS = "details";
    private static final Style WHITE = Style.EMPTY.withColor(ChatFormatting.WHITE);

    public static void removeEssentiaLines(CompoundTag serverData) {
        CompoundTag details = serverData.getCompound(BlockDetailsDataProvider.DATA).orElse(null);
        if (details == null) {
            return;
        }
        removeEssentiaLines(details, FIELD_SUMMARY);
        removeEssentiaLines(details, FIELD_DETAILS);
    }

    private static void removeEssentiaLines(CompoundTag details, String field) {
        ListTag original = details.getList(field).orElse(null);
        if (original == null) {
            return;
        }
        ListTag kept = new ListTag();
        boolean changed = false;
        for (Tag tag : original) {
            if (isEssentiaLine(tag)) {
                changed = true;
                continue;
            }
            kept.add(tag);
        }
        if (changed) {
            details.put(field, kept);
        }
    }

    private static boolean isEssentiaLine(Tag tag) {
        Component component = ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
        if (component == null || !(component.getContents() instanceof TranslatableContents contents)) {
            return false;
        }
        return switch (contents.getKey()) {
            case KEY_FILL, KEY_AMOUNT, KEY_EMPTY, KEY_EMPTY_CAPACITY -> true;
            default -> false;
        };
    }

    public static void rewrite(CompoundTag serverData) {
        CompoundTag details = serverData.getCompound(BlockDetailsDataProvider.DATA).orElse(null);
        if (details == null) {
            return;
        }
        rewriteList(details, FIELD_SUMMARY);
        rewriteList(details, FIELD_DETAILS);
    }

    private static void rewriteList(CompoundTag details, String field) {
        ListTag original = details.getList(field).orElse(null);
        if (original == null) {
            return;
        }
        ListTag rewritten = new ListTag();
        boolean changed = false;
        for (Tag tag : original) {
            List<Component> mapped = mapLine(tag);
            if (mapped == null) {
                rewritten.add(tag);
                continue;
            }
            changed = true;
            for (Component line : mapped) {
                rewritten.add(ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, line).getOrThrow());
            }
        }
        if (changed) {
            details.put(field, rewritten);
        }
    }

    private static @Nullable List<Component> mapLine(Tag tag) {
        Component component = ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
        if (component == null || !(component.getContents() instanceof TranslatableContents contents)) {
            return null;
        }
        Object[] args = contents.getArgs();
        return switch (contents.getKey()) {
            case KEY_FILL -> args.length >= 3 ? List.of(
                    essentia(args[0]),
                    local(args[1] + "/" + args[2])) : null;
            case KEY_AMOUNT -> args.length >= 2 ? List.of(
                    essentia(args[0]),
                    local(args[1])) : null;
            case KEY_EMPTY, KEY_EMPTY_CAPACITY -> List.of(
                    Component.translatable("gui.mekanismthaumaturge.essentia_empty").setStyle(WHITE));
            default -> null;
        };
    }

    private static Component essentia(Object aspectName) {
        Component name = aspectName instanceof Component component
                ? component
                : Component.literal(String.valueOf(aspectName));
        return Component.translatable("gui.mekanismthaumaturge.essentia", name).setStyle(WHITE);
    }

    private static Component local(Object amount) {
        return Component.translatable("gui.mekanismthaumaturge.essentia_local", String.valueOf(amount)).setStyle(WHITE);
    }
}
