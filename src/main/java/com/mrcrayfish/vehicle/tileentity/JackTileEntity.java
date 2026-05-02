package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.JackBlock;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class JackTileEntity extends TileEntitySynced
{
    public static final int MAX_LIFT_PROGRESS = 20;

    private EntityJack jack = null;

    private boolean activated = false;
    public int prevLiftProgress;
    public int liftProgress;

    public JackTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.JACK.get(), pos, state);
    }

    public void setVehicle(VehicleEntity vehicle)
    {
        this.jack = new EntityJack(ModEntities.JACK.get(), this.level, this.worldPosition, 11 * 0.0625, vehicle.getYRot());
        vehicle.startRiding(this.jack, true);
        this.jack.rideTick();
        this.level.addFreshEntity(this.jack);
    }

    @Nullable
    public EntityJack getJack()
    {
        return this.jack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, JackTileEntity blockEntity)
    {
        if(!blockEntity.activated && blockEntity.liftProgress == 0 && blockEntity.prevLiftProgress == 1)
        {
            blockEntity.level.setBlock(blockEntity.worldPosition, blockEntity.getBlockState().setValue(JackBlock.ENABLED, false), Block.UPDATE_ALL);
        }

        blockEntity.prevLiftProgress = blockEntity.liftProgress;

        if(blockEntity.jack == null)
        {
            List<EntityJack> jacks = blockEntity.level.getEntitiesOfClass(EntityJack.class, new AABB(blockEntity.worldPosition));
            if(jacks.size() > 0)
            {
                blockEntity.jack = jacks.get(0);
            }
        }

        if(blockEntity.jack != null && (blockEntity.jack.getPassengers().isEmpty() || !blockEntity.jack.isAlive()))
        {
            blockEntity.jack = null;
        }

        if(blockEntity.jack != null)
        {
            if(blockEntity.jack.getPassengers().size() > 0)
            {
                if(!blockEntity.activated)
                {
                    blockEntity.level.playSound(null, blockEntity.worldPosition, ModSounds.BLOCK_JACK_HEAD_UP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    blockEntity.activated = true;
                    blockEntity.level.setBlock(blockEntity.worldPosition, blockEntity.getBlockState().setValue(JackBlock.ENABLED, true), Block.UPDATE_ALL);
                }
            }
            else if(blockEntity.activated)
            {
                blockEntity.level.playSound(null, blockEntity.worldPosition, ModSounds.BLOCK_JACK_HEAD_DOWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                blockEntity.activated = false;
            }
        }
        else if(blockEntity.activated)
        {
            blockEntity.level.playSound(null, blockEntity.worldPosition, ModSounds.BLOCK_JACK_HEAD_DOWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            blockEntity.activated = false;
        }

        if(blockEntity.activated)
        {
            if(blockEntity.liftProgress < MAX_LIFT_PROGRESS)
            {
                blockEntity.liftProgress++;
                blockEntity.moveCollidedEntities();
            }
        }
        else if(blockEntity.liftProgress > 0)
        {
            blockEntity.liftProgress--;
            blockEntity.moveCollidedEntities();
        }
    }

    private void moveCollidedEntities()
    {
        BlockState state = this.level.getBlockState(this.getBlockPos());
        if(state.getBlock() instanceof JackBlock)
        {
            AABB boundingBox = state.getShape(this.level, this.worldPosition).bounds().move(this.worldPosition);
            List<Entity> list = this.level.getEntities(this.jack, boundingBox);
            if(!list.isEmpty())
            {
                for(Entity entity : list)
                {
                    if(entity.getPistonPushReaction() != PushReaction.IGNORE)
                    {
                        AABB entityBoundingBox = entity.getBoundingBox();
                        double posY = boundingBox.maxY - entityBoundingBox.minY;
                        entity.move(MoverType.PISTON, new Vec3(0.0, posY, 0.0));
                    }
                }
            }
        }
    }

    public float getProgress()
    {
        return (float) this.liftProgress / (float) MAX_LIFT_PROGRESS;
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}
