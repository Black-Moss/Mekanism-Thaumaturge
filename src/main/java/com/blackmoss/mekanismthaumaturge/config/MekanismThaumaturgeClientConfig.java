package com.blackmoss.mekanismthaumaturge.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MekanismThaumaturgeClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REPLACE_ESSENTIA_DISPLAY_FORMAT = BUILDER
            .comment("When true, Replace Thaumaturge's own essentia display with a Mekanism Thaumaturge style")
            .define("replace_essentia_display_format", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
