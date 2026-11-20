package com.blackmoss.mekanismthaumaturge.registry;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.TTAspects;
import com.leclowndu93150.thaumaturge.content.aspect.Aspect;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

public final class MTAspects {
    public static final ResourceKey<IAspect> ELECTRICA = ResourceKey.create(IAspect.REGISTRY_KEY, MekanismThaumaturge.identifier("electrica"));

    public static void bootstrap(BootstrapContext<IAspect> ctx) {
        var lookup = ctx.lookup(IAspect.REGISTRY_KEY);
        ctx.register(ELECTRICA, new Aspect("electrica", 0x3EF090,
                List.of(lookup.getOrThrow(TTAspects.MACHINA), lookup.getOrThrow(TTAspects.POTENTIA)),
                Optional.empty(),
                MekanismThaumaturge.identifier("textures/aspects/electrica.png"),
                Aspect.DEFAULT_BLEND));
    }

}
