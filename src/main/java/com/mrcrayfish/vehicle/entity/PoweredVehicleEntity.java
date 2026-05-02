package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.*;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class PoweredVehicleEntity extends VehicleEntity implements ContainerListener, MenuProvider
{
    protected static final EntityDataAccessor<Float> CURRENT_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> MAX_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> ACCELERATION_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> TURN_DIRECTION = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> TARGET_TURN_ANGLE = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> TURN_SENSITIVITY = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> MAX_TURN_ANGLE = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ACCELERATION_DIRECTION = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> HORN = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> REQUIRES_FUEL = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> CURRENT_FUEL = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> FUEL_CAPACITY = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> NEEDS_KEY = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> KEY_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<ItemStack> ENGINE_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<ItemStack> WHEEL_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, EntityDataSerializers.ITEM_STACK);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 0.25F;
    protected boolean charging;
    protected AccelerationDirection prevAcceleration;

    protected double[] wheelPositions;
    protected boolean wheelsOnGround = true;
    public float turnAngle;
    public float prevTurnAngle;

    public float deltaYaw;
    public float wheelAngle;
    public float prevWheelAngle; //TODO can remove use render wheel angle instead

    @OnlyIn(Dist.CLIENT)
    public float targetWheelAngle;
    @OnlyIn(Dist.CLIENT)
    public float renderWheelAngle;
    @OnlyIn(Dist.CLIENT)
    public float prevRenderWheelAngle;
    @OnlyIn(Dist.CLIENT)
    public int wheelieCount;
    @OnlyIn(Dist.CLIENT)
    public int prevWheelieCount;

    public float vehicleMotionX;
    public float vehicleMotionY;
    public float vehicleMotionZ;

    private UUID owner;

    private SimpleContainer vehicleInventory;

    private FuelPortType fuelPortType;
    private boolean fueling;

    protected PoweredVehicleEntity(EntityType<?> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.setMaxUpStep(1.0F);
    }

    public PoweredVehicleEntity(EntityType<?> entityType, Level worldIn, double posX, double posY, double posZ)
    {
        this(entityType, worldIn);
        this.setPos(posX, posY, posZ);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CURRENT_SPEED, 0F);
        this.entityData.define(MAX_SPEED, 10F);
        this.entityData.define(ACCELERATION_SPEED, 0.5F);
        this.entityData.define(POWER, 1.0F);
        this.entityData.define(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.entityData.define(TARGET_TURN_ANGLE, 0F);
        this.entityData.define(TURN_SENSITIVITY, 6);
        this.entityData.define(MAX_TURN_ANGLE, 35);
        this.entityData.define(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        this.entityData.define(HORN, false);
        this.entityData.define(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get());
        this.entityData.define(CURRENT_FUEL, 0F);
        this.entityData.define(FUEL_CAPACITY, 15000F);
        this.entityData.define(NEEDS_KEY, false);
        this.entityData.define(KEY_STACK, ItemStack.EMPTY);
        this.entityData.define(ENGINE_STACK, ItemStack.EMPTY);
        this.entityData.define(WHEEL_STACK, ItemStack.EMPTY);

        List<Wheel> wheels = this.getProperties().getWheels();
        if(wheels != null && wheels.size() > 0)
        {
            this.wheelPositions = new double[wheels.size() * 3];
        }
    }

    public abstract SoundEvent getEngineSound();

    //TODO ability to change with nbt
    public SoundEvent getHornSound()
    {
        return ModSounds.ENTITY_VEHICLE_HORN.get();
    }

    public void playFuelPortOpenSound()
    {
        if(!this.fueling)
        {
            this.fuelPortType.playOpenSound();
            this.fueling = true;
        }
    }

    public void playFuelPortCloseSound()
    {
        if(this.fueling)
        {
            this.fuelPortType.playCloseSound();
            this.fueling = false;
        }
    }

    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    public float getMaxEnginePitch()
    {
        return 1.2F;
    }

    @Override
    public boolean isPickable()
    {
        return true;
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        this.setFuelPortType(FuelPortType.DEFAULT);
    }

    protected void setFuelPortType(FuelPortType fuelPortType)
    {
        this.fuelPortType = fuelPortType;
    }

    public void fuelVehicle(Player player, InteractionHand hand)
    {
        if(ModDataKeys.GAS_PUMP.getValue(player).isPresent())
        {
            BlockPos pos = ModDataKeys.GAS_PUMP.getValue(player).get();
            BlockEntity tileEntity = this.level().getBlockEntity(pos);
            if(!(tileEntity instanceof GasPumpTileEntity))
                return;

            tileEntity = this.level().getBlockEntity(pos.below());
            if(!(tileEntity instanceof GasPumpTankTileEntity))
                return;

            GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity) tileEntity;
            FluidTank tank = gasPumpTank.getFluidTank();
            FluidStack stack = tank.getFluid();
            if(stack.isEmpty() || !Config.SERVER.validFuels.get().contains(ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString())) // FIXME
                return;

            stack = tank.drain(200, IFluidHandler.FluidAction.EXECUTE);
            if(stack.isEmpty())
                return;

            stack.setAmount(this.addFuel(stack.getAmount()));
            if(stack.getAmount() <= 0)
                return;

            gasPumpTank.getFluidTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
            return;
        }

        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof JerryCanItem))
            return;

        JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
        Optional<IFluidHandlerItem> optional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if(!optional.isPresent())
            return;

        IFluidHandlerItem handler = optional.get();
        FluidStack fluidStack = handler.getFluidInTank(0);
        if(fluidStack.isEmpty() || !Config.SERVER.validFuels.get().contains(ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString())) // FIXME
            return;

        int transferAmount = Math.min(handler.getFluidInTank(0).getAmount(), jerryCan.getFillRate());
        transferAmount = (int) Math.min(Math.floor(this.getFuelCapacity() - this.getCurrentFuel()), transferAmount);
        handler.drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
        this.addFuel(transferAmount);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(!level().isClientSide)
        {
            /* If no owner is set, make the owner the person adding the key. It is used because
             * owner will not be set if the vehicle was summoned through a command */
            if(this.owner == null)
            {
                this.owner = player.getUUID();
            }

            if(stack.getItem() == ModItems.KEY.get())
            {
                if(!this.owner.equals(player.getUUID()))
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                    return InteractionResult.FAIL;
                }

                if(this.isLockable())
                {
                    CompoundTag tag = CommonUtils.getOrCreateStackTag(stack);
                    if(!tag.hasUUID("VehicleId") || this.getUUID().equals(tag.getUUID("VehicleId")))
                    {
                        tag.putUUID("VehicleId", this.getUUID());
                        if(!this.isKeyNeeded())
                        {
                            this.setKeyNeeded(true);
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_added");
                        }
                        else
                        {
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_created");
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.not_lockable");
                    return InteractionResult.FAIL;
                }
            }
            else if(stack.getItem() == ModItems.WRENCH.get() && this.getVehicle() instanceof EntityJack)
            {
                if(player.getUUID().equals(owner))
                {
                    this.openEditInventory(player);
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public void onUpdateVehicle()
    {
        this.prevCurrentSpeed = this.currentSpeed;
        this.prevTurnAngle = this.turnAngle;
        this.prevWheelAngle = this.wheelAngle;

        if(this.level().isClientSide)
        {
            this.onClientUpdate();
        }

        Entity controllingPassenger = this.getControllingPassenger();

        /* If there driver, create particles */
        if(controllingPassenger != null)
        {
            this.createParticles();
        }

        /* Makes the vehicle boost slightly from charging up */
        if(this.charging && this.prevAcceleration == AccelerationDirection.CHARGING && this.getAcceleration() != this.prevAcceleration && this.getRealSpeed() > 0.95F)
        {
            this.releaseCharge();
        }

        /* InteractionHandle the current speed of the vehicle based on rider's forward movement */
        this.updateGroundState();
        this.updateSpeed();
        this.updateTurning();
        this.updateVehicle();
        this.setSpeed(this.currentSpeed);

        /* Updates the direction of the vehicle */
        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
        {
            this.setYRot(this.getYRot() - this.deltaYaw);
        }

        /* Updates the vehicle motion and applies it on top of the normal motion */
        this.updateVehicleMotion();

        this.setRot(this.getYRot(), this.getXRot());
        double deltaRot = (double) (this.yRotO - this.getYRot());
        if (deltaRot < -180.0D)
        {
            this.yRotO += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.yRotO -= 360.0F;
        }
        this.updateWheelPositions();

        this.move(MoverType.SELF, this.getDeltaMovement().add(this.vehicleMotionX, this.vehicleMotionY, this.vehicleMotionZ));

        /* Reduces the motion and speed multiplier */
        if(this.onGround())
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.98, 0.8));
        }
        else
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));
        }

        if(this.boostTimer > 0)
        {
            this.boostTimer--;
        }
        else
        {
            this.boosting = false;
            this.speedMultiplier *= 0.85;
        }

        if(this.launchingTimer > 0)
        {
            //Ensures fall damage is disabled while launching
            this.disableFallDamage = true;
            this.launchingTimer--;
        }
        else
        {
            this.launching = false;
        }

        /* Checks for block collisions */
        this.checkInsideBlocks();

        /* Checks for collisions with any other vehicles */
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), entity -> entity instanceof BumperCarEntity);
        if (!list.isEmpty())
        {
            for(Entity entity : list)
            {
                this.push(entity);
            }
        }

        if(this.requiresFuel() && controllingPassenger instanceof Player && !((Player) controllingPassenger).isCreative() && this.isEnginePowered())
        {
            float currentSpeed = Math.abs(Math.min(this.getSpeed(), this.getMaxSpeed()));
            float normalSpeed = Math.max(0.05F, currentSpeed / this.getMaxSpeed());
            float currentFuel = this.getCurrentFuel();
            currentFuel -= this.fuelConsumption * normalSpeed * Config.SERVER.fuelConsumptionFactor.get();
            if(currentFuel < 0F) currentFuel = 0F;
            this.setCurrentFuel(currentFuel);
        }

        this.prevAcceleration = this.getAcceleration();
    }

    public void updateVehicle() {}

    public abstract void updateVehicleMotion();

    public FuelPortType getFuelPortType()
    {
        return FuelPortType.DEFAULT;
    }

    protected void updateSpeed()
    {
        float surfaceModifier = SurfaceHelper.getSurfaceModifier(this);
        this.currentSpeed = this.getSpeed();

        Optional<IEngineTier> optional = this.getEngineTier();
        AccelerationDirection acceleration = this.getAcceleration();

        /* Reset charging to false if acceleration is not charging */
        if(acceleration != AccelerationDirection.CHARGING)
        {
            this.charging = false;
        }

        if(this.getControllingPassenger() != null && optional.isPresent())
        {
            if(this.canDrive())
            {
                boolean charging = this.canCharge() && acceleration == AccelerationDirection.CHARGING && Math.abs(this.currentSpeed) < 0.5F;
                if(acceleration == AccelerationDirection.FORWARD || (charging || this.charging))
                {
                    if(!this.charging)
                    {
                        this.charging = charging;
                    }
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        float maxSpeed = this.getActualMaxSpeed() * surfaceModifier * this.getPower();
                        if(this.currentSpeed < maxSpeed)
                        {
                            IEngineTier engineTier = optional.get();
                            this.currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                            if(this.currentSpeed > maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
                else if(acceleration == AccelerationDirection.REVERSE)
                {
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        IEngineTier engineTier = optional.get();
                        float maxSpeed = -(4.0F + engineTier.getAdditionalMaxSpeed() / 2) * surfaceModifier * this.getPower();;
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed -= this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                            if(this.currentSpeed < maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed < maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
            }

            if(this.wheelsOnGround || this.canAccelerateInAir())
            {
                this.currentSpeed *= 0.9;
            }
            else
            {
                this.currentSpeed *= 0.98;
            }
        }
        else if(this.wheelsOnGround)
        {
            this.currentSpeed *= 0.85;
        }
        else
        {
            this.currentSpeed *= 0.98;
        }
    }

    protected void updateTurning()
    {
        this.turnAngle = this.getTargetTurnAngle();
        this.wheelAngle = this.turnAngle * Math.max(0.45F, 1.0F - Math.abs(this.currentSpeed / 20F));
        this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30F) / 2F;

        if(level().isClientSide)
        {
            this.renderWheelAngle = this.wheelAngle;
        }
    }

    public void createParticles()
    {
        if(this.getAcceleration() == AccelerationDirection.FORWARD || this.charging)
        {
            /* Uses the same logic when rendering wheels to determine the position, then spawns
             * particles at the contact of the wheel and the ground. */
            VehicleProperties properties = this.getProperties();
            if(properties.getWheels() != null)
            {
                List<Wheel> wheels = properties.getWheels();
                for(int i = 0; i < wheels.size(); i++)
                {
                    Wheel wheel = wheels.get(i);
                    if(!wheel.shouldSpawnParticles())
                        continue;
                    /* Gets the block under the wheel and spawns a particle */
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = Mth.floor(this.getX() + wheelX);
                    int y = Mth.floor(this.getY() + wheelY - 0.2D);
                    int z = Mth.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);
                    if(!state.isAir() && state.isSolid())
                    {
                        Vec3 dirVec = this.calculateViewVector(this.getXRot(), this.getModifiedRotationYaw() + 180F).add(0, 0.5, 0);
                        if(this.charging)
                        {
                            dirVec = dirVec.scale(this.currentSpeed / 3F);
                        }
                        if(this.level().isClientSide())
                        {
                            VehicleHelper.spawnWheelParticle(pos, state, this.getX() + wheelX, this.getY() + wheelY, this.getZ() + wheelZ, dirVec);
                        }
                    }
                }
            }
        }

        if(this.shouldShowEngineSmoke()&& this.canDrive() && this.tickCount % 2 == 0)
        {
            Vec3 smokePosition = this.getEngineSmokePosition().yRot(-this.getModifiedRotationYaw() * 0.017453292F);
            this.level().addParticle(ParticleTypes.SMOKE, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            if(this.charging && this.getRealSpeed() > 0.95F)
            {
                this.level().addParticle(ParticleTypes.CRIT, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientUpdate()
    {
        this.prevRenderWheelAngle = this.renderWheelAngle;
        this.prevWheelieCount = this.wheelieCount;

        Entity entity = this.getControllingPassenger();
        if(entity instanceof LivingEntity && entity.equals(Minecraft.getInstance().player))
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            float power = VehicleHelper.getPower(this);
            if(power != this.getPower())
            {
                this.setPower(power);
                PacketHandler.instance.sendToServer(new MessagePower(power));
            }

            AccelerationDirection acceleration = VehicleHelper.getAccelerationDirection(livingEntity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.instance.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean horn = VehicleHelper.isHonking();
            this.setHorn(horn);
            PacketHandler.instance.sendToServer(new MessageHorn(horn));

            TurnDirection direction = VehicleHelper.getTurnDirection(livingEntity);
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.instance.sendToServer(new MessageTurnDirection(direction));
            }

            float targetTurnAngle = VehicleHelper.getTargetTurnAngle(this, false);
            this.setTargetTurnAngle(targetTurnAngle);
            PacketHandler.instance.sendToServer(new MessageTurnAngle(targetTurnAngle));
        }

        if(this.isBoosting() && this.getControllingPassenger() != null)
        {
            if(this.wheelieCount < 4)
            {
                this.wheelieCount++;
            }
        }
        else if(this.wheelieCount > 0)
        {
            this.wheelieCount--;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Owner", Tag.TAG_COMPOUND))
        {
            this.owner = compound.getUUID("Owner");
        }
        if(compound.contains("EngineStack", Tag.TAG_COMPOUND))
        {
            this.setEngineStack(ItemStack.of(compound.getCompound("EngineStack")));
        }
        if(compound.contains("WheelStack", Tag.TAG_COMPOUND))
        {
            this.setWheelStack(ItemStack.of(compound.getCompound("WheelStack")));
        }
        if(compound.contains("MaxSpeed", Tag.TAG_FLOAT))
        {
            this.setMaxSpeed(compound.getFloat("MaxSpeed"));
        }
        if(compound.contains("AccelerationSpeed", Tag.TAG_FLOAT))
        {
            this.setAccelerationSpeed(compound.getFloat("AccelerationSpeed"));
        }
        if(compound.contains("TurnSensitivity", Tag.TAG_INT))
        {
            this.setTurnSensitivity(compound.getInt("TurnSensitivity"));
        }
        if(compound.contains("MaxTurnAngle", Tag.TAG_INT))
        {
            this.setMaxTurnAngle(compound.getInt("MaxTurnAngle"));
        }
        if(compound.contains("StepHeight", Tag.TAG_FLOAT))
        {
            this.setMaxUpStep(compound.getFloat("StepHeight"));
        }
        if(compound.contains("RequiresFuel", Tag.TAG_BYTE))
        {
            this.setRequiresFuel(compound.getBoolean("RequiresFuel"));
        }
        if(compound.contains("CurrentFuel", Tag.TAG_FLOAT))
        {
            this.setCurrentFuel(compound.getFloat("CurrentFuel"));
        }
        if(compound.contains("FuelCapacity", Tag.TAG_INT))
        {
            this.setFuelCapacity(compound.getInt("FuelCapacity"));
        }
        if(compound.contains("KeyNeeded", Tag.TAG_BYTE))
        {
            this.setKeyNeeded(compound.getBoolean("KeyNeeded"));
        }
        this.setKeyStack(CommonUtils.readItemStackFromTag(compound, "KeyStack"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        if(this.owner != null)
        {
            compound.putUUID("Owner", this.owner);
        }
        compound.putBoolean("HasEngine", this.hasEngine());
        CommonUtils.writeItemStackToTag(compound, "EngineStack", this.getEngineStack());
        CommonUtils.writeItemStackToTag(compound, "WheelStack", this.getWheelStack());
        compound.putFloat("MaxSpeed", this.getMaxSpeed());
        compound.putFloat("AccelerationSpeed", this.getAccelerationSpeed());
        compound.putInt("TurnSensitivity", this.getTurnSensitivity());
        compound.putInt("MaxTurnAngle", this.getMaxTurnAngle());
        compound.putFloat("StepHeight", this.maxUpStep());
        compound.putBoolean("RequiresFuel", this.requiresFuel());
        compound.putFloat("CurrentFuel", this.getCurrentFuel());
        compound.putFloat("FuelCapacity", this.getFuelCapacity());
        compound.putBoolean("KeyNeeded", this.isKeyNeeded());
        CommonUtils.writeItemStackToTag(compound, "KeyStack", this.getKeyStack());
    }

    @Nullable
    public LivingEntity getControllingPassenger()
    {
        if(this.getPassengers().isEmpty())
        {
            return null;
        }
        VehicleProperties properties = this.getProperties();
        for(Entity passenger : this.getPassengers())
        {
            if (passenger instanceof LivingEntity)
            {
                int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
                if(seatIndex != -1 && properties.getSeats().get(seatIndex).isDriverSeat())
                {
                    return (LivingEntity) passenger;
                }
            }
        }
        return null;
    }

    @Override
    public void updatePassengerPosition(Entity passenger, MoveFunction moveFunction)
    {
        if(this.hasPassenger(passenger))
        {
            int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
            if(seatIndex != -1)
            {
                VehicleProperties properties = this.getProperties();
                if(seatIndex >= 0 && seatIndex < properties.getSeats().size())
                {
                    Seat seat = properties.getSeats().get(seatIndex);
                    Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625).yRot(-(this.getModifiedRotationYaw() + 180) * 0.017453292F);
                    //Vec3 seatVec = Vec3.ZERO;
                    moveFunction.accept(passenger, this.getX() - seatVec.x, this.getY() + seatVec.y + passenger.getMyRidingOffset(), this.getZ() - seatVec.z);
                    if(this.level().isClientSide() && VehicleHelper.canApplyVehicleYaw(passenger))
                    {
                        passenger.setYRot(passenger.getYRot() - this.deltaYaw);
                        passenger.setYHeadRot(passenger.getYRot());
                    }
                    this.applyYawToEntity(passenger);
                }
            }
        }
    }

    public boolean isMoving()
    {
        return this.currentSpeed != 0;
    }

    public void setMaxSpeed(float maxSpeed)
    {
        this.entityData.set(MAX_SPEED, maxSpeed);
    }

    public float getMaxSpeed()
    {
        return this.entityData.get(MAX_SPEED);
    }

    public float getActualMaxSpeed()
    {
        float maxSpeed = this.entityData.get(MAX_SPEED);
        Optional<IEngineTier> engineTier = this.getEngineTier();
        if(engineTier.isPresent()) maxSpeed += engineTier.get().getAdditionalMaxSpeed();
        return maxSpeed;
    }

    public float getRealSpeed()
    {
        return this.currentSpeed / (this.getActualMaxSpeed() * SurfaceHelper.getSurfaceModifier(this) * this.getPower());
    }

    public void setSpeed(float speed)
    {
        this.entityData.set(CURRENT_SPEED, speed);
    }

    public float getSpeed()
    {
        return this.currentSpeed;
    }

    public float getNormalSpeed()
    {
        return this.currentSpeed / this.getMaxSpeed();
    }

    public float getActualSpeed()
    {
        return (this.currentSpeed + this.currentSpeed * this.speedMultiplier) / this.getActualMaxSpeed();
    }

    public void setAccelerationSpeed(float speed)
    {
        this.entityData.set(ACCELERATION_SPEED, speed);
    }

    public float getAccelerationSpeed()
    {
        return this.entityData.get(ACCELERATION_SPEED);
    }

    protected float getModifiedAccelerationSpeed()
    {
        return this.entityData.get(ACCELERATION_SPEED);
    }

    public double getKilometersPreHour()
    {
        return Math.sqrt(Math.pow(this.getX() - this.xo, 2) + Math.pow(this.getY() - this.yo, 2) + Math.pow(this.getZ() - this.zo, 2)) * 20;
    }

    public void setTurnDirection(TurnDirection turnDirection)
    {
        this.entityData.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection()
    {
        return TurnDirection.values()[this.entityData.get(TURN_DIRECTION)];
    }

    public void setTargetTurnAngle(float targetTurnAngle)
    {
        this.entityData.set(TARGET_TURN_ANGLE, targetTurnAngle);
    }

    public float getTargetTurnAngle()
    {
        return this.entityData.get(TARGET_TURN_ANGLE);
    }

    public void setAcceleration(AccelerationDirection direction)
    {
        this.entityData.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public AccelerationDirection getAcceleration()
    {
        return AccelerationDirection.values()[this.entityData.get(ACCELERATION_DIRECTION)];
    }

    public void setPower(float power)
    {
        this.entityData.set(POWER, Mth.clamp(power, 0.0F, 1.0F));
    }

    public float getPower()
    {
        return this.entityData.get(POWER);
    }

    public void setTurnSensitivity(int sensitivity)
    {
        this.entityData.set(TURN_SENSITIVITY, sensitivity);
    }

    public int getTurnSensitivity()
    {
        return this.entityData.get(TURN_SENSITIVITY);
    }

    public void setMaxTurnAngle(int turnAngle)
    {
        this.entityData.set(MAX_TURN_ANGLE, turnAngle);
    }

    public int getMaxTurnAngle()
    {
        return this.entityData.get(MAX_TURN_ANGLE);
    }

    public boolean hasEngine()
    {
        return !this.getEngineStack().isEmpty();
    }

    public void setEngineStack(ItemStack engine)
    {
        this.entityData.set(ENGINE_STACK, engine);
    }

    public ItemStack getEngineStack()
    {
        return this.entityData.get(ENGINE_STACK);
    }

    public Optional<IEngineTier> getEngineTier()
    {
        return IEngineTier.fromStack(this.getEngineStack());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderEngine()
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderFuelPort()
    {
        return true;
    }

    public Vec3 getEngineSmokePosition()
    {
        return new Vec3(0, 0, 0);
    }

    public boolean shouldShowEngineSmoke()
    {
        return false;
    }

    public void setHorn(boolean activated)
    {
        this.entityData.set(HORN, activated);
    }

    public boolean getHorn()
    {
        return this.entityData.get(HORN);
    }

    public void setBoosting(boolean boosting)
    {
        this.boosting = boosting;
        this.boostTimer = 10;
    }

    public boolean isBoosting()
    {
        return boosting;
    }

    public void setLaunching(int hold)
    {
        this.launching = true;
        this.launchingTimer = hold;
        this.disableFallDamage = true;
    }

    public boolean isLaunching()
    {
        return launching;
    }

    public boolean requiresFuel()
    {
        return Config.SERVER.fuelEnabled.get() && this.entityData.get(REQUIRES_FUEL);
    }

    public void setRequiresFuel(boolean requiresFuel)
    {
        this.entityData.set(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get() && requiresFuel);
    }

    public boolean isFueled()
    {
        return !this.requiresFuel() || this.isControllingPassengerCreative() || this.getCurrentFuel() > 0F;
    }

    public void setCurrentFuel(float fuel)
    {
        this.entityData.set(CURRENT_FUEL, fuel);
    }

    public float getCurrentFuel()
    {
        return this.entityData.get(CURRENT_FUEL);
    }

    public void setFuelCapacity(float capacity)
    {
        this.entityData.set(FUEL_CAPACITY, capacity);
    }

    public float getFuelCapacity()
    {
        return this.entityData.get(FUEL_CAPACITY);
    }

    public void setFuelConsumption(float consumption)
    {
        this.fuelConsumption = consumption;
    }

    public float getFuelConsumption()
    {
        return fuelConsumption;
    }

    public int addFuel(int fuel)
    {
        if(!this.requiresFuel())
            return fuel;
        float currentFuel = this.getCurrentFuel();
        currentFuel += fuel;
        int remaining = Math.max(0, Math.round(currentFuel - this.getFuelCapacity()));
        currentFuel = Math.min(currentFuel, this.getFuelCapacity());
        this.setCurrentFuel(currentFuel);
        return remaining;
    }

    public void setKeyNeeded(boolean needsKey)
    {
        this.entityData.set(NEEDS_KEY, needsKey);
    }

    public boolean isKeyNeeded()
    {
        return this.entityData.get(NEEDS_KEY);
    }

    public void setKeyStack(ItemStack stack)
    {
        this.entityData.set(KEY_STACK, stack);
    }

    public ItemStack getKeyStack()
    {
        return this.entityData.get(KEY_STACK);
    }

    public void ejectKey()
    {
        if(!this.getKeyStack().isEmpty())
        {
            Vec3 keyHole = this.getPartPositionAbsoluteVec(this.getProperties().getKeyPortPosition(), 1F);
            this.level().addFreshEntity(new ItemEntity(this.level(), keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public boolean isLockable()
    {
        return true;
    }

    public boolean isEnginePowered()
    {
        return ((this.getProperties().getEngineType() == EngineType.NONE || this.hasEngine()) && (this.isControllingPassengerCreative() || this.isFueled()) && this.getDestroyedStage() < 9) && (!this.isKeyNeeded() || !this.getKeyStack().isEmpty());
    }

    public boolean canDrive()
    {
        return (!this.canChangeWheels() || this.hasWheelStack()) && this.isEnginePowered();
    }

    public boolean isOwner(Player player)
    {
        return owner == null || player.getUUID().equals(owner);
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public boolean hasWheelStack()
    {
        return !this.getWheelStack().isEmpty();
    }

    public void setWheelStack(ItemStack wheels)
    {
        this.entityData.set(WHEEL_STACK, wheels);
    }

    public ItemStack getWheelStack()
    {
        return this.entityData.get(WHEEL_STACK);
    }

    public Optional<IWheelType> getWheelType()
    {
        return IWheelType.fromStack(this.entityData.get(WHEEL_STACK));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        super.onSyncedDataUpdated(key);
        if(level().isClientSide)
        {
            if(COLOR.equals(key))
            {
                /*Color color = new Color(this.dataManager.get(COLOR)); //TODO move this code to renderer to make fuel port darker or lighter
                int colorInt = (Math.sqrt(color.getRed() * color.getRed() * 0.241
                        + color.getGreen() * color.getGreen() * 0.691
                        + color.getBlue() * color.getBlue() * 0.068) > 127 ? color.darker() : color.brighter()).getRGB();*/
            }
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(passenger instanceof Player && this.level().isClientSide())
        {
            VehicleHelper.playVehicleSound((Player) passenger, this);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        if(!this.disableFallDamage)
        {
            super.causeFallDamage(distance, damageMultiplier, source);
        }
        if(this.launchingTimer <= 0 && distance > 3)
        {
            this.disableFallDamage = false;
        }
        return true;
    }

    private boolean isControllingPassengerCreative()
    {
        Entity entity = this.getControllingPassenger();
        if(entity instanceof Player)
        {
            return ((Player) entity).isCreative();
        }
        return false;
    }

    private void openEditInventory(Player player)
    {
        if(player instanceof ServerPlayer)
        {
            NetworkHooks.openScreen((ServerPlayer) player, this, buffer -> buffer.writeInt(this.getId()));
            /*ServerPlayer serverPlayer = (ServerPlayer) player;
            serverPlayer.getNextWindowId();
            serverPlayer.openContainer = new EditVehicleContainer(serverPlayer.currentWindowId, this.getVehicleInventory(), this, player, player.inventory);
            serverPlayer.openContainer.addListener(serverPlayer);
            PacketInteractionHandler.instance.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MessageVehicleWindow(serverPlayer.currentWindowId, this.getEntityId()));*/
        }
    }

    public SimpleContainer getVehicleInventory()
    {
        if(this.vehicleInventory == null)
        {
            this.initVehicleInventory();
        }
        return this.vehicleInventory;
    }

    protected void initVehicleInventory()
    {
        this.vehicleInventory = new SimpleContainer(2);

        ItemStack engine = this.getEngineStack();
        if(this.getProperties().getEngineType() != EngineType.NONE & !engine.isEmpty())
        {
            this.vehicleInventory.setItem(0, engine.copy());
        }

        ItemStack wheel = this.getWheelStack();
        if(this.canChangeWheels() && !wheel.isEmpty())
        {
            this.vehicleInventory.setItem(1, wheel.copy());
        }

        this.vehicleInventory.addListener(this);
    }

    private void updateSlots()
    {
        if(!this.level().isClientSide())
        {
            ItemStack engine = this.vehicleInventory.getItem(0);
            if(engine.getItem() instanceof EngineItem)
            {
                EngineItem item = (EngineItem) engine.getItem();
                if(item.getEngineType() == this.getProperties().getEngineType())
                {
                    this.setEngineStack(engine.copy());
                }
                else
                {
                    this.setEngineStack(ItemStack.EMPTY);
                }
            }
            else if(this.getProperties().getEngineType() != EngineType.NONE)
            {
                this.setEngineStack(ItemStack.EMPTY);
            }

            ItemStack wheel = this.vehicleInventory.getItem(1);
            if(this.canChangeWheels())
            {
                if(wheel.getItem() instanceof WheelItem)
                {
                    if(!this.hasWheelStack())
                    {
                        this.level().playSound(null, this.blockPosition(), ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundSource.BLOCKS, 1.0F, 1.1F);
                        this.setWheelStack(wheel.copy());
                    }
                }
                else
                {
                    this.level().playSound(null, this.blockPosition(), ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                    this.setWheelStack(ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void containerChanged(Container inventory)
    {
        this.updateSlots();
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        boolean isCreativeMode = entity instanceof Player && ((Player) entity).isCreative();
        if(!isCreativeMode && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            // Spawns the engine if the vehicle has one
            ItemStack engine = ItemLookup.getEngine(this);
            if(this.getProperties().getEngineType() != EngineType.NONE && !engine.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), engine);
            }

            // Spawns the key and removes the associated vehicle uuid
            ItemStack key = this.getKeyStack().copy();
            if(!key.isEmpty())
            {
                CommonUtils.getOrCreateStackTag(key).remove("VehicleId");
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), key);
            }

            // Spawns wheels if the vehicle has any
            ItemStack wheel = this.getWheelStack();
            if(this.canChangeWheels() && !wheel.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), wheel.copy());
            }
        }
    }

    public boolean canChangeWheels()
    {
        return true;
    }

    private void updateWheelPositions()
    {
        VehicleProperties properties = this.getProperties();
        if(properties.getWheels() != null)
        {
            List<Wheel> wheels = properties.getWheels();
            for(int i = 0; i < wheels.size(); i++)
            {
                Wheel wheel = wheels.get(i);

                PartPosition bodyPosition = properties.getBodyPosition();
                double wheelX = bodyPosition.getX();
                double wheelY = bodyPosition.getY();
                double wheelZ = bodyPosition.getZ();

                double scale = bodyPosition.getScale();

                /* Applies axel and wheel offets */
                wheelY += (properties.getWheelOffset() * 0.0625F) * scale;

                /* Wheels Translations */
                wheelX += ((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset()) * scale;
                wheelY += (wheel.getOffsetY() * 0.0625) * scale;
                wheelZ += (wheel.getOffsetZ() * 0.0625) * scale;
                wheelX += ((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().getOffset()) * scale;

                /* Offsets the position to the wheel contact on the ground */
                wheelY -= ((8 * 0.0625) / 2.0) * scale * wheel.getScaleY();

                /* Update the wheel position */
                Vec3 wheelVec = new Vec3(wheelX, wheelY, wheelZ).yRot(-this.getModifiedRotationYaw() * 0.017453292F);
                wheelPositions[i * 3] = wheelVec.x;
                wheelPositions[i * 3 + 1] = wheelVec.y;
                wheelPositions[i * 3 + 2] = wheelVec.z;
            }
        }
    }

    protected void updateGroundState()
    {
        if(this.hasWheelStack())
        {
            VehicleProperties properties = this.getProperties();
            List<Wheel> wheels = properties.getWheels();
            if(this.hasWheelStack() && wheels != null)
            {
                for(int i = 0; i < wheels.size(); i++)
                {
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = Mth.floor(this.getX() + wheelX);
                    int y = Mth.floor(this.getY() + wheelY - 0.2D);
                    int z = Mth.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);
                    if(!state.getCollisionShape(this.level(), pos).isEmpty())
                    {
                        wheelsOnGround = true;
                        return;
                    }
                }
            }
            wheelsOnGround = false;
        }
    }

    protected boolean canAccelerateInAir()
    {
        return false;
    }

    protected boolean canCharge()
    {
        return false;
    }

    protected void releaseCharge()
    {
        this.boosting = true;
        this.boostTimer = 20;
        this.speedMultiplier = 0.5F;
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        ItemStack engine = ItemStack.EMPTY;
        if(this.hasEngine())
        {
            engine = this.getEngineStack();
        }

        ItemStack wheel = ItemStack.EMPTY;
        if(this.hasWheelStack())
        {
            wheel = this.getWheelStack();
        }

        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(this.getType()); // FIXME
        if(entityId != null)
        {
            return VehicleCrateBlock.create(entityId, this.getColor(), engine, wheel);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Component getDisplayName()
    {
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
    {
        return new EditVehicleContainer(windowId, this.getVehicleInventory(), this, playerEntity, playerInventory);
    }

    public double[] getWheelPositions()
    {
        return this.wheelPositions;
    }

    public enum TurnDirection
    {
        LEFT(1), FORWARD(0), RIGHT(-1);

        final int dir;

        TurnDirection(int dir)
        {
            this.dir = dir;
        }

        public int getDir()
        {
            return dir;
        }
    }

    public enum AccelerationDirection
    {
        FORWARD, NONE, REVERSE,
        CHARGING;

        public static AccelerationDirection fromEntity(LivingEntity entity)
        {
            if(entity.zza > 0)
            {
                return FORWARD;
            }
            else if(entity.zza < 0)
            {
                return REVERSE;
            }
            return NONE;
        }
    }

    public enum FuelPortType
    {
        DEFAULT(SpecialModels.FUEL_DOOR_CLOSED, SpecialModels.FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN.get(), 0.25F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE.get(), 0.12F, 0.6F),
        SMALL(SpecialModels.SMALL_FUEL_DOOR_CLOSED, SpecialModels.SMALL_FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN.get(), 0.4F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE.get(), 0.3F, 0.6F);

        private ISpecialModel closed;
        private ISpecialModel open;
        private SoundEvent openSound;
        private SoundEvent closeSound;
        private float openVolume;
        private float closeVolume;
        private float openPitch;
        private float closePitch;

        FuelPortType(ISpecialModel closed, ISpecialModel open, SoundEvent openSound, float openVolume, float openPitch, SoundEvent closeCount, float closeVolume, float closePitch)
        {
            this.closed = closed;
            this.open = open;
            this.openSound = openSound;
            this.openVolume = openVolume;
            this.openPitch = openPitch;
            this.closeSound = closeCount;
            this.closeVolume = closeVolume;
            this.closePitch = closePitch;
        }

        public ISpecialModel getClosedModel()
        {
            return closed;
        }

        public ISpecialModel getOpenModel()
        {
            return open;
        }

        @OnlyIn(Dist.CLIENT)
        public void playOpenSound()
        {
            VehicleHelper.playSound(this.openSound, this.openVolume, this.openPitch);
        }

        @OnlyIn(Dist.CLIENT)
        public void playCloseSound()
        {
            VehicleHelper.playSound(this.closeSound, this.closeVolume, this.closePitch);
        }
    }
}
