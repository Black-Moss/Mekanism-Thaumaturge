package com.blackmoss.lazycat.datamap.mekanismthaumaturge;

import com.blackmoss.lazycat.api.datamap.AbstractDataMapBuilder;
import com.blackmoss.lazycat.api.datamap.DataMapContext;
import com.blackmoss.lazycat.api.datamap.DataMapTargets;
import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Objects;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public final class ChemicalAspectDataMapBuilder extends AbstractDataMapBuilder<Chemical, AspectRatio> {
    private int volume = 10;
    private AspectList aspects = AspectList.EMPTY;

    public ChemicalAspectDataMapBuilder(DataMapContext context, ResourceKey<Chemical> key) {
        this(context, DataMapTargets.entry(Objects.requireNonNull(key, "key")));
    }

    public ChemicalAspectDataMapBuilder(DataMapContext context, TagKey<Chemical> tag) {
        this(context, DataMapTargets.tag(Objects.requireNonNull(tag, "tag")));
    }

    public ChemicalAspectDataMapBuilder(
            DataMapContext context,
            DataMapType<Chemical, AspectRatio> type,
            BiConsumer<DataMapProvider.Builder<AspectRatio, Chemical>, AspectRatio> target) {
        super(context, type, target);
    }

    private ChemicalAspectDataMapBuilder(
            DataMapContext context,
            BiConsumer<DataMapProvider.Builder<AspectRatio, Chemical>, AspectRatio> target) {
        super(context, MTDataMaps.CHEMICAL_ASPECT, target);
    }

    public ChemicalAspectDataMapBuilder volume(int volume) {
        if (volume <= 0) {
            throw new IllegalArgumentException("volume must be positive");
        }
        this.volume = volume;
        return this;
    }

    public ChemicalAspectDataMapBuilder aspect(ResourceKey<IAspect> aspect) {
        return aspect(aspect, 10);
    }

    public ChemicalAspectDataMapBuilder aspect(ResourceKey<IAspect> aspect, int amount) {
        Objects.requireNonNull(aspect, "aspect");
        if (amount <= 0) {
            throw new IllegalArgumentException("aspect amount must be positive");
        }
        Holder<IAspect> holder = context().registries().lookupOrThrow(IAspect.REGISTRY_KEY).getOrThrow(aspect);
        this.aspects = this.aspects.add(holder, amount);
        return this;
    }

    @Override
    protected AspectRatio build() {
        if (this.aspects.isEmpty()) {
            throw new IllegalStateException("a chemical aspect entry needs at least one aspect");
        }
        return new AspectRatio(this.volume, this.aspects);
    }
}
