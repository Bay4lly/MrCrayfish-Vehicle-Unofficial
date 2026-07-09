/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$MoveFunction
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class EntityJack
extends Entity
implements IEntityWithComplexSpawn {
    private double initialX;
    private double initialY;
    private double initialZ;
    private boolean activated = false;
    private int liftProgress;

    public EntityJack(EntityType<? extends EntityJack> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public EntityJack(EntityType<? extends EntityJack> type, Level worldIn, BlockPos pos, double yOffset, float yaw) {
        this(type, worldIn);
        this.setPos((double)pos.getX() + 0.5, (double)pos.getY() + yOffset, (double)pos.getZ() + 0.5);
        this.setRot(yaw, 0.0f);
        this.initialX = (double)pos.getX() + 0.5;
        this.initialY = (double)pos.getY() + yOffset;
        this.initialZ = (double)pos.getZ() + 0.5;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void tick() {
        BlockEntity tileEntity;
        super.tick();
        if (!this.level().isClientSide && this.getPassengers().size() == 0) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
        if (!this.isAlive()) {
            return;
        }
        if (!this.activated && this.getPassengers().size() > 0) {
            this.activated = true;
        }
        if (this.activated) {
            if (this.liftProgress < 10) {
                ++this.liftProgress;
            }
        } else if (this.liftProgress > 0) {
            --this.liftProgress;
        }
        if ((tileEntity = this.level().getBlockEntity(BlockPos.containing((double)this.initialX, (double)this.initialY, (double)this.initialZ))) instanceof JackTileEntity) {
            JackTileEntity jackTileEntity = (JackTileEntity)tileEntity;
            this.setPos(this.initialX, this.initialY + 0.5 * ((double)jackTileEntity.liftProgress / 20.0), this.initialZ);
        }
    }

    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.getPassengers().contains(passenger)) {
            passenger.xo = this.getX();
            passenger.yo = this.getY();
            passenger.zo = this.getZ();
            passenger.xOld = this.getX();
            passenger.yOld = this.getY();
            passenger.zOld = this.getZ();
        }
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (passenger instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)passenger;
            Vec3 heldOffset = vehicle.getProperties().getHeldOffset().yRot(passenger.getYRot() * ((float)Math.PI / 180));
            moveFunction.accept((Entity)vehicle, this.getX() - heldOffset.z * 0.0625, this.getY() - heldOffset.y * 0.0625 - 0.125, this.getZ() - heldOffset.x * 0.0625);
        }
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.initialX = compound.getDouble("initialX");
        this.initialY = compound.getDouble("initialY");
        this.initialZ = compound.getDouble("initialZ");
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putDouble("initialX", this.initialX);
        compound.putDouble("initialY", this.initialY);
        compound.putDouble("initialZ", this.initialZ);
    }

    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeDouble(this.initialX);
        buffer.writeDouble(this.initialY);
        buffer.writeDouble(this.initialZ);
    }

    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.initialX = buffer.readDouble();
        this.initialY = buffer.readDouble();
        this.initialZ = buffer.readDouble();
        this.moveTo(this.initialX, this.initialY, this.initialZ, this.getYRot(), this.getXRot());
        this.xo = this.initialX;
        this.yo = this.initialY;
        this.zo = this.initialZ;
        this.xOld = this.initialX;
        this.yOld = this.initialY;
        this.zOld = this.initialZ;
    }
}

