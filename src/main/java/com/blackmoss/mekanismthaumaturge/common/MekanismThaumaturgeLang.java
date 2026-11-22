package com.blackmoss.mekanismthaumaturge.common;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import mekanism.api.text.ILangEntry;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public enum MekanismThaumaturgeLang implements ILangEntry {
    TRANSMISSION_TYPE_ESSENTIA("transmission", "essentia"),
    TRANSMITTER_ESSENTIA("transmitter", "essentia"),
    TRANSMITTER_CAPACITY("capacity", "essentia"),
    TRANSMITTER_TRANSFER_RATE("transfer_rate", "essentia"),
    DESCRIPTION_ESSENTIA_PIPE("description", "essentia_pipe");

    private final String key;

    MekanismThaumaturgeLang(String type, String path) {
        this(Util.makeDescriptionId(type, MekanismThaumaturge.identifier(path)));
    }

    MekanismThaumaturgeLang(String key) {
        this.key = key;
    }

    @Override
    public @NonNull String getTranslationKey() {
        return this.key;
    }
}
