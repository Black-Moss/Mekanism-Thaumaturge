package com.blackmoss.lazycat.datamap.mekanismthaumaturge;

import com.blackmoss.lazycat.api.LazyIdentifier;
import com.blackmoss.lazycat.api.datamap.AbstractDataMapBuilder;
import com.blackmoss.lazycat.api.datamap.DataMapContext;
import com.blackmoss.lazycat.api.datamap.DataMapTargets;
import com.blackmoss.mekanismthaumaturge.api.AspectRatio;
import com.blackmoss.mekanismthaumaturge.registry.MTDataMaps;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Objects;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public final class FluidAspectDataMapBuilder extends AbstractDataMapBuilder<Fluid, AspectRatio> {
    private int volume = 100;
    private AspectList aspects = AspectList.EMPTY;

    public FluidAspectDataMapBuilder(DataMapContext context, Fluid fluid) {
        this(context, DataMapTargets.entry(LazyIdentifier.getIdentifier(BuiltInRegistries.FLUID, fluid)));
    }

    public FluidAspectDataMapBuilder(DataMapContext context, TagKey<Fluid> tag) {
        this(context, DataMapTargets.tag(Objects.requireNonNull(tag, "tag")));
    }

    public FluidAspectDataMapBuilder(
            DataMapContext context,
            DataMapType<Fluid, AspectRatio> type,
            BiConsumer<DataMapProvider.Builder<AspectRatio, Fluid>, AspectRatio> target) {
        super(context, type, target);
    }

    private FluidAspectDataMapBuilder(
            DataMapContext context,
            BiConsumer<DataMapProvider.Builder<AspectRatio, Fluid>, AspectRatio> target) {
        super(context, MTDataMaps.FLUID_ASPECT, target);
    }

    public FluidAspectDataMapBuilder volume(int volume) {
        if (volume <= 0) {
            throw new IllegalArgumentException("volume must be positive");
        }
        this.volume = volume;
        return this;
    }

    public FluidAspectDataMapBuilder aspect(ResourceKey<IAspect> aspect) {
        return aspect(aspect, 1);
    }

    public FluidAspectDataMapBuilder aspect(ResourceKey<IAspect> aspect, int amount) {
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
            throw new IllegalStateException("a fluid aspect entry needs at least one aspect");
        }
        return new AspectRatio(this.volume, this.aspects);
    }
}
