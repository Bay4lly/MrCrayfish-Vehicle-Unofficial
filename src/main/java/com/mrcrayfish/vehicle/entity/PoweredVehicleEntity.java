/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerListener
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$MoveFunction
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidUtil
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
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
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessagePower;
import com.mrcrayfish.vehicle.network.message.MessageTurnAngle;
import com.mrcrayfish.vehicle.network.message.MessageTurnDirection;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public abstract class PoweredVehicleEntity
extends VehicleEntity
implements ContainerListener,
MenuProvider {
    protected static final EntityDataAccessor<Float> CURRENT_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> MAX_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> ACCELERATION_SPEED = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> TURN_DIRECTION = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> TARGET_TURN_ANGLE = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> TURN_SENSITIVITY = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> MAX_TURN_ANGLE = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ACCELERATION_DIRECTION = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> HORN = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> REQUIRES_FUEL = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> CURRENT_FUEL = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> FUEL_CAPACITY = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> NEEDS_KEY = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> KEY_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<ItemStack> ENGINE_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<ItemStack> WHEEL_STACK = SynchedEntityData.defineId(PoweredVehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.ITEM_STACK);
    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 0.25f;
    protected boolean charging;
    protected AccelerationDirection prevAcceleration;
    protected double[] wheelPositions;
    protected boolean wheelsOnGround = true;
    public float turnAngle;
    public float prevTurnAngle;
    public float deltaYaw;
    public float wheelAngle;
    public float prevWheelAngle;
    @OnlyIn(value=Dist.CLIENT)
    public float targetWheelAngle;
    @OnlyIn(value=Dist.CLIENT)
    public float renderWheelAngle;
    @OnlyIn(value=Dist.CLIENT)
    public float prevRenderWheelAngle;
    @OnlyIn(value=Dist.CLIENT)
    public int wheelieCount;
    @OnlyIn(value=Dist.CLIENT)
    public int prevWheelieCount;
    public float vehicleMotionX;
    public float vehicleMotionY;
    public float vehicleMotionZ;
    private UUID owner;
    private SimpleContainer vehicleInventory;
    private FuelPortType fuelPortType;
    private boolean fueling;
    protected float stepHeight = 1.0f;

    public float maxUpStep() {
        return this.stepHeight;
    }

    protected PoweredVehicleEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public PoweredVehicleEntity(EntityType<?> entityType, Level worldIn, double posX, double posY, double posZ) {
        this(entityType, worldIn);
        this.setPos(posX, posY, posZ);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_SPEED, Float.valueOf(0.0f));
        builder.define(MAX_SPEED, Float.valueOf(10.0f));
        builder.define(ACCELERATION_SPEED, Float.valueOf(0.5f));
        builder.define(POWER, Float.valueOf(1.0f));
        builder.define(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        builder.define(TARGET_TURN_ANGLE, Float.valueOf(0.0f));
        builder.define(TURN_SENSITIVITY, 6);
        builder.define(MAX_TURN_ANGLE, 35);
        builder.define(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        builder.define(HORN, false);
        builder.define(REQUIRES_FUEL, ((Boolean)Config.SERVER.fuelEnabled.get()));
        builder.define(CURRENT_FUEL, Float.valueOf(0.0f));
        builder.define(FUEL_CAPACITY, Float.valueOf(15000.0f));
        builder.define(NEEDS_KEY, false);
        builder.define(KEY_STACK, ItemStack.EMPTY);
        builder.define(ENGINE_STACK, ItemStack.EMPTY);
        builder.define(WHEEL_STACK, ItemStack.EMPTY);
        List<Wheel> wheels = this.getProperties().getWheels();
        if (wheels != null && wheels.size() > 0) {
            this.wheelPositions = new double[wheels.size() * 3];
        }
    }

    public abstract SoundEvent getEngineSound();

    public SoundEvent getHornSound() {
        return (SoundEvent)ModSounds.ENTITY_VEHICLE_HORN.get();
    }

    public void playFuelPortOpenSound() {
        if (!this.fueling) {
            this.fuelPortType.playOpenSound();
            this.fueling = true;
        }
    }

    public void playFuelPortCloseSound() {
        if (this.fueling) {
            this.fuelPortType.playCloseSound();
            this.fueling = false;
        }
    }

    public float getMinEnginePitch() {
        return 0.5f;
    }

    public float getMaxEnginePitch() {
        return 1.2f;
    }

    public boolean isPickable() {
        return true;
    }

    @Override
    public void onClientInit() {
        super.onClientInit();
        this.setFuelPortType(FuelPortType.DEFAULT);
    }

    protected void setFuelPortType(FuelPortType fuelPortType) {
        this.fuelPortType = fuelPortType;
    }

    public void fuelVehicle(Player player, InteractionHand hand) {
        if (((Optional)ModDataKeys.GAS_PUMP.getValue(player)).isPresent()) {
            BlockPos pos = (BlockPos)((Optional)ModDataKeys.GAS_PUMP.getValue(player)).get();
            BlockEntity tileEntity = this.level().getBlockEntity(pos);
            if (!(tileEntity instanceof GasPumpTileEntity)) {
                return;
            }
            tileEntity = this.level().getBlockEntity(pos.below());
            if (!(tileEntity instanceof GasPumpTankTileEntity)) {
                return;
            }
            GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity)tileEntity;
            FluidTank tank = gasPumpTank.getFluidTank();
            FluidStack stack = tank.getFluid();
            if (stack.isEmpty() || !((List)Config.SERVER.validFuels.get()).contains(BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString())) {
                return;
            }
            stack = tank.drain(200, IFluidHandler.FluidAction.EXECUTE);
            if (stack.isEmpty()) {
                return;
            }
            stack.setAmount(this.addFuel(stack.getAmount()));
            if (stack.getAmount() <= 0) {
                return;
            }
            gasPumpTank.getFluidTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof JerryCanItem)) {
            return;
        }
        JerryCanItem jerryCan = (JerryCanItem)stack.getItem();
        Optional optional = FluidUtil.getFluidHandler((ItemStack)stack);
        if (!optional.isPresent()) {
            return;
        }
        IFluidHandlerItem handler = (IFluidHandlerItem)optional.get();
        FluidStack fluidStack = handler.getFluidInTank(0);
        if (fluidStack.isEmpty() || !((List)Config.SERVER.validFuels.get()).contains(BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString())) {
            return;
        }
        int transferAmount = Math.min(handler.getFluidInTank(0).getAmount(), jerryCan.getFillRate());
        transferAmount = (int)Math.min(Math.floor(this.getFuelCapacity() - this.getCurrentFuel()), (double)transferAmount);
        handler.drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
        this.addFuel(transferAmount);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.level().isClientSide) return super.interact(player, hand);
        if (this.owner == null) {
            this.owner = player.getUUID();
        }
        if (stack.getItem() == ModItems.KEY.get()) {
            if (!this.owner.equals(player.getUUID())) {
                CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                return InteractionResult.FAIL;
            }
            if (this.isLockable()) {
                CompoundTag tag = CommonUtils.getOrCreateStackTag(stack);
                if (tag.hasUUID("VehicleId") && !this.getUUID().equals(tag.getUUID("VehicleId"))) return super.interact(player, hand);
                tag.putUUID("VehicleId", this.getUUID());
                if (!this.isKeyNeeded()) {
                    this.setKeyNeeded(true);
                    CommonUtils.sendInfoMessage(player, "vehicle.status.key_added");
                    return InteractionResult.SUCCESS;
                } else {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.key_created");
                }
                return InteractionResult.SUCCESS;
            }
            CommonUtils.sendInfoMessage(player, "vehicle.status.not_lockable");
            return InteractionResult.FAIL;
        }
        if (stack.getItem() != ModItems.WRENCH.get() || !(this.getVehicle() instanceof EntityJack)) return super.interact(player, hand);
        if (player.getUUID().equals(this.owner)) {
            this.openEditInventory(player);
            return InteractionResult.SUCCESS;
        } else {
            CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUpdateVehicle() {
        LivingEntity controllingPassenger;
        this.prevCurrentSpeed = this.currentSpeed;
        this.prevTurnAngle = this.turnAngle;
        this.prevWheelAngle = this.wheelAngle;
        if (this.level().isClientSide) {
            this.onClientUpdate();
        }
        if ((controllingPassenger = this.getControllingPassenger()) != null) {
            this.createParticles();
        }
        if (this.charging && this.prevAcceleration == AccelerationDirection.CHARGING && this.getAcceleration() != this.prevAcceleration && this.getRealSpeed() > 0.95f) {
            this.releaseCharge();
        }
        this.updateGroundState();
        this.updateSpeed();
        this.updateTurning();
        this.updateVehicle();
        this.setSpeed(this.currentSpeed);
        VehicleProperties properties = this.getProperties();
        if (properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null) {
            this.setYRot(this.getYRot() - this.deltaYaw);
        }
        this.updateVehicleMotion();
        this.setRot(this.getYRot(), this.getXRot());
        double deltaRot = this.yRotO - this.getYRot();
        if (deltaRot < -180.0) {
            this.yRotO += 360.0f;
        } else if (deltaRot >= 180.0) {
            this.yRotO -= 360.0f;
        }
        this.updateWheelPositions();
        this.move(MoverType.SELF, this.getDeltaMovement().add((double)this.vehicleMotionX, (double)this.vehicleMotionY, (double)this.vehicleMotionZ));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.98, 0.8));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));
        }
        if (this.boostTimer > 0) {
            --this.boostTimer;
        } else {
            this.boosting = false;
            this.speedMultiplier = (float)((double)this.speedMultiplier * 0.85);
        }
        if (this.launchingTimer > 0) {
            this.disableFallDamage = true;
            --this.launchingTimer;
        } else {
            this.launching = false;
        }
        this.checkInsideBlocks();
        List<net.minecraft.world.entity.Entity> list = this.level().getEntities((Entity)this, this.getBoundingBox(), entity -> entity instanceof BumperCarEntity);
        if (!list.isEmpty()) {
            for (Entity entity2 : list) {
                this.push(entity2);
            }
        }
        if (this.requiresFuel() && controllingPassenger instanceof Player && !((Player)controllingPassenger).isCreative() && this.isEnginePowered()) {
            float currentSpeed = Math.abs(Math.min(this.getSpeed(), this.getMaxSpeed()));
            float normalSpeed = Math.max(0.05f, currentSpeed / this.getMaxSpeed());
            float currentFuel = this.getCurrentFuel();
            if ((currentFuel = (float)((double)currentFuel - (double)(this.fuelConsumption * normalSpeed) * (Double)Config.SERVER.fuelConsumptionFactor.get())) < 0.0f) {
                currentFuel = 0.0f;
            }
            this.setCurrentFuel(currentFuel);
        }
        this.prevAcceleration = this.getAcceleration();
    }

    public void updateVehicle() {
    }

    public abstract void updateVehicleMotion();

    public FuelPortType getFuelPortType() {
        return FuelPortType.DEFAULT;
    }

    protected void updateSpeed() {
        float surfaceModifier = SurfaceHelper.getSurfaceModifier(this);
        this.currentSpeed = this.getSpeed();
        Optional<IEngineTier> optional = this.getEngineTier();
        AccelerationDirection acceleration = this.getAcceleration();
        if (acceleration != AccelerationDirection.CHARGING) {
            this.charging = false;
        }
        if (this.getControllingPassenger() != null && optional.isPresent()) {
            if (this.canDrive()) {
                boolean charging;
                boolean bl = charging = this.canCharge() && acceleration == AccelerationDirection.CHARGING && Math.abs(this.currentSpeed) < 0.5f;
                if (acceleration == AccelerationDirection.FORWARD || charging || this.charging) {
                    if (!this.charging) {
                        this.charging = charging;
                    }
                    if (this.wheelsOnGround || this.canAccelerateInAir()) {
                        float maxSpeed = this.getActualMaxSpeed() * surfaceModifier * this.getPower();
                        if (this.currentSpeed < maxSpeed) {
                            IEngineTier engineTier = optional.get();
                            this.currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                            if (this.currentSpeed > maxSpeed) {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if (this.currentSpeed > maxSpeed) {
                            this.currentSpeed *= 0.975f;
                        }
                        return;
                    }
                } else if (acceleration == AccelerationDirection.REVERSE && (this.wheelsOnGround || this.canAccelerateInAir())) {
                    IEngineTier engineTier = optional.get();
                    float maxSpeed = -(4.0f + engineTier.getAdditionalMaxSpeed() / 2.0f) * surfaceModifier * this.getPower();
                    if (this.currentSpeed > maxSpeed) {
                        this.currentSpeed -= this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                        if (this.currentSpeed < maxSpeed) {
                            this.currentSpeed = maxSpeed;
                        }
                    }
                    if (this.currentSpeed < maxSpeed) {
                        this.currentSpeed *= 0.975f;
                    }
                    return;
                }
            }
            this.currentSpeed = this.wheelsOnGround || this.canAccelerateInAir() ? (float)((double)this.currentSpeed * 0.9) : (float)((double)this.currentSpeed * 0.98);
        } else {
            this.currentSpeed = this.wheelsOnGround ? (float)((double)this.currentSpeed * 0.85) : (float)((double)this.currentSpeed * 0.98);
        }
    }

    protected void updateTurning() {
        this.turnAngle = this.getTargetTurnAngle();
        this.wheelAngle = this.turnAngle * Math.max(0.45f, 1.0f - Math.abs(this.currentSpeed / 20.0f));
        this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30.0f) / 2.0f;
        if (this.level().isClientSide) {
            this.renderWheelAngle = this.wheelAngle;
        }
    }

    public void createParticles() {
        VehicleProperties properties;
        if ((this.getAcceleration() == AccelerationDirection.FORWARD || this.charging) && (properties = this.getProperties()).getWheels() != null) {
            List<Wheel> wheels = properties.getWheels();
            for (int i = 0; i < wheels.size(); ++i) {
                Wheel wheel = wheels.get(i);
                if (!wheel.shouldSpawnParticles()) continue;
                double wheelX = this.wheelPositions[i * 3];
                double wheelY = this.wheelPositions[i * 3 + 1];
                double wheelZ = this.wheelPositions[i * 3 + 2];
                int x = Mth.floor((double)(this.getX() + wheelX));
                int y = Mth.floor((double)(this.getY() + wheelY - 0.2));
                int z = Mth.floor((double)(this.getZ() + wheelZ));
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = this.level().getBlockState(pos);
                if (state.isAir() || !state.isSolid()) continue;
                Vec3 dirVec = this.calculateViewVector(this.getXRot(), this.getModifiedRotationYaw() + 180.0f).add(0.0, 0.5, 0.0);
                if (this.charging) {
                    dirVec = dirVec.scale((double)(this.currentSpeed / 3.0f));
                }
                if (!this.level().isClientSide()) continue;
                VehicleHelper.spawnWheelParticle(pos, state, this.getX() + wheelX, this.getY() + wheelY, this.getZ() + wheelZ, dirVec);
            }
        }
        if (this.shouldShowEngineSmoke() && this.canDrive() && this.tickCount % 2 == 0) {
            Vec3 smokePosition = this.getEngineSmokePosition().yRot(-this.getModifiedRotationYaw() * ((float)Math.PI / 180));
            this.level().addParticle((ParticleOptions)ParticleTypes.SMOKE, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0, -this.getDeltaMovement().z);
            if (this.charging && this.getRealSpeed() > 0.95f) {
                this.level().addParticle((ParticleOptions)ParticleTypes.CRIT, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0, -this.getDeltaMovement().z);
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onClientUpdate() {
        this.prevRenderWheelAngle = this.renderWheelAngle;
        this.prevWheelieCount = this.wheelieCount;
        LivingEntity entity = this.getControllingPassenger();
        if (entity instanceof LivingEntity && entity.equals((Object)Minecraft.getInstance().player)) {
            LivingEntity livingEntity = entity;
            float power = VehicleHelper.getPower(this);
            if (power != this.getPower()) {
                this.setPower(power);
                PacketHandler.sendToServer(new MessagePower(power));
            }
            AccelerationDirection acceleration = VehicleHelper.getAccelerationDirection(livingEntity);
            if (this.getAcceleration() != acceleration) {
                this.setAcceleration(acceleration);
                PacketHandler.sendToServer(new MessageAccelerating(acceleration));
            }
            boolean horn = VehicleHelper.isHonking();
            this.setHorn(horn);
            PacketHandler.sendToServer(new MessageHorn(horn));
            TurnDirection direction = VehicleHelper.getTurnDirection(livingEntity);
            if (this.getTurnDirection() != direction) {
                this.setTurnDirection(direction);
                PacketHandler.sendToServer(new MessageTurnDirection(direction));
            }
            float targetTurnAngle = VehicleHelper.getTargetTurnAngle(this, false);
            this.setTargetTurnAngle(targetTurnAngle);
            PacketHandler.sendToServer(new MessageTurnAngle(targetTurnAngle));
        }
        if (this.isBoosting() && this.getControllingPassenger() != null) {
            if (this.wheelieCount < 4) {
                ++this.wheelieCount;
            }
        } else if (this.wheelieCount > 0) {
            --this.wheelieCount;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Owner", 10)) {
            this.owner = compound.getUUID("Owner");
        }
        if (compound.contains("EngineStack", 10)) {
            this.setEngineStack(ItemStack.parse((HolderLookup.Provider)this.level().registryAccess(), (Tag)compound.getCompound("EngineStack")).orElse(ItemStack.EMPTY));
        }
        if (compound.contains("WheelStack", 10)) {
            this.setWheelStack(ItemStack.parse((HolderLookup.Provider)this.level().registryAccess(), (Tag)compound.getCompound("WheelStack")).orElse(ItemStack.EMPTY));
        }
        if (compound.contains("MaxSpeed", 5)) {
            this.setMaxSpeed(compound.getFloat("MaxSpeed"));
        }
        if (compound.contains("AccelerationSpeed", 5)) {
            this.setAccelerationSpeed(compound.getFloat("AccelerationSpeed"));
        }
        if (compound.contains("TurnSensitivity", 3)) {
            this.setTurnSensitivity(compound.getInt("TurnSensitivity"));
        }
        if (compound.contains("MaxTurnAngle", 3)) {
            this.setMaxTurnAngle(compound.getInt("MaxTurnAngle"));
        }
        if (compound.contains("StepHeight", 5)) {
            this.stepHeight = compound.getFloat("StepHeight");
        }
        if (compound.contains("RequiresFuel", 1)) {
            this.setRequiresFuel(compound.getBoolean("RequiresFuel"));
        }
        if (compound.contains("CurrentFuel", 5)) {
            this.setCurrentFuel(compound.getFloat("CurrentFuel"));
        }
        if (compound.contains("FuelCapacity", 3)) {
            this.setFuelCapacity(compound.getInt("FuelCapacity"));
        }
        if (compound.contains("KeyNeeded", 1)) {
            this.setKeyNeeded(compound.getBoolean("KeyNeeded"));
        }
        this.setKeyStack(CommonUtils.readItemStackFromTag((HolderLookup.Provider)this.level().registryAccess(), compound, "KeyStack"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.owner != null) {
            compound.putUUID("Owner", this.owner);
        }
        compound.putBoolean("HasEngine", this.hasEngine());
        CommonUtils.writeItemStackToTag((HolderLookup.Provider)this.level().registryAccess(), compound, "EngineStack", this.getEngineStack());
        CommonUtils.writeItemStackToTag((HolderLookup.Provider)this.level().registryAccess(), compound, "WheelStack", this.getWheelStack());
        compound.putFloat("MaxSpeed", this.getMaxSpeed());
        compound.putFloat("AccelerationSpeed", this.getAccelerationSpeed());
        compound.putInt("TurnSensitivity", this.getTurnSensitivity());
        compound.putInt("MaxTurnAngle", this.getMaxTurnAngle());
        compound.putFloat("StepHeight", this.maxUpStep());
        compound.putBoolean("RequiresFuel", this.requiresFuel());
        compound.putFloat("CurrentFuel", this.getCurrentFuel());
        compound.putFloat("FuelCapacity", this.getFuelCapacity());
        compound.putBoolean("KeyNeeded", this.isKeyNeeded());
        CommonUtils.writeItemStackToTag((HolderLookup.Provider)this.level().registryAccess(), compound, "KeyStack", this.getKeyStack());
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        VehicleProperties properties = this.getProperties();
        for (Entity passenger : this.getPassengers()) {
            int seatIndex;
            if (!(passenger instanceof LivingEntity) || (seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID())) == -1 || !properties.getSeats().get(seatIndex).isDriverSeat()) continue;
            return (LivingEntity)passenger;
        }
        return null;
    }

    @Override
    public void updatePassengerPosition(Entity passenger, Entity.MoveFunction moveFunction) {
        int seatIndex;
        if (this.hasPassenger(passenger) && (seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID())) != -1) {
            VehicleProperties properties = this.getProperties();
            if (seatIndex >= 0 && seatIndex < properties.getSeats().size()) {
                Seat seat = properties.getSeats().get(seatIndex);
                Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).multiply(-1.0, 1.0, 1.0).scale(0.0625).yRot(-(this.getModifiedRotationYaw() + 180.0f) * ((float)Math.PI / 180));
                Vec3 attachmentPoint = passenger.getVehicleAttachmentPoint((Entity)this);
                moveFunction.accept(passenger, this.getX() - seatVec.x - attachmentPoint.x, this.getY() + seatVec.y - attachmentPoint.y + 0.25, this.getZ() - seatVec.z - attachmentPoint.z);
                if (this.level().isClientSide() && VehicleHelper.canApplyVehicleYaw(passenger)) {
                    passenger.setYRot(passenger.getYRot() - this.deltaYaw);
                    passenger.setYHeadRot(passenger.getYRot());
                }
                this.applyYawToEntity(passenger);
            }
        }
    }

    public boolean isMoving() {
        return this.currentSpeed != 0.0f;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.entityData.set(MAX_SPEED, Float.valueOf(maxSpeed));
    }

    public float getMaxSpeed() {
        return ((Float)this.entityData.get(MAX_SPEED)).floatValue();
    }

    public float getActualMaxSpeed() {
        float maxSpeed = ((Float)this.entityData.get(MAX_SPEED)).floatValue();
        Optional<IEngineTier> engineTier = this.getEngineTier();
        if (engineTier.isPresent()) {
            maxSpeed += engineTier.get().getAdditionalMaxSpeed();
        }
        return maxSpeed;
    }

    public float getRealSpeed() {
        return this.currentSpeed / (this.getActualMaxSpeed() * SurfaceHelper.getSurfaceModifier(this) * this.getPower());
    }

    public void setSpeed(float speed) {
        this.entityData.set(CURRENT_SPEED, Float.valueOf(speed));
    }

    public float getSpeed() {
        return this.currentSpeed;
    }

    public float getNormalSpeed() {
        return this.currentSpeed / this.getMaxSpeed();
    }

    public float getActualSpeed() {
        return (this.currentSpeed + this.currentSpeed * this.speedMultiplier) / this.getActualMaxSpeed();
    }

    public void setAccelerationSpeed(float speed) {
        this.entityData.set(ACCELERATION_SPEED, Float.valueOf(speed));
    }

    public float getAccelerationSpeed() {
        return ((Float)this.entityData.get(ACCELERATION_SPEED)).floatValue();
    }

    protected float getModifiedAccelerationSpeed() {
        return ((Float)this.entityData.get(ACCELERATION_SPEED)).floatValue();
    }

    public double getKilometersPreHour() {
        return Math.sqrt(Math.pow(this.getX() - this.xo, 2.0) + Math.pow(this.getY() - this.yo, 2.0) + Math.pow(this.getZ() - this.zo, 2.0)) * 20.0;
    }

    public void setTurnDirection(TurnDirection turnDirection) {
        this.entityData.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection() {
        return TurnDirection.values()[(Integer)this.entityData.get(TURN_DIRECTION)];
    }

    public void setTargetTurnAngle(float targetTurnAngle) {
        this.entityData.set(TARGET_TURN_ANGLE, Float.valueOf(targetTurnAngle));
    }

    public float getTargetTurnAngle() {
        return ((Float)this.entityData.get(TARGET_TURN_ANGLE)).floatValue();
    }

    public void setAcceleration(AccelerationDirection direction) {
        this.entityData.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public AccelerationDirection getAcceleration() {
        return AccelerationDirection.values()[(Integer)this.entityData.get(ACCELERATION_DIRECTION)];
    }

    public void setPower(float power) {
        this.entityData.set(POWER, Float.valueOf(Mth.clamp((float)power, (float)0.0f, (float)1.0f)));
    }

    public float getPower() {
        return ((Float)this.entityData.get(POWER)).floatValue();
    }

    public void setTurnSensitivity(int sensitivity) {
        this.entityData.set(TURN_SENSITIVITY, sensitivity);
    }

    public int getTurnSensitivity() {
        return (Integer)this.entityData.get(TURN_SENSITIVITY);
    }

    public void setMaxTurnAngle(int turnAngle) {
        this.entityData.set(MAX_TURN_ANGLE, turnAngle);
    }

    public int getMaxTurnAngle() {
        return (Integer)this.entityData.get(MAX_TURN_ANGLE);
    }

    public boolean hasEngine() {
        return !this.getEngineStack().isEmpty();
    }

    public void setEngineStack(ItemStack engine) {
        this.entityData.set(ENGINE_STACK, engine);
    }

    public ItemStack getEngineStack() {
        return (ItemStack)this.entityData.get(ENGINE_STACK);
    }

    public Optional<IEngineTier> getEngineTier() {
        return IEngineTier.fromStack(this.getEngineStack());
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderEngine() {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderFuelPort() {
        return true;
    }

    public Vec3 getEngineSmokePosition() {
        return new Vec3(0.0, 0.0, 0.0);
    }

    public boolean shouldShowEngineSmoke() {
        return false;
    }

    public void setHorn(boolean activated) {
        this.entityData.set(HORN, activated);
    }

    public boolean getHorn() {
        return (Boolean)this.entityData.get(HORN);
    }

    public void setBoosting(boolean boosting) {
        this.boosting = boosting;
        this.boostTimer = 10;
    }

    public boolean isBoosting() {
        return this.boosting;
    }

    public void setLaunching(int hold) {
        this.launching = true;
        this.launchingTimer = hold;
        this.disableFallDamage = true;
    }

    public boolean isLaunching() {
        return this.launching;
    }

    public boolean requiresFuel() {
        return (Boolean)Config.SERVER.fuelEnabled.get() != false && (Boolean)this.entityData.get(REQUIRES_FUEL) != false;
    }

    public void setRequiresFuel(boolean requiresFuel) {
        this.entityData.set(REQUIRES_FUEL, ((Boolean)Config.SERVER.fuelEnabled.get() != false && requiresFuel));
    }

    public boolean isFueled() {
        return !this.requiresFuel() || this.isControllingPassengerCreative() || this.getCurrentFuel() > 0.0f;
    }

    public void setCurrentFuel(float fuel) {
        this.entityData.set(CURRENT_FUEL, Float.valueOf(fuel));
    }

    public float getCurrentFuel() {
        return ((Float)this.entityData.get(CURRENT_FUEL)).floatValue();
    }

    public void setFuelCapacity(float capacity) {
        this.entityData.set(FUEL_CAPACITY, Float.valueOf(capacity));
    }

    public float getFuelCapacity() {
        return ((Float)this.entityData.get(FUEL_CAPACITY)).floatValue();
    }

    public void setFuelConsumption(float consumption) {
        this.fuelConsumption = consumption;
    }

    public float getFuelConsumption() {
        return this.fuelConsumption;
    }

    public int addFuel(int fuel) {
        if (!this.requiresFuel()) {
            return fuel;
        }
        float currentFuel = this.getCurrentFuel();
        int remaining = Math.max(0, Math.round((currentFuel += (float)fuel) - this.getFuelCapacity()));
        currentFuel = Math.min(currentFuel, this.getFuelCapacity());
        this.setCurrentFuel(currentFuel);
        return remaining;
    }

    public void setKeyNeeded(boolean needsKey) {
        this.entityData.set(NEEDS_KEY, needsKey);
    }

    public boolean isKeyNeeded() {
        return (Boolean)this.entityData.get(NEEDS_KEY);
    }

    public void setKeyStack(ItemStack stack) {
        this.entityData.set(KEY_STACK, stack);
    }

    public ItemStack getKeyStack() {
        return (ItemStack)this.entityData.get(KEY_STACK);
    }

    public void ejectKey() {
        if (!this.getKeyStack().isEmpty()) {
            Vec3 keyHole = this.getPartPositionAbsoluteVec(this.getProperties().getKeyPortPosition(), 1.0f);
            this.level().addFreshEntity((Entity)new ItemEntity(this.level(), keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public boolean isLockable() {
        return true;
    }

    public boolean isEnginePowered() {
        return !(this.getProperties().getEngineType() != EngineType.NONE && !this.hasEngine() || !this.isControllingPassengerCreative() && !this.isFueled() || this.getDestroyedStage() >= 9 || this.isKeyNeeded() && this.getKeyStack().isEmpty());
    }

    public boolean canDrive() {
        return (!this.canChangeWheels() || this.hasWheelStack()) && this.isEnginePowered();
    }

    public boolean isOwner(Player player) {
        return this.owner == null || player.getUUID().equals(this.owner);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean hasWheelStack() {
        return !this.getWheelStack().isEmpty();
    }

    public void setWheelStack(ItemStack wheels) {
        this.entityData.set(WHEEL_STACK, wheels);
    }

    public ItemStack getWheelStack() {
        return (ItemStack)this.entityData.get(WHEEL_STACK);
    }

    public Optional<IWheelType> getWheelType() {
        return IWheelType.fromStack((ItemStack)this.entityData.get(WHEEL_STACK));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (!this.level().isClientSide || COLOR.equals(key)) {
            // empty if block
        }
    }

    @Override
    public void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (passenger instanceof Player && this.level().isClientSide()) {
            VehicleHelper.playVehicleSound((Player)passenger, this);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (!this.disableFallDamage) {
            super.causeFallDamage(distance, damageMultiplier, source);
        }
        if (this.launchingTimer <= 0 && distance > 3.0f) {
            this.disableFallDamage = false;
        }
        return true;
    }

    private boolean isControllingPassengerCreative() {
        LivingEntity entity = this.getControllingPassenger();
        if (entity instanceof Player) {
            return ((Player)entity).isCreative();
        }
        return false;
    }

    private void openEditInventory(Player player) {
        if (player instanceof ServerPlayer) {
            ((ServerPlayer)player).openMenu((MenuProvider)this, buffer -> buffer.writeInt(this.getId()));
        }
    }

    public SimpleContainer getVehicleInventory() {
        if (this.vehicleInventory == null) {
            this.initVehicleInventory();
        }
        return this.vehicleInventory;
    }

    protected void initVehicleInventory() {
        this.vehicleInventory = new SimpleContainer(2);
        ItemStack engine = this.getEngineStack();
        if (this.getProperties().getEngineType() != EngineType.NONE & !engine.isEmpty()) {
            this.vehicleInventory.setItem(0, engine.copy());
        }
        ItemStack wheel = this.getWheelStack();
        if (this.canChangeWheels() && !wheel.isEmpty()) {
            this.vehicleInventory.setItem(1, wheel.copy());
        }
        this.vehicleInventory.addListener((ContainerListener)this);
    }

    private void updateSlots() {
        if (!this.level().isClientSide()) {
            ItemStack engine = this.vehicleInventory.getItem(0);
            if (engine.getItem() instanceof EngineItem) {
                EngineItem item = (EngineItem)engine.getItem();
                if (item.getEngineType() == this.getProperties().getEngineType()) {
                    this.setEngineStack(engine.copy());
                } else {
                    this.setEngineStack(ItemStack.EMPTY);
                }
            } else if (this.getProperties().getEngineType() != EngineType.NONE) {
                this.setEngineStack(ItemStack.EMPTY);
            }
            ItemStack wheel = this.vehicleInventory.getItem(1);
            if (this.canChangeWheels()) {
                if (wheel.getItem() instanceof WheelItem) {
                    if (!this.hasWheelStack()) {
                        this.level().playSound(null, this.blockPosition(), (SoundEvent)ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundSource.BLOCKS, 1.0f, 1.1f);
                        this.setWheelStack(wheel.copy());
                    }
                } else {
                    this.level().playSound(null, this.blockPosition(), (SoundEvent)ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundSource.BLOCKS, 1.0f, 0.8f);
                    this.setWheelStack(ItemStack.EMPTY);
                }
            }
        }
    }

    public void containerChanged(Container inventory) {
        this.updateSlots();
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity) {
        boolean isCreativeMode;
        super.onVehicleDestroyed(entity);
        boolean bl = isCreativeMode = entity instanceof Player && ((Player)entity).isCreative();
        if (!isCreativeMode && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack key;
            ItemStack engine = ItemLookup.getEngine(this);
            if (this.getProperties().getEngineType() != EngineType.NONE && !engine.isEmpty()) {
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), engine);
            }
            if (!(key = this.getKeyStack().copy()).isEmpty()) {
                CommonUtils.getOrCreateStackTag(key).remove("VehicleId");
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), key);
            }
            ItemStack wheel = this.getWheelStack();
            if (this.canChangeWheels() && !wheel.isEmpty()) {
                InventoryUtil.spawnItemStack(this.level(), this.getX(), this.getY(), this.getZ(), wheel.copy());
            }
        }
    }

    public boolean canChangeWheels() {
        return true;
    }

    private void updateWheelPositions() {
        VehicleProperties properties = this.getProperties();
        if (properties.getWheels() != null) {
            List<Wheel> wheels = properties.getWheels();
            for (int i = 0; i < wheels.size(); ++i) {
                Wheel wheel = wheels.get(i);
                PartPosition bodyPosition = properties.getBodyPosition();
                double wheelX = bodyPosition.getX();
                double wheelY = bodyPosition.getY();
                double wheelZ = bodyPosition.getZ();
                double scale = bodyPosition.getScale();
                wheelY += (double)(properties.getWheelOffset() * 0.0625f) * scale;
                wheelX += (double)wheel.getOffsetX() * 0.0625 * (double)wheel.getSide().getOffset() * scale;
                wheelY += (double)wheel.getOffsetY() * 0.0625 * scale;
                Vec3 wheelVec = new Vec3(wheelX += (double)(wheel.getWidth() * wheel.getScaleX() / 2.0f) * 0.0625 * (double)wheel.getSide().getOffset() * scale, wheelY -= 0.25 * scale * (double)wheel.getScaleY(), wheelZ += (double)wheel.getOffsetZ() * 0.0625 * scale).yRot(-this.getModifiedRotationYaw() * ((float)Math.PI / 180));
                this.wheelPositions[i * 3] = wheelVec.x;
                this.wheelPositions[i * 3 + 1] = wheelVec.y;
                this.wheelPositions[i * 3 + 2] = wheelVec.z;
            }
        }
    }

    protected void updateGroundState() {
        if (this.hasWheelStack()) {
            VehicleProperties properties = this.getProperties();
            List<Wheel> wheels = properties.getWheels();
            if (this.hasWheelStack() && wheels != null) {
                for (int i = 0; i < wheels.size(); ++i) {
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = Mth.floor((double)(this.getX() + wheelX));
                    int y = Mth.floor((double)(this.getY() + wheelY - 0.2));
                    int z = Mth.floor((double)(this.getZ() + wheelZ));
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);
                    if (state.getCollisionShape((BlockGetter)this.level(), pos).isEmpty()) continue;
                    this.wheelsOnGround = true;
                    return;
                }
            }
            this.wheelsOnGround = false;
        }
    }

    protected boolean canAccelerateInAir() {
        return false;
    }

    protected boolean canCharge() {
        return false;
    }

    protected void releaseCharge() {
        this.boosting = true;
        this.boostTimer = 20;
        this.speedMultiplier = 0.5f;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        ResourceLocation entityId;
        ItemStack engine = ItemStack.EMPTY;
        if (this.hasEngine()) {
            engine = this.getEngineStack();
        }
        ItemStack wheel = ItemStack.EMPTY;
        if (this.hasWheelStack()) {
            wheel = this.getWheelStack();
        }
        if ((entityId = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType())) != null) {
            return VehicleCrateBlock.create((HolderLookup.Provider)this.level().registryAccess(), entityId, this.getColor(), engine, wheel);
        }
        return ItemStack.EMPTY;
    }

    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new EditVehicleContainer(windowId, (Container)this.getVehicleInventory(), this, playerEntity, playerInventory);
    }

    public double[] getWheelPositions() {
        return this.wheelPositions;
    }

    public static enum TurnDirection {
        LEFT(1),
        FORWARD(0),
        RIGHT(-1);

        final int dir;

        private TurnDirection(int dir) {
            this.dir = dir;
        }

        public int getDir() {
            return this.dir;
        }
    }

    public static enum AccelerationDirection {
        FORWARD,
        NONE,
        REVERSE,
        CHARGING;


        public static AccelerationDirection fromEntity(LivingEntity entity) {
            if (entity.zza > 0.0f) {
                return FORWARD;
            }
            if (entity.zza < 0.0f) {
                return REVERSE;
            }
            return NONE;
        }
    }

    public static enum FuelPortType {
        DEFAULT(SpecialModels.FUEL_DOOR_CLOSED, SpecialModels.FUEL_DOOR_OPEN, (SoundEvent)ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN.get(), 0.25f, 0.6f, (SoundEvent)ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE.get(), 0.12f, 0.6f),
        SMALL(SpecialModels.SMALL_FUEL_DOOR_CLOSED, SpecialModels.SMALL_FUEL_DOOR_OPEN, (SoundEvent)ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN.get(), 0.4f, 0.6f, (SoundEvent)ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE.get(), 0.3f, 0.6f);

        private ISpecialModel closed;
        private ISpecialModel open;
        private SoundEvent openSound;
        private SoundEvent closeSound;
        private float openVolume;
        private float closeVolume;
        private float openPitch;
        private float closePitch;

        private FuelPortType(ISpecialModel closed, ISpecialModel open, SoundEvent openSound, float openVolume, float openPitch, SoundEvent closeCount, float closeVolume, float closePitch) {
            this.closed = closed;
            this.open = open;
            this.openSound = openSound;
            this.openVolume = openVolume;
            this.openPitch = openPitch;
            this.closeSound = closeCount;
            this.closeVolume = closeVolume;
            this.closePitch = closePitch;
        }

        public ISpecialModel getClosedModel() {
            return this.closed;
        }

        public ISpecialModel getOpenModel() {
            return this.open;
        }

        @OnlyIn(value=Dist.CLIENT)
        public void playOpenSound() {
            VehicleHelper.playSound(this.openSound, this.openVolume, this.openPitch);
        }

        @OnlyIn(value=Dist.CLIENT)
        public void playCloseSound() {
            VehicleHelper.playSound(this.closeSound, this.closeVolume, this.closePitch);
        }
    }
}

