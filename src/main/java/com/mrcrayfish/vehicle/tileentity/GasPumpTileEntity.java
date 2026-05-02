package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.util.TileEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class GasPumpTileEntity extends TileEntitySynced
{
    private int fuelingEntityId;
    private Player fuelingEntity;

    private HermiteInterpolator cachedSpline;
    private boolean recentlyUsed;

    public GasPumpTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.GAS_PUMP.get(), pos, state);
    }

    public HermiteInterpolator getCachedSpline()
    {
        return cachedSpline;
    }

    public void setCachedSpline(HermiteInterpolator cachedSpline)
    {
        this.cachedSpline = cachedSpline;
    }

    public boolean isRecentlyUsed()
    {
        return recentlyUsed;
    }

    public void setRecentlyUsed(boolean recentlyUsed)
    {
        this.recentlyUsed = recentlyUsed;
    }

    @Nullable
    public FluidTank getTank()
    {
        BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.below());
        if(tileEntity instanceof GasPumpTankTileEntity)
        {
            return ((GasPumpTankTileEntity) tileEntity).getFluidTank();
        }
        return null;
    }

    public Player getFuelingEntity()
    {
        return this.fuelingEntity;
    }

    public void setFuelingEntity(@Nullable Player entity)
    {
        if(!this.level.isClientSide)
        {
            if(this.fuelingEntity != null)
            {
                ModDataKeys.GAS_PUMP.setValue(this.fuelingEntity, Optional.empty());
            }
            this.fuelingEntity = null;
            this.fuelingEntityId = -1;
            if(entity != null)
            {
                this.fuelingEntityId = entity.getId();
                ModDataKeys.GAS_PUMP.setValue(entity, Optional.of(this.getBlockPos()));
            }
            this.syncToClient();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GasPumpTileEntity blockEntity)
    {
        if(blockEntity.fuelingEntityId != -1)
        {
            if(blockEntity.fuelingEntity == null)
            {
                Entity entity = blockEntity.level.getEntity(blockEntity.fuelingEntityId);
                if(entity instanceof Player)
                {
                    blockEntity.fuelingEntity = (Player) entity;
                }
                else if(!blockEntity.level.isClientSide)
                {
                    blockEntity.fuelingEntityId = -1;
                    blockEntity.syncFuelingEntity();
                }
            }
        }
        else if(blockEntity.level.isClientSide && blockEntity.fuelingEntity != null)
        {
            blockEntity.fuelingEntity = null;
        }

        if(!blockEntity.level.isClientSide && blockEntity.fuelingEntity != null)
        {
            if(Math.sqrt(blockEntity.fuelingEntity.distanceToSqr(blockEntity.worldPosition.getX() + 0.5, blockEntity.worldPosition.getY() + 0.5, blockEntity.worldPosition.getZ() + 0.5)) > Config.SERVER.maxHoseDistance.get() || !blockEntity.fuelingEntity.isAlive())
            {
                if(blockEntity.fuelingEntity.isAlive())
                {
                    blockEntity.level.playSound(null, blockEntity.fuelingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                ModDataKeys.GAS_PUMP.setValue(blockEntity.fuelingEntity, Optional.empty());
                blockEntity.fuelingEntityId = -1;
                blockEntity.fuelingEntity = null;
                blockEntity.syncFuelingEntity();
            }
        }
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        if(compound.contains("FuelingEntity", Tag.TAG_INT))
        {
            this.fuelingEntityId = compound.getInt("FuelingEntity");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        compound.putInt("FuelingEntity", this.fuelingEntityId);
    }

    private void syncFuelingEntity()
    {
        CompoundTag compound = new CompoundTag();
        this.saveAdditional(compound);
        TileEntityUtil.sendUpdatePacket(this, compound);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}
