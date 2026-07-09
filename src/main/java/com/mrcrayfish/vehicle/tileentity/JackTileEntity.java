/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.JackBlock;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class JackTileEntity
extends TileEntitySynced {
    public static final int MAX_LIFT_PROGRESS = 20;
    private EntityJack jack = null;
    private boolean activated = false;
    public int prevLiftProgress;
    public int liftProgress;

    public JackTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.JACK.get(), pos, state);
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.jack = new EntityJack((EntityType<? extends EntityJack>)((EntityType)ModEntities.JACK.get()), this.level, this.worldPosition, 0.6875, vehicle.getYRot());
        vehicle.startRiding(this.jack, true);
        this.jack.rideTick();
        this.level.addFreshEntity((Entity)this.jack);
    }

    @Nullable
    public EntityJack getJack() {
        return this.jack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, JackTileEntity blockEntity) {
        List jacks;
        if (!blockEntity.activated && blockEntity.liftProgress == 0 && blockEntity.prevLiftProgress == 1) {
            blockEntity.level.setBlock(blockEntity.worldPosition, (BlockState)blockEntity.getBlockState().setValue(JackBlock.ENABLED, Boolean.valueOf(false)), 3);
        }
        blockEntity.prevLiftProgress = blockEntity.liftProgress;
        if (blockEntity.jack == null && (jacks = blockEntity.level.getEntitiesOfClass(EntityJack.class, new AABB(blockEntity.worldPosition))).size() > 0) {
            blockEntity.jack = (EntityJack)((Object)jacks.get(0));
        }
        if (blockEntity.jack != null && (blockEntity.jack.getPassengers().isEmpty() || !blockEntity.jack.isAlive())) {
            blockEntity.jack = null;
        }
        if (blockEntity.jack != null) {
            if (blockEntity.jack.getPassengers().size() > 0) {
                if (!blockEntity.activated) {
                    blockEntity.level.playSound(null, blockEntity.worldPosition, (SoundEvent)ModSounds.BLOCK_JACK_HEAD_UP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    blockEntity.activated = true;
                    blockEntity.level.setBlock(blockEntity.worldPosition, (BlockState)blockEntity.getBlockState().setValue(JackBlock.ENABLED, Boolean.valueOf(true)), 3);
                }
            } else if (blockEntity.activated) {
                blockEntity.level.playSound(null, blockEntity.worldPosition, (SoundEvent)ModSounds.BLOCK_JACK_HEAD_DOWN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                blockEntity.activated = false;
            }
        } else if (blockEntity.activated) {
            blockEntity.level.playSound(null, blockEntity.worldPosition, (SoundEvent)ModSounds.BLOCK_JACK_HEAD_DOWN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            blockEntity.activated = false;
        }
        if (blockEntity.activated) {
            if (blockEntity.liftProgress < 20) {
                ++blockEntity.liftProgress;
                blockEntity.moveCollidedEntities();
            }
        } else if (blockEntity.liftProgress > 0) {
            --blockEntity.liftProgress;
            blockEntity.moveCollidedEntities();
        }
    }

    private void moveCollidedEntities() {
        AABB boundingBox;
        BlockState state = this.level.getBlockState(this.getBlockPos());
        if (state.getBlock() instanceof JackBlock && !( (List<Entity>)(List)this.level.getEntities((Entity)this.jack, boundingBox = state.getShape((BlockGetter)this.level, this.worldPosition).bounds().move(this.worldPosition))).isEmpty()) {
            List<Entity> list = this.level.getEntities((Entity)this.jack, boundingBox = state.getShape((BlockGetter)this.level, this.worldPosition).bounds().move(this.worldPosition));
            for (Entity entity : list) {
                if (entity.getPistonPushReaction() == PushReaction.IGNORE) continue;
                AABB entityBoundingBox = entity.getBoundingBox();
                double posY = boundingBox.maxY - entityBoundingBox.minY;
                entity.move(MoverType.PISTON, new Vec3(0.0, posY, 0.0));
            }
        }
    }

    public float getProgress() {
        return (float)this.liftProgress / 20.0f;
    }
}

