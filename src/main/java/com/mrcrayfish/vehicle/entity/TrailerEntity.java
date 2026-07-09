/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.server.level.ChunkMap$TrackedEntity
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class TrailerEntity
extends VehicleEntity {
    public static final EntityDataAccessor<Integer> PULLING_ENTITY = SynchedEntityData.defineId(TrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private Entity pullingEntity;
    private int clientPendingPullingEntityId = -1;
    public float wheelRotation;
    public float prevWheelRotation;

    public float maxUpStep() {
        return 1.0f;
    }

    public TrailerEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PULLING_ENTITY, -1);
    }

    public boolean isPickable() {
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (this.level().isClientSide() && key.equals(PULLING_ENTITY)) {
            int requestedPullingEntityId = (Integer)this.entityData.get(PULLING_ENTITY);
            if (requestedPullingEntityId == -1) {
                this.resetPullingOrMaybeTrailer();
            } else {
                this.clientPendingPullingEntityId = requestedPullingEntityId;
            }
        }
    }

    @Override
    public void onUpdateVehicle() {
        Entity potentialPullingEntity;
        if (this.level().isClientSide() && this.clientPendingPullingEntityId != -1 && ((potentialPullingEntity = this.level().getEntity(this.clientPendingPullingEntityId)) instanceof Player || potentialPullingEntity instanceof VehicleEntity && ((VehicleEntity)potentialPullingEntity).canTowTrailer())) {
            this.clientPendingPullingEntityId = -1;
            this.setPullingOrMaybeTrailer(potentialPullingEntity);
        }
        this.prevWheelRotation = this.wheelRotation;
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x(), motion.y() - 0.08, motion.z());
        if (!this.level().isClientSide() && this.pullingEntity != null) {
            double threshold = (Double)Config.SERVER.trailerDetachThreshold.get() + Math.abs(this.getHitchOffset() / 16.0) * this.getProperties().getBodyPosition().getScale();
            if ((double)this.pullingEntity.distanceTo((Entity)this) > threshold) {
                this.level().playSound(null, this.pullingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
                this.resetPullingOrMaybeTrailer();
            }
        }
        if (!this.level().isClientSide() && this.pullingEntity != null && (!this.pullingEntity.isAlive() || this.pullingEntity instanceof VehicleEntity && ((VehicleEntity)this.pullingEntity).getTrailer() != null && !((VehicleEntity)this.pullingEntity).getTrailer().equals(this))) {
            this.resetPullingOrMaybeTrailer();
        }
        if (this.pullingEntity != null) {
            this.updatePullingMotion();
        } else {
            motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x() * 0.75, motion.y(), motion.z() * 0.75);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        this.hurtMarked = true;
        this.checkInsideBlocks();
        float speed = (float)(Math.sqrt(Math.pow(this.getX() - this.xo, 2.0) + Math.pow(this.getY() - this.yo, 2.0) + Math.pow(this.getZ() - this.zo, 2.0)) * 20.0);
        this.wheelRotation -= 90.0f * (speed / 10.0f);
    }

    private void updatePullingMotion() {
        Vec3 towBar = this.pullingEntity.position();
        if (this.pullingEntity instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)this.pullingEntity;
            Vec3 towBarVec = vehicle.getProperties().getTowBarPosition();
            towBarVec = new Vec3(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + vehicle.getProperties().getBodyPosition().getZ());
            if (vehicle instanceof LandVehicleEntity) {
                LandVehicleEntity landVehicle = (LandVehicleEntity)vehicle;
                towBar = towBar.add(towBarVec.yRot((float)Math.toRadians(-vehicle.getYRot() + landVehicle.additionalYaw)));
            } else {
                towBar = towBar.add(towBarVec.yRot((float)Math.toRadians(-vehicle.getYRot())));
            }
        }
        this.setYRot((float)Math.toDegrees(Math.atan2(towBar.z - this.getZ(), towBar.x - this.getX()) - Math.toRadians(90.0)));
        double deltaRot = this.yRotO - this.getYRot();
        if (deltaRot < -180.0) {
            this.yRotO += 360.0f;
        } else if (deltaRot >= 180.0) {
            this.yRotO -= 360.0f;
        }
        double hitchLength = Math.abs(this.getHitchOffset() * 0.0625);
        double verticalDistance = towBar.y - this.getY();
        double horizontalLength = Math.sqrt(Math.max(0.0, hitchLength * hitchLength - verticalDistance * verticalDistance));
        double signedHorizontalLength = Math.copySign(horizontalLength, this.getHitchOffset());
        Vec3 vec = new Vec3(0.0, 0.0, signedHorizontalLength).yRot((float)Math.toRadians(-this.getYRot())).add(towBar);
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(vec.x - this.getX(), motion.y(), vec.z - this.getZ());
        this.move(MoverType.SELF, this.getDeltaMovement());
        double horizontalDistance = Math.sqrt(Math.pow(towBar.x - this.getX(), 2.0) + Math.pow(towBar.z - this.getZ(), 2.0));
        double pitchVerticalDistance = towBar.y - this.getY();
        float targetPitch = (float)Math.toDegrees(Math.atan2(pitchVerticalDistance, Math.max(horizontalDistance, 0.001)));
        targetPitch = Mth.clamp((float)targetPitch, (float)-20.0f, (float)20.0f);
        this.setXRot(this.getXRot() + (targetPitch - this.getXRot()) * 0.25f);
    }

    public boolean broadcastToPlayer(ServerPlayer player) {
        if (this.pullingEntity != null) {
            if (this.pullingEntity == player) {
                return true;
            }
            ChunkMap.TrackedEntity trackedPullingEntity = (ChunkMap.TrackedEntity)player.serverLevel().getChunkSource().chunkMap.entityMap.get(this.pullingEntity.getId());
            if (trackedPullingEntity == null) {
                return false;
            }
            if (!trackedPullingEntity.seenBy.contains(player.connection)) {
                return false;
            }
        }
        return true;
    }

    public double getPassengersRidingOffset() {
        return 0.0;
    }

    public void resetPullingOrMaybeTrailer() {
        if (this.pullingEntity instanceof VehicleEntity) {
            ((VehicleEntity)this.pullingEntity).setTrailerAndPulling(null);
        } else {
            this.setPulling(null);
        }
    }

    public void setPullingOrMaybeTrailer(Entity entity) {
        this.resetPullingOrMaybeTrailer();
        if (entity instanceof VehicleEntity) {
            ((VehicleEntity)entity).setTrailerAndPulling(this);
        } else {
            this.setPulling(entity);
        }
    }

    public void setPulling(@Nullable Entity pulling) {
        if (pulling instanceof Player || pulling instanceof VehicleEntity && pulling.getVehicle() == null && ((VehicleEntity)pulling).canTowTrailer()) {
            if (!this.level().isClientSide()) {
                this.entityData.set(PULLING_ENTITY, pulling.getId());
            }
            this.pullingEntity = pulling;
        } else {
            if (!this.level().isClientSide()) {
                this.entityData.set(PULLING_ENTITY, -1);
            }
            this.pullingEntity = null;
        }
    }

    @Nullable
    public Entity getPullingEntity() {
        return this.pullingEntity;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 1;
    }

    @Override
    public boolean canMountTrailer() {
        return false;
    }

    public abstract double getHitchOffset();

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    public boolean shouldBeSaved() {
        return (this.pullingEntity == null || this.pullingEntity instanceof Player) && super.shouldBeSaved();
    }

    public boolean save(CompoundTag compound) {
        return (this.pullingEntity == null || this.pullingEntity instanceof Player) && super.save(compound);
    }
}

