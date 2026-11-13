package com.blackmoss.mekanismthaumaturge.common.essentia;

import com.blackmoss.mekanismthaumaturge.common.MTTransmission;
import com.blackmoss.mekanismthaumaturge.registry.MTBlocks;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.essentia.IEssentiaTransport;
import mekanism.api.tier.BaseTier;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TileEntityEssentiaTube extends TileEntityTransmitter implements IEssentiaTransport {
    @Nullable
    private Holder<IAspect> suctionType;
    private int suctionAmount;

    public TileEntityEssentiaTube(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected @NonNull EssentiaTube createTransmitter(@NonNull Holder<Block> blockProvider) {
        return new EssentiaTube(blockProvider, this);
    }

    @Override
    public @NonNull EssentiaTube getTransmitter() {
        return (EssentiaTube) super.getTransmitter();
    }

    @Override
    public void setLevel(@NonNull Level level) {
        super.setLevel(level);
        getTransmitter().resolvePendingType(level);
    }

    @Override
    protected void onUpdateServer(@NonNull ServerLevel level) {
        getTransmitter().pullFromAcceptors(level);
        super.onUpdateServer(level);
    }

    @Override
    public @NonNull TransmitterType getTransmitterType() {
        return MTTransmission.ESSENTIA_TUBE;
    }

    @Override
    protected @NonNull BlockState upgradeResult(@NonNull BlockState current, BaseTier tier) {
        return BlockStateHelper.copyStateData(current, switch (tier) {
            case BASIC -> MTBlocks.BASIC_ESSENTIA_TUBE;
            case ADVANCED -> MTBlocks.ADVANCED_ESSENTIA_TUBE;
            case ELITE -> MTBlocks.ELITE_ESSENTIA_TUBE;
            case ULTIMATE -> MTBlocks.ULTIMATE_ESSENTIA_TUBE;
            default -> null;
        });
    }

    @Override
    public int addEssentia(Holder<IAspect> type, int count, Direction side) {
        if (type == null || side == null || count <= 0) {
            return 0;
        }
        return getTransmitter().insertEssentia(type, count);
    }

    @Override
    public int takeEssentia(Holder<IAspect> type, int count, Direction side) {
        if (type == null || side == null || count <= 0) {
            return 0;
        }
        return getTransmitter().extractEssentia(type, count);
    }

    @Override
    public @Nullable Holder<IAspect> getEssentiaType(@Nullable Direction side) {
        return getTransmitter().getStoredType();
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction side) {
        return getTransmitter().getStored();
    }

    @Override
    public void setSuction(@Nullable Holder<IAspect> type, int count) {
        if (count <= 0) {
            suctionType = null;
            suctionAmount = 0;
        } else {
            suctionType = type;
            suctionAmount = count;
        }
    }

    @Override
    public @Nullable Holder<IAspect> getSuctionType(@Nullable Direction side) {
        return suctionType;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction side) {
        return suctionAmount;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean canOutputTo(Direction side) {
        if (side == null) {
            return false;
        }
        EssentiaTube tube = getTransmitter();
        return tube.isConnectionType(side, ConnectionType.NORMAL) || tube.isConnectionType(side, ConnectionType.PUSH);
    }

    @Override
    public boolean canInputFrom(Direction side) {
        if (side == null) {
            return false;
        }
        EssentiaTube tube = getTransmitter();
        return tube.isConnectionType(side, ConnectionType.NORMAL) || tube.isConnectionType(side, ConnectionType.PULL);
    }

    @Override
    public boolean isConnectable(Direction side) {
        return side != null && !getTransmitter().isConnectionType(side, ConnectionType.NONE);
    }

    @Override
    public int spaceFor(Holder<IAspect> type, Direction side) {
        if (type == null || side == null) {
            return 0;
        }
        EssentiaTube tube = getTransmitter();
        Holder<IAspect> held = tube.getStoredType();
        if (held != null && !held.equals(type)) {
            return 0;
        }
        return Math.max(0, tube.getStoredCapacity() - tube.getStored());
    }
}
