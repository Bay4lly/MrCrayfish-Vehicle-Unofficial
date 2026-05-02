package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class TrailerEntity extends VehicleEntity
{
    public static final EntityDataAccessor<Integer> PULLING_ENTITY = SynchedEntityData.defineId(TrailerEntity.class, EntityDataSerializers.INT);

    private Entity pullingEntity;
    private int clientPendingPullingEntityId = -1;

    public float wheelRotation;
    public float prevWheelRotation;

    public TrailerEntity(EntityType<?> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(PULLING_ENTITY, -1);
    }

    @Override
    public boolean isPickable()
    {
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if(this.level().isClientSide() && key.equals(PULLING_ENTITY))
        {
            int requestedPullingEntityId = this.entityData.get(PULLING_ENTITY);
            if(requestedPullingEntityId == -1)
            {
                this.resetPullingOrMaybeTrailer();
            }
            else
            {
                this.clientPendingPullingEntityId = requestedPullingEntityId;
            }
        }
    }

    @Override
    public void onUpdateVehicle()
    {
        if(this.level().isClientSide() && this.clientPendingPullingEntityId != -1)
        {
            Entity potentialPullingEntity = this.level().getEntity(this.clientPendingPullingEntityId);
            if(potentialPullingEntity instanceof Player || (potentialPullingEntity instanceof VehicleEntity && ((VehicleEntity) potentialPullingEntity).canTowTrailer()))
            {
                this.clientPendingPullingEntityId = -1;
                this.setPullingOrMaybeTrailer(potentialPullingEntity);
            }
        }

        this.prevWheelRotation = this.wheelRotation;

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x(), motion.y() - 0.08, motion.z());

        if(!this.level().isClientSide() && this.pullingEntity != null)
        {
            double threshold = Config.SERVER.trailerDetachThreshold.get() + Math.abs(this.getHitchOffset() / 16.0) * this.getProperties().getBodyPosition().getScale();
            if(this.pullingEntity.distanceTo(this) > threshold)
            {
                this.level().playSound(null, this.pullingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                this.resetPullingOrMaybeTrailer();
            }
        }

        if(!this.level().isClientSide() && this.pullingEntity != null && (!this.pullingEntity.isAlive() || (this.pullingEntity instanceof VehicleEntity && ((VehicleEntity) this.pullingEntity).getTrailer() != null && !((VehicleEntity) this.pullingEntity).getTrailer().equals(this))))
        {
            this.resetPullingOrMaybeTrailer();
        }

        if(this.pullingEntity != null)
        {
            this.updatePullingMotion();
        }
        else
        {
            motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x() * 0.75, motion.y(), motion.z() * 0.75);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        this.hurtMarked = true;

        this.checkInsideBlocks();

        float speed = (float) (Math.sqrt(Math.pow(this.getX() - this.xo, 2) + Math.pow(this.getY() - this.yo, 2) + Math.pow(this.getZ() - this.zo, 2)) * 20);
        wheelRotation -= 90F * (speed / 10F);
    }

    private void updatePullingMotion()
    {
        Vec3 towBar = this.pullingEntity.position();
        if(this.pullingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) this.pullingEntity;
            Vec3 towBarVec = vehicle.getProperties().getTowBarPosition();
            towBarVec = new Vec3(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + vehicle.getProperties().getBodyPosition().getZ());
            if(vehicle instanceof LandVehicleEntity)
            {
                LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                towBar = towBar.add(towBarVec.yRot((float) Math.toRadians(-vehicle.getYRot() + landVehicle.additionalYaw)));
            }
            else
            {
                towBar = towBar.add(towBarVec.yRot((float) Math.toRadians(-vehicle.getYRot())));
            }
        }

        this.setYRot((float) Math.toDegrees(Math.atan2(towBar.z - this.getZ(), towBar.x - this.getX()) - Math.toRadians(90F)));
        double deltaRot = (double) (this.yRotO - this.getYRot());
        if (deltaRot < -180.0D)
        {
            this.yRotO += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.yRotO -= 360.0F;
        }

        double hitchLength = Math.abs(this.getHitchOffset() * 0.0625);
        double verticalDistance = towBar.y - this.getY();
        double horizontalLength = Math.sqrt(Math.max(0.0, hitchLength * hitchLength - verticalDistance * verticalDistance));
        double signedHorizontalLength = Math.copySign(horizontalLength, this.getHitchOffset());
        Vec3 vec = new Vec3(0, 0, signedHorizontalLength).yRot((float) Math.toRadians(-this.getYRot())).add(towBar);
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(vec.x - this.getX(), motion.y(), vec.z - this.getZ());
        this.move(MoverType.SELF, this.getDeltaMovement());

        // Add a small physical-like pitch so trailers tilt on height changes.
        double horizontalDistance = Math.sqrt(Math.pow(towBar.x - this.getX(), 2) + Math.pow(towBar.z - this.getZ(), 2));
        double pitchVerticalDistance = towBar.y - this.getY();
        float targetPitch = (float) Math.toDegrees(Math.atan2(pitchVerticalDistance, Math.max(horizontalDistance, 0.001)));
        targetPitch = Mth.clamp(targetPitch, -20.0F, 20.0F);
        this.setXRot(this.getXRot() + (targetPitch - this.getXRot()) * 0.25F);
    }

    @Override
    public boolean broadcastToPlayer(ServerPlayer player)
    {
        if(this.pullingEntity != null)
        {
            // Always allow the owner of the pulling link to receive updates.
            // Without this, a trailer being pulled by a player can become invisible
            // to that same player due to tracking order edge-cases.
            if(this.pullingEntity == player)
            {
                return true;
            }

            TrackedEntity trackedPullingEntity = player.serverLevel().getChunkSource().chunkMap.entityMap.get(this.pullingEntity.getId());
            if(trackedPullingEntity == null)
            {
                return false;
            }

            if(!trackedPullingEntity.seenBy.contains(player.connection))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return 0.0;
    }

    public void resetPullingOrMaybeTrailer()
    {
        if(this.pullingEntity instanceof VehicleEntity)
        {
            ((VehicleEntity) this.pullingEntity).setTrailerAndPulling(null);
        }
        else
        {
            this.setPulling(null);
        }
    }

    public void setPullingOrMaybeTrailer(Entity entity)
    {
        this.resetPullingOrMaybeTrailer();

        if(entity instanceof VehicleEntity)
        {
            ((VehicleEntity) entity).setTrailerAndPulling(this);
        }
        else
        {
            this.setPulling(entity);
        }
    }

    public void setPulling(@Nullable Entity pulling)
    {
        if(pulling instanceof Player || (pulling instanceof VehicleEntity && pulling.getVehicle() == null && ((VehicleEntity) pulling).canTowTrailer()))
        {
            if(!this.level().isClientSide())
            {
                this.entityData.set(PULLING_ENTITY, pulling.getId(), true);
            }
            this.pullingEntity = pulling;
        }
        else
        {
            if(!this.level().isClientSide())
            {
                this.entityData.set(PULLING_ENTITY, -1, true);
            }
            this.pullingEntity = null;
        }
    }

    @Nullable
    public Entity getPullingEntity()
    {
        return pullingEntity;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 1;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    public abstract double getHitchOffset();

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return false;
    }

    // TODO save player link

    @Override
    public boolean shouldBeSaved()
    {
        return (this.pullingEntity == null || this.pullingEntity instanceof Player) && super.shouldBeSaved();
    }

    @Override
    public boolean save(CompoundTag compound)
    {
        return (this.pullingEntity == null || this.pullingEntity instanceof Player) && super.save(compound);
    }
}
