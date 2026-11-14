package com.blackmoss.mekanismthaumaturge.network;

import com.blackmoss.mekanismthaumaturge.MekanismThaumaturge;
import com.blackmoss.mekanismthaumaturge.block.EssentiaNetwork;
import com.leclowndu93150.thaumaturge.api.aspect.Aspects;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PacketEssentiaNetworkSync(
        UUID networkID,
        String typeKey,
        int amount,
        float scale) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketEssentiaNetworkSync> TYPE = new CustomPacketPayload.Type<>(
            MekanismThaumaturge.identifier("essentia_network_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketEssentiaNetworkSync> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketEssentiaNetworkSync::networkID,
            ByteBufCodecs.STRING_UTF8, PacketEssentiaNetworkSync::typeKey,
            ByteBufCodecs.VAR_INT, PacketEssentiaNetworkSync::amount,
            ByteBufCodecs.FLOAT, PacketEssentiaNetworkSync::scale,
            PacketEssentiaNetworkSync::new
    );
    private static final Map<UUID, PacketEssentiaNetworkSync> pendingSyncs = new HashMap<>();

    public PacketEssentiaNetworkSync(EssentiaNetwork network) {
        this(network.getUUID(), typeKeyOf(network.getStoredType()), network.getStored(), network.currentScale);
    }

    private static String typeKeyOf(@Nullable Holder<IAspect> type) {
        return type == null ? "" : type.unwrapKey().map(key -> key.identifier().toString()).orElse("");
    }

    static void queueOrApply(PacketEssentiaNetworkSync sync) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (TransmitterNetworkRegistry.getClientNetwork(sync.networkID()) instanceof EssentiaNetwork network) {
            sync.applyTo(network, level);
        } else {
            pendingSyncs.put(sync.networkID(), sync);
        }
    }

    public static void applyPending(UUID networkID) {
        PacketEssentiaNetworkSync sync = pendingSyncs.remove(networkID);
        if (sync == null) {
            return;
        }
        Level level = Minecraft.getInstance().level;
        if (level != null && TransmitterNetworkRegistry.getClientNetwork(networkID) instanceof EssentiaNetwork network) {
            sync.applyTo(network, level);
        }
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(TYPE, STREAM_CODEC, (payload, context) ->
                context.enqueueWork(() -> queueOrApply(payload)));
    }

    @Override
    public CustomPacketPayload.@NonNull Type<PacketEssentiaNetworkSync> type() {
        return TYPE;
    }

    public void applyTo(EssentiaNetwork network, Level level) {
        Holder<IAspect> holder = null;
        if (!typeKey.isEmpty()) {
            try {
                holder = Aspects.resolve(level, ResourceKey.create(IAspect.REGISTRY_KEY, Identifier.parse(typeKey)));
            } catch (Exception _) {
            }
        }
        network.setClientContents(holder, amount);
        network.currentScale = scale;
    }
}
