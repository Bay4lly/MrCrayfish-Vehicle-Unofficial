/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class CompactHelicopterEntity
extends HelicopterEntity {
    private static final Vec3 EXHAUST_OFFSET = new Vec3(-9.5564, 23.5, -38.1927);

    public CompactHelicopterEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setKeyNeeded(false);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_VEHICLE_HELICOPTER_ROTOR.get();
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    public boolean shouldRenderFuelPort() {
        return true;
    }

    @Override
    public PoweredVehicleEntity.FuelPortType getFuelPortType() {
        return PoweredVehicleEntity.FuelPortType.SMALL;
    }

    @Override
    public void onClientUpdate() {
        float bladeSpeed;
        super.onClientUpdate();
        if (this.canDrive() && this.tickCount % 2 == 0) {
            Vec3 fumePosition = EXHAUST_OFFSET.scale(0.0625).xRot((float)Math.toRadians(this.bodyRotationZ)).yRot((float)Math.toRadians(-this.getYRot()));
            this.level().addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, this.getX() + fumePosition.x, this.getY() + fumePosition.y, this.getZ() + fumePosition.z, -this.getDeltaMovement().x, 0.0, -this.getDeltaMovement().z);
        }
        if ((bladeSpeed = this.getBladeSpeedNormal() * 60.0f) > 30.0f) {
            double bladeScale = (double)bladeSpeed * 0.001;
            double spreadRange = 8.0;
            double randX = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double randZ = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double posX = this.getX() + randX;
            double posZ = this.getZ() + randZ;
            double downDistance = Math.min(12.0, (double)bladeSpeed / 15.0);
            downDistance = downDistance * 0.5 + downDistance * 0.5 * this.random.nextDouble();
            Vec3 start = new Vec3(posX, this.getY() + 3.0, posZ);
            Vec3 end = start.subtract(0.0, downDistance, 0.0);
            BlockHitResult result = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, CollisionContext.empty()));
            if (result.getType() != HitResult.Type.MISS) {
                Vec3 loc = result.getLocation();
                double distanceScale = (downDistance - start.distanceTo(loc)) / downDistance;
                BlockState state = this.level().getBlockState(result.getBlockPos());
                if (!state.getFluidState().isEmpty()) {
                    this.level().addParticle((ParticleOptions)ParticleTypes.SPLASH, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level().addParticle((ParticleOptions)ParticleTypes.BUBBLE, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level().addParticle((ParticleOptions)ParticleTypes.CLOUD, loc.x, loc.y, loc.z, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}

