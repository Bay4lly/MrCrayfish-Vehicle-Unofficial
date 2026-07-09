/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundAnimatePacket
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$MoveFunction
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public abstract class VehicleEntity
extends Entity
implements IEntityWithComplexSpawn,
EntityRayTracer.IEntityRayTraceable {
    public static final int[] DYE_TO_COLOR = new int[]{0xF9FFFE, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 0x9D9D97, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 0x1D1D21};
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_HEALTH = SynchedEntityData.defineId(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TRAILER = SynchedEntityData.defineId(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    protected TrailerEntity trailer = null;
    private CompoundTag serverPendingTrailerTag = null;
    private int clientPendingTrailerId = -1;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;
    protected SeatTracker seatTracker = new SeatTracker(this);

    public VehicleEntity(EntityType<?> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TIME_SINCE_HIT, 0);
        builder.define(MAX_HEALTH, Float.valueOf(100.0f));
        builder.define(HEALTH, Float.valueOf(100.0f));
        builder.define(COLOR, 0xF9FFFE);
        builder.define(TRAILER, -1);
        if (this.level().isClientSide) {
            this.onClientInit();
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onClientInit() {
    }

    protected final void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && !player.isCrouching()) {
            ItemStack heldItem;
            Object trailer;
            int trailerId = (Integer)ModDataKeys.TRAILER.getValue(player);
            if (trailerId != -1) {
                Entity entity;
                boolean attached = false;
                if (this.getVehicle() == null && this.canTowTrailer() && this.getTrailer() == null && (entity = this.level().getEntity(trailerId)) instanceof TrailerEntity && entity != this) {
                    trailer = (TrailerEntity)entity;
                    this.setTrailerAndPulling((TrailerEntity)trailer);
                    ModDataKeys.TRAILER.setValue(player, -1);
                    attached = true;
                }
                if (attached) {
                    return InteractionResult.SUCCESS;
                }
            }
            if ((trailer = (heldItem = player.getItemInHand(hand)).getItem()) instanceof SprayCanItem) {
                CompoundTag compound;
                SprayCanItem sprayCan = (SprayCanItem)trailer;
                if (this.canBeColored() && (compound = SprayCanItem.getStackTag(heldItem)) != null) {
                    int remainingSprays = compound.getInt("RemainingSprays");
                    if (sprayCan.hasColor(heldItem) && remainingSprays > 0) {
                        int color = sprayCan.getColor(heldItem);
                        if (this.getColor() != color) {
                            this.setColor(color);
                            player.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)ModSounds.ITEM_SPRAY_CAN_SPRAY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                            compound.putInt("RemainingSprays", remainingSprays - 1);
                            heldItem.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)compound));
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (heldItem.getItem() == ModItems.HAMMER.get() && this.getVehicle() instanceof EntityJack) {
                if (this.getHealth() < this.getMaxHealth()) {
                    heldItem.hurtAndBreak(1, (LivingEntity)player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    this.setHealth(this.getHealth() + 5.0f);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)ModSounds.ENTITY_VEHICLE_THUD.get(), SoundSource.PLAYERS, 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                    player.swing(hand);
                    if (player instanceof ServerPlayer) {
                        ((ServerPlayer)player).connection.send((Packet)new ClientboundAnimatePacket(player, hand == InteractionHand.MAIN_HAND ? 0 : 3));
                    }
                    if (this.getHealth() == this.getMaxHealth()) {
                        if (this.level() instanceof ServerLevel) {
                            int count = (int)(50.0f * (this.getBbWidth() * this.getBbHeight()));
                            for (int i = 0; i < count; ++i) {
                                double width = this.getBbWidth() * 2.0f;
                                double height = (double)this.getBbHeight() * 1.5;
                                Vec3 heldOffset = this.getProperties().getHeldOffset().yRot((float)Math.toRadians(-this.getYRot()));
                                double x = this.getX() + width * (double)this.random.nextFloat() - width / 2.0 + heldOffset.z * 0.0625;
                                double y = this.getY() + height * (double)this.random.nextFloat();
                                double z = this.getZ() + width * (double)this.random.nextFloat() - width / 2.0 + heldOffset.x * 0.0625;
                                double d0 = this.random.nextGaussian() * 0.02;
                                double d1 = this.random.nextGaussian() * 0.02;
                                double d2 = this.random.nextGaussian() * 0.02;
                                ((ServerLevel)this.level()).sendParticles((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, d0, d1, d2, 1.0);
                            }
                        }
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.5f);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (this.canRide(player)) {
                int seatIndex = this.seatTracker.getClosestAvailableSeatToPlayer(player);
                if (seatIndex != -1 && player.startRiding((Entity)this)) {
                    this.getSeatTracker().setSeatIndex(seatIndex, player.getUUID());
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Color", 11)) {
            int[] c = compound.getIntArray("Color");
            if (c.length == 3) {
                int color = (c[0] & 0xFF) << 16 | (c[1] & 0xFF) << 8 | c[2] & 0xFF;
                this.setColor(color);
            }
        } else if (compound.contains("Color", 3)) {
            int val = compound.getInt("Color");
            if (val >= 0 && val < DYE_TO_COLOR.length) {
                this.setColor(DYE_TO_COLOR[val]);
            } else {
                this.setColor(val);
            }
            compound.remove("Color");
        }
        if (compound.contains("MaxHealth", 5)) {
            this.setMaxHealth(compound.getFloat("MaxHealth"));
        }
        if (compound.contains("Health", 5)) {
            this.setHealth(compound.getFloat("Health"));
        }
        if (compound.contains("SeatTracker", 10)) {
            this.seatTracker.read(compound.getCompound("SeatTracker"));
        }
        if (compound.contains("Trailer", 10)) {
            this.serverPendingTrailerTag = compound.getCompound("Trailer");
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        CompoundTag trailerTag;
        compound.putIntArray("Color", this.getColorRGB());
        compound.putFloat("MaxHealth", this.getMaxHealth());
        compound.putFloat("Health", this.getHealth());
        compound.put("SeatTracker", (Tag)this.seatTracker.write());
        if (this.trailer != null && this.trailer.saveAsPassenger(trailerTag = new CompoundTag())) {
            compound.put("Trailer", (Tag)trailerTag);
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (this.level().isClientSide() && key.equals(TRAILER)) {
            int requestedTrailerId = (Integer)this.entityData.get(TRAILER);
            if (requestedTrailerId == -1) {
                this.setTrailerAndPulling(null);
            } else {
                this.clientPendingTrailerId = requestedTrailerId;
            }
        }
    }

    public void setRemoved(Entity.RemovalReason reason) {
        super.setRemoved(reason);
        if (!this.level().isClientSide() && reason.shouldSave() && this.trailer != null) {
            this.trailer.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    public Component getName() {
        return Component.literal((String)(this.getTypeName().getString() + " id = " + this.getId()));
    }

    public void tick() {
        Entity potentialTrailer;
        if (!this.level().isClientSide() && this.serverPendingTrailerTag != null) {
            ServerLevel serverLevel = (ServerLevel)this.level();
            Entity potentialTrailer2 = EntityType.loadEntityRecursive((CompoundTag)this.serverPendingTrailerTag, (Level)serverLevel, entity -> {
                serverLevel.addWithUUID(entity);
                return entity;
            });
            this.setTrailerAndPulling((TrailerEntity)potentialTrailer2);
            this.serverPendingTrailerTag = null;
        }
        if (this.level().isClientSide() && this.clientPendingTrailerId != -1 && (potentialTrailer = this.level().getEntity(this.clientPendingTrailerId)) instanceof TrailerEntity) {
            this.clientPendingTrailerId = -1;
            this.setTrailerAndPulling((TrailerEntity)potentialTrailer);
        }
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (!(this.level().isClientSide() || this.trailer == null || this.trailer.isAlive() && (this.trailer.getPullingEntity() == null || this.trailer.getPullingEntity().equals((Object)this)))) {
            this.setTrailerAndPulling(null);
        }
        super.tick();
        this.tickLerp();
        this.onUpdateVehicle();
    }

    protected abstract void onUpdateVehicle();

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.level().isClientSide && this.isAlive()) {
            boolean isCreativeMode;
            Entity trueSource = source.getEntity();
            if (source.is(DamageTypeTags.IS_PROJECTILE) && trueSource != null && this.hasPassenger(trueSource)) {
                return false;
            }
            if (((Boolean)Config.SERVER.vehicleDamage.get()).booleanValue()) {
                this.setTimeSinceHit(10);
                this.setHealth(this.getHealth() - amount);
            }
            boolean bl = isCreativeMode = trueSource instanceof Player && ((Player)trueSource).isCreative();
            if (isCreativeMode || this.getHealth() < 0.0f) {
                this.onVehicleDestroyed((LivingEntity)trueSource);
                this.remove(Entity.RemovalReason.DISCARDED);
            }
            return true;
        }
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (((Boolean)Config.SERVER.vehicleDamage.get()).booleanValue() && distance >= 4.0f && this.getDeltaMovement().y() < -1.0) {
            float damage = distance / 2.0f;
            this.hurt(source, damage);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)ModSounds.ENTITY_VEHICLE_IMPACT.get(), SoundSource.AMBIENT, 1.0f, 1.0f);
        }
        return true;
    }

    protected void onVehicleDestroyed(LivingEntity entity) {
        WorkstationRecipe recipe;
        boolean isCreativeMode;
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)ModSounds.ENTITY_VEHICLE_DESTROYED.get(), SoundSource.AMBIENT, 1.0f, 0.5f);
        boolean bl = isCreativeMode = entity instanceof Player && ((Player)entity).isCreative();
        if (isCreativeMode || !this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) || (recipe = WorkstationRecipes.getRecipe(this.getType(), this.level())) != null) {
            // empty if block
        }
    }

    public int getDestroyedStage() {
        return 10 - (int)Math.max(1.0f, (float)((int)Math.ceil(10.0f * (this.getHealth() / this.getMaxHealth()))));
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d3 = Mth.wrapDegrees((double)(this.lerpYaw - (double)this.getYRot()));
            this.setYRot((float)((double)this.getYRot() + d3 / (double)this.lerpSteps));
            this.setXRot((float)((double)this.getXRot() + (this.lerpPitch - (double)this.getXRot()) / (double)this.lerpSteps));
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 10;
    }

    protected boolean canRide(Entity entityIn) {
        return true;
    }

    public void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.setPos(this.lerpX, this.lerpY, this.lerpZ);
            this.setYRot((float)this.lerpYaw);
            this.setXRot((float)this.lerpPitch);
        }
    }

    protected void applyYawToEntity(Entity passenger) {
        int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
        if (seatIndex != -1) {
            VehicleProperties properties = this.getProperties();
            Seat seat = properties.getSeats().get(seatIndex);
            passenger.setYBodyRot(this.getModifiedRotationYaw() + seat.getYawOffset());
            float f = Mth.wrapDegrees((float)(passenger.getYRot() - this.getModifiedRotationYaw() + seat.getYawOffset()));
            float f1 = Mth.clamp((float)f, (float)-120.0f, (float)120.0f);
            passenger.yRotO += f1 - f;
            passenger.setYRot(passenger.getYRot() + f1 - f);
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onPassengerTurned(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    public void push(double x, double y, double z) {
    }

    public void setTimeSinceHit(int timeSinceHit) {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit() {
        return (Integer)this.entityData.get(TIME_SINCE_HIT);
    }

    public void setMaxHealth(float maxHealth) {
        this.entityData.set(MAX_HEALTH, Float.valueOf(maxHealth));
    }

    public float getMaxHealth() {
        return ((Float)this.entityData.get(MAX_HEALTH)).floatValue();
    }

    public void setHealth(float health) {
        this.entityData.set(HEALTH, Float.valueOf(Math.min(this.getMaxHealth(), health)));
    }

    public float getHealth() {
        return ((Float)this.entityData.get(HEALTH)).floatValue();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void animateHurt(float yaw) {
        this.setTimeSinceHit(10);
    }

    public boolean canBeColored() {
        return false;
    }

    public void setColor(int color) {
        if (this.canBeColored()) {
            this.entityData.set(COLOR, color);
        }
    }

    public void setColorRGB(int r, int g, int b) {
        int color = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return (Integer)this.entityData.get(COLOR);
    }

    public int[] getColorRGB() {
        int color = (Integer)this.entityData.get(COLOR);
        return new int[]{color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF};
    }

    public boolean canMountTrailer() {
        return true;
    }

    public Vec3 getPartPositionAbsoluteVec(PartPosition position, float partialTicks) {
        VehicleProperties properties = this.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        Vec3 partVec = Vec3.ZERO;
        partVec = partVec.add(0.0, 0.5, 0.0);
        partVec = partVec.scale(position.getScale());
        partVec = partVec.add(0.0, -0.5, 0.0);
        partVec = partVec.add(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        partVec = partVec.add(0.0, (double)properties.getWheelOffset() * 0.0625, 0.0);
        partVec = partVec.add(0.0, (double)properties.getAxleOffset() * 0.0625, 0.0);
        partVec = partVec.add(0.0, 0.5, 0.0);
        partVec = partVec.scale(bodyPosition.getScale());
        partVec = partVec.add(0.0, -0.5, 0.0);
        partVec = partVec.add(0.0, 0.5, 0.0);
        partVec = partVec.add(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        partVec = partVec.yRot(-(this.yRotO + (this.getYRot() - this.yRotO) * partialTicks) * ((float)Math.PI / 180));
        partVec = partVec.add(this.xo + (this.getX() - this.xo) * (double)partialTicks, 0.0, 0.0);
        partVec = partVec.add(0.0, this.yo + (this.getY() - this.yo) * (double)partialTicks, 0.0);
        partVec = partVec.add(0.0, 0.0, this.zo + (this.getZ() - this.zo) * (double)partialTicks);
        return partVec;
    }

    protected static AABB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale) {
        return new AABB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    protected static AABB createBoxScaled(double x1, double y1, double z1, double x2, double y2, double z2, double scale) {
        return new AABB(x1 * 0.0625 * scale, y1 * 0.0625 * scale, z1 * 0.0625 * scale, x2 * 0.0625 * scale, y2 * 0.0625 * scale, z2 * 0.0625 * scale);
    }

    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.getYRot());
        this.seatTracker.write((FriendlyByteBuf)buffer);
    }

    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.yRotO = buffer.readFloat();
        this.setYRot(this.yRotO);
        this.seatTracker.read((FriendlyByteBuf)buffer);
    }

    public boolean canTowTrailer() {
        return false;
    }

    public void setTrailerAndPulling(@Nullable TrailerEntity trailer) {
        if (trailer != null) {
            trailer.setPulling(this);
            if (!this.level().isClientSide()) {
                this.entityData.set(TRAILER, trailer.getId(), true);
            }
            this.trailer = trailer;
        } else {
            if (this.trailer != null) {
                this.trailer.setPulling(null);
            }
            if (!this.level().isClientSide()) {
                this.entityData.set(TRAILER, -1, true);
            }
            this.trailer = null;
        }
    }

    @Nullable
    public TrailerEntity getTrailer() {
        return this.trailer;
    }

    public final VehicleProperties getProperties() {
        return VehicleProperties.get(this.getType());
    }

    public float getModifiedRotationYaw() {
        return this.getYRot();
    }

    public ItemStack getPickedResult(HitResult target) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType());
        if (entityId != null) {
            return VehicleCrateBlock.create((HolderLookup.Provider)this.level().registryAccess(), entityId, this.getColor(), null, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    public SeatTracker getSeatTracker() {
        return this.seatTracker;
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < this.getProperties().getSeats().size();
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        this.updatePassengerPosition(passenger, moveFunction);
    }

    protected void updatePassengerPosition(Entity passenger, Entity.MoveFunction moveFunction) {
        int seatIndex;
        if (this.hasPassenger(passenger) && (seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID())) != -1) {
            VehicleProperties properties = this.getProperties();
            if (seatIndex >= 0 && seatIndex < properties.getSeats().size()) {
                Seat seat = properties.getSeats().get(seatIndex);
                Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale() * 0.0625).yRot(-this.getModifiedRotationYaw() * ((float)Math.PI / 180) - 1.5707964f);
                Vec3 attachmentPoint = passenger.getVehicleAttachmentPoint((Entity)this);
                moveFunction.accept(passenger, this.getX() + seatVec.x - attachmentPoint.x, this.getY() + seatVec.y - attachmentPoint.y + 0.25, this.getZ() + seatVec.z - attachmentPoint.z);
                this.applyYawToEntity(passenger);
            }
        }
    }
}

