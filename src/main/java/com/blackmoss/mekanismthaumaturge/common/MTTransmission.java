package com.blackmoss.mekanismthaumaturge.common;

import mekanism.common.block.states.TransmitterType;
import mekanism.common.lib.transmitter.TransmissionType;

public final class MTTransmission {
    public static final TransmissionType ESSENTIA = resolve(TransmissionType.class, "MEKANISM_THAUMATURGE_ESSENTIA");
    public static final TransmitterType ESSENTIA_TUBE = resolve(TransmitterType.class, "MEKANISM_THAUMATURGE_ESSENTIA_TUBE");

    private static <E extends Enum<E>> E resolve(Class<E> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Missing injected enum constant %s; verify mekanismthaumaturge.mixins.json".formatted(name), e);
        }
    }
}
