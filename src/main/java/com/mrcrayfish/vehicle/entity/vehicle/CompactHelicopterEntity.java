package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class CompactHelicopterEntity extends HelicopterEntity
{
    private static final Vec3 EXHAUST_OFFSET = new Vec3(-9.5564, 23.5, -38.1927);

    public CompactHelicopterEntity(EntityType<?> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.setKeyNeeded(false);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_VEHICLE_HELICOPTER_ROTOR.get();
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean shouldRenderFuelPort()
    {
        return true;
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        if(this.canDrive() && this.tickCount % 2 == 0)
        {
            Vec3 fumePosition = EXHAUST_OFFSET.scale(0.0625)
                    .xRot((float) Math.toRadians(this.bodyRotationZ))
                    .yRot((float) Math.toRadians(-this.getYRot()));
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + fumePosition.x, this.getY() + fumePosition.y, this.getZ() + fumePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
        }

        float bladeSpeed = this.getBladeSpeedNormal() * 60.0F;
        if(bladeSpeed > 30.0F)
        {
            double bladeScale = bladeSpeed * 0.001;
            double spreadRange = 8.0;
            double randX = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double randZ = -(spreadRange / 2.0) + spreadRange * this.random.nextDouble();
            double posX = this.getX() + randX;
            double posZ = this.getZ() + randZ;
            double downDistance = Math.min(12.0, bladeSpeed / 15.0);
            downDistance = (downDistance * 0.5) + (downDistance * 0.5) * this.random.nextDouble();
            Vec3 start = new Vec3(posX, this.getY() + 3.0, posZ);
            Vec3 end = start.subtract(0, downDistance, 0);

            BlockHitResult result = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, null));
            if(result.getType() != HitResult.Type.MISS)
            {
                Vec3 loc = result.getLocation();
                double distanceScale = (downDistance - start.distanceTo(loc)) / downDistance;
                BlockState state = this.level().getBlockState(result.getBlockPos());
                if(!state.getFluidState().isEmpty())
                {
                    this.level().addParticle(ParticleTypes.SPLASH, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level().addParticle(ParticleTypes.BUBBLE, loc.x, loc.y, loc.z, randX * bladeScale * distanceScale, 0.02, randZ * bladeScale * distanceScale);
                    this.level().addParticle(ParticleTypes.CLOUD, loc.x, loc.y, loc.z, 0, 0, 0);
                }
            }
        }
    }
}
