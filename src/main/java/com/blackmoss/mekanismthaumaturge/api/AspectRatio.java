package com.blackmoss.mekanismthaumaturge.api;

import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AspectRatio(int volume, AspectList aspects) {
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
}
