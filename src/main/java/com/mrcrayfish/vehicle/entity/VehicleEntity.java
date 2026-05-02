package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class VehicleEntity extends Entity implements IEntityAdditionalSpawnData, EntityRayTracer.IEntityRayTraceable
{
    public static final int[] DYE_TO_COLOR = new int[] {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_HEALTH = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TRAILER = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);

    protected TrailerEntity trailer = null;
    private CompoundTag serverPendingTrailerTag = null;
    private int clientPendingTrailerId = -1;

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;

    protected SeatTracker seatTracker;

    public VehicleEntity(EntityType<?> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.seatTracker = new SeatTracker(this);
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(MAX_HEALTH, 100F);
        this.entityData.define(HEALTH, 100F);
        this.entityData.define(COLOR, 16383998);
        this.entityData.define(TRAILER, -1);

        if(this.level().isClientSide)
        {
            this.onClientInit();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientInit() {}

    /* Overridden to prevent odd step sound when driving vehicles. Ain't no subclasses getting
     * the ability to override this. */
    @Override
    protected final void playStepSound(BlockPos pos, BlockState blockIn) {}

    @Override //TODO hmmmmmmmm
    public AABB getBoundingBoxForCulling()
    {
        return this.getBoundingBox().inflate(1);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        if(!level().isClientSide && !player.isCrouching())
        {
            int trailerId = ModDataKeys.TRAILER.getValue(player);
            if(trailerId != -1)
            {
                boolean attached = false;
                if(this.getVehicle() == null && this.canTowTrailer() && this.getTrailer() == null)
                {
                    Entity entity = level().getEntity(trailerId);
                    if(entity instanceof TrailerEntity && entity != this)
                    {
                        TrailerEntity trailer = (TrailerEntity) entity;
                        this.setTrailerAndPulling(trailer);
                        ModDataKeys.TRAILER.setValue(player, -1);
                        attached = true;
                    }
                }
                if(attached)
                {
                    return InteractionResult.SUCCESS;
                }
                // If we didn't attach (stale trailer id or other failure), continue to other interaction handling
            }

            ItemStack heldItem = player.getItemInHand(hand);
            if(heldItem.getItem() instanceof SprayCanItem)
            {
                if(this.canBeColored())
                {
                    CompoundTag compound = heldItem.getTag();
                    if(compound != null)
                    {
                        if(!compound.contains("RemainingSprays", Tag.TAG_INT))
                        {
                            compound.putInt("RemainingSprays", ModItems.SPRAY_CAN.get().getCapacity(heldItem));
                        }
                        int remainingSprays = compound.getInt("RemainingSprays");
                        if(compound.contains("Color", Tag.TAG_INT) && remainingSprays > 0)
                        {
                            int color = compound.getInt("Color");
                            if(this.getColor() != color)
                            {
                                this.setColor(compound.getInt("Color"));
                                player.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ITEM_SPRAY_CAN_SPRAY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                compound.putInt("RemainingSprays", remainingSprays - 1);
                            }
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else if(heldItem.getItem() == ModItems.HAMMER.get() && this.getVehicle() instanceof EntityJack)
            {
                if(this.getHealth() < this.getMaxHealth())
                {
                    heldItem.hurtAndBreak(1, player, playerEntity -> player.broadcastBreakEvent(hand));
                    this.setHealth(this.getHealth() + 5F);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_THUD.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * random.nextFloat());
                    player.swing(hand);
                    if(player instanceof ServerPlayer)
                    {
                        ((ServerPlayer) player).connection.send(new ClientboundAnimatePacket(player, hand == InteractionHand.MAIN_HAND ? ClientboundAnimatePacket.SWING_MAIN_HAND : ClientboundAnimatePacket.SWING_OFF_HAND));
                    }
                    if(this.getHealth() == this.getMaxHealth())
                    {
                        if(level() instanceof ServerLevel)
                        {
                            //TODO send as single packet instead of multiple
                            int count = (int) (50 * (this.getBbWidth() * this.getBbHeight()));
                            for(int i = 0; i < count; i++)
                            {
                                double width = this.getBbWidth() * 2;
                                double height = this.getBbHeight() * 1.5;

                                Vec3 heldOffset = this.getProperties().getHeldOffset().yRot((float) Math.toRadians(-this.getYRot()));
                                double x = this.getX() + width * random.nextFloat() - width / 2 + heldOffset.z * 0.0625;
                                double y = this.getY() + height * random.nextFloat();
                                double z = this.getZ() + width * random.nextFloat() - width / 2 + heldOffset.x * 0.0625;

                                double d0 = random.nextGaussian() * 0.02D;
                                double d1 = random.nextGaussian() * 0.02D;
                                double d2 = random.nextGaussian() * 0.02D;
                                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, d0, d1, d2, 1.0);
                            }
                        }
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else if(this.canRide(player))
            {
                int seatIndex = this.seatTracker.getClosestAvailableSeatToPlayer(player);
                if(seatIndex != -1)
                {
                    if(player.startRiding(this))
                    {
                        this.getSeatTracker().setSeatIndex(seatIndex, player.getUUID());
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound)
    {
        if(compound.contains("Color", Tag.TAG_INT_ARRAY))
        {
            int[] c = compound.getIntArray("Color");
            if(c.length == 3)
            {
                int color = ((c[0] & 0xFF) << 16) | ((c[1] & 0xFF) << 8) | ((c[2] & 0xFF));
                this.setColor(color);
            }
        }
        else if(compound.contains("Color", Tag.TAG_INT))
        {
            int index = compound.getInt("Color");
            if(index >= 0 && index < DYE_TO_COLOR.length)
            {
                this.setColor(DYE_TO_COLOR[index]);
            }
            compound.remove("Color");
        }
        if(compound.contains("MaxHealth", Tag.TAG_FLOAT))
        {
            this.setMaxHealth(compound.getFloat("MaxHealth"));
        }
        if(compound.contains("Health", Tag.TAG_FLOAT))
        {
            this.setHealth(compound.getFloat("Health"));
        }
        if(compound.contains("SeatTracker", Tag.TAG_COMPOUND))
        {
            this.seatTracker.read(compound.getCompound("SeatTracker"));
        }
        if(compound.contains("Trailer", Tag.TAG_COMPOUND))
        {
            this.serverPendingTrailerTag = compound.getCompound("Trailer");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound)
    {
        compound.putIntArray("Color", this.getColorRGB());
        compound.putFloat("MaxHealth", this.getMaxHealth());
        compound.putFloat("Health", this.getHealth());
        compound.put("SeatTracker", this.seatTracker.write());

        if(this.trailer != null)
        {
            CompoundTag trailerTag = new CompoundTag();
            if(this.trailer.saveAsPassenger(trailerTag))
            {
                compound.put("Trailer", trailerTag);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if(this.level().isClientSide() && key.equals(TRAILER))
        {
            int requestedTrailerId = this.entityData.get(TRAILER);
            if(requestedTrailerId == -1)
            {
                this.setTrailerAndPulling(null);
            }
            else
            {
                this.clientPendingTrailerId = requestedTrailerId;
            }
        }
    }

    @Override
    public void setRemoved(RemovalReason reason) {
        super.setRemoved(reason);

        if(!this.level().isClientSide() && reason.shouldSave() && this.trailer != null)
        {
            this.trailer.remove(RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    @Override
    public Component getName() {
        return Component.literal(getTypeName().getString() + " id = " + getId());
    }

    @Override
    public void tick()
    {
        if(!this.level().isClientSide() && this.serverPendingTrailerTag != null)
        {
            ServerLevel serverLevel = (ServerLevel) this.level();
            Entity potentialTrailer = EntityType.loadEntityRecursive(this.serverPendingTrailerTag, serverLevel, entity -> {
                serverLevel.addWithUUID(entity);
                return entity;
            });
            this.setTrailerAndPulling((TrailerEntity) potentialTrailer);
            this.serverPendingTrailerTag = null;
        }
        if(this.level().isClientSide() && this.clientPendingTrailerId != -1)
        {
            Entity potentialTrailer = this.level().getEntity(this.clientPendingTrailerId);
            if(potentialTrailer instanceof TrailerEntity)
            {
                this.clientPendingTrailerId = -1;
                this.setTrailerAndPulling((TrailerEntity) potentialTrailer);
            }
        }

        if(this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        /*this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();*/

        if(!this.level().isClientSide() && this.trailer != null && (!this.trailer.isAlive() || (this.trailer.getPullingEntity() != null && !this.trailer.getPullingEntity().equals(this))))
        {
            this.setTrailerAndPulling(null);
        }

        super.tick();
        this.tickLerp();
        this.onUpdateVehicle();
    }

    protected abstract void onUpdateVehicle();

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if(this.isInvulnerableTo(source))
        {
            return false;
        }
        else if(!this.level().isClientSide && this.isAlive())
        {
            Entity trueSource = source.getEntity();
            if(source.is(DamageTypeTags.IS_PROJECTILE) && trueSource != null && this.hasPassenger(trueSource)) // FIXME
            {
                return false;
            }
            else
            {
                if(Config.SERVER.vehicleDamage.get())
                {
                    this.setTimeSinceHit(10);
                    this.setHealth(this.getHealth() - amount);
                }
                boolean isCreativeMode = trueSource instanceof Player && ((Player) trueSource).isCreative();
                if(isCreativeMode || this.getHealth() < 0.0F)
                {
                    this.onVehicleDestroyed((LivingEntity) trueSource);
                    this.remove(RemovalReason.DISCARDED);
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        if(Config.SERVER.vehicleDamage.get() && distance >= 4F && this.getDeltaMovement().y() < -1.0F)
        {
            float damage = distance / 2F;
            this.hurt(source, damage); // FIXME
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_IMPACT.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
        }
        return true;
    }

    protected void onVehicleDestroyed(LivingEntity entity)
    {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_DESTROYED.get(), SoundSource.AMBIENT, 1.0F, 0.5F);

        boolean isCreativeMode = entity instanceof Player && ((Player) entity).isCreative();
        if(!isCreativeMode && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            WorkstationRecipe recipe = WorkstationRecipes.getRecipe(this.getType(), this.level());
            if(recipe != null)
            {
                //TODO make vehicle inoperable instead of destroying
                /*List<ItemStack> materials = recipe.getMaterials();
                for(ItemStack stack : materials)
                {
                    ItemStack copy = stack.copy();
                    int shrink = copy.getCount() / 2;
                    if(shrink > 0)
                        copy.shrink(this.random.nextInt(shrink + 1));
                    InventoryUtil.spawnItemStack(this.level, this.getX(), this.getY(), this.getZ(), copy);
                }*/
            }
        }
    }

    public int getDestroyedStage()
    {
        return 10 - (int) Math.max(1.0F, (int) Math.ceil(10.0F * (this.getHealth() / this.getMaxHealth())));
    }

    /**
     * Smooths the rendering on servers
     */
    private void tickLerp()
    {
        if(this.isControlledByLocalInstance())
        {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if(this.lerpSteps > 0)
        {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYaw - (double) this.getYRot());
            this.setYRot((float) ((double) this.getYRot() + d3 / (double) this.lerpSteps));
            this.setXRot((float) ((double) this.getXRot() + (this.lerpPitch - (double) this.getXRot()) / (double) this.lerpSteps));
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }


    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 10;
    }

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return true;
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.isControlledByLocalInstance() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.setPos(this.lerpX, this.lerpY, this.lerpZ);
            this.setYRot((float) this.lerpYaw);
            this.setXRot((float) this.lerpPitch);
        }
    }

    protected void applyYawToEntity(Entity passenger)
    {
        int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
        if(seatIndex != -1)
        {
            VehicleProperties properties = this.getProperties();
            Seat seat = properties.getSeats().get(seatIndex);
            passenger.setYBodyRot(this.getModifiedRotationYaw() + seat.getYawOffset());
            float f = Mth.wrapDegrees(passenger.getYRot() - this.getModifiedRotationYaw() + seat.getYawOffset());
            float f1 = Mth.clamp(f, -120.0F, 120.0F);
            passenger.yRotO += f1 - f;
            passenger.setYRot(passenger.getYRot() + f1 - f);
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPassengerTurned(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    public void push(double x, double y, double z) {}

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit)
    {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.entityData.get(TIME_SINCE_HIT);
    }

    /**
     * Sets the max health of the vehicle.
     */
    public void setMaxHealth(float maxHealth)
    {
        this.entityData.set(MAX_HEALTH, maxHealth);
    }

    /**
     * Gets the max health of the vehicle.
     */
    public float getMaxHealth()
    {
        return this.entityData.get(MAX_HEALTH);
    }

    /**
     * Sets the current health of the vehicle.
     */
    public void setHealth(float health)
    {
        this.entityData.set(HEALTH, Math.min(this.getMaxHealth(), health));
    }

    /**
     * Gets the current health of the vehicle.
     */
    public float getHealth()
    {
        return this.entityData.get(HEALTH);
    }

    //TODO look into this and why its here. May have to send vanilla event to client
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateHurt(float yaw)
    {
        this.setTimeSinceHit(10);
    }

    public boolean canBeColored()
    {
        return false;
    }

    public void setColor(int color)
    {
        if(this.canBeColored())
        {
            this.entityData.set(COLOR, color);
        }
    }

    public void setColorRGB(int r, int g, int b)
    {
        int color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        this.entityData.set(COLOR, color);
    }

    public int getColor()
    {
        return this.entityData.get(COLOR);
    }

    public int[] getColorRGB()
    {
        int color = this.entityData.get(COLOR);
        return new int[]{ (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF };
    }

    public boolean canMountTrailer()
    {
        return true;
    }

    /**
     * Gets the absolute position of a part in the world
     *
     * @param position the position definition of the part
     * @return a Vec3 containing the exact location
     */
    public Vec3 getPartPositionAbsoluteVec(PartPosition position, float partialTicks)
    {
        VehicleProperties properties = this.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        Vec3 partVec = Vec3.ZERO;
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.scale(position.getScale());
        partVec = partVec.add(0, -0.5, 0);
        partVec = partVec.add(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        partVec = partVec.add(0, properties.getWheelOffset() * 0.0625, 0);
        partVec = partVec.add(0, properties.getAxleOffset() * 0.0625, 0);
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.scale(bodyPosition.getScale());
        partVec = partVec.add(0, -0.5, 0);
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.add(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        partVec = partVec.yRot(-(this.yRotO + (this.getYRot() - this.yRotO) * partialTicks) * 0.017453292F);
        partVec = partVec.add(this.xo + (this.getX() - this.xo) * partialTicks, 0, 0);
        partVec = partVec.add(0, this.yo + (this.getY() - this.yo) * partialTicks, 0);
        partVec = partVec.add(0, 0, this.zo + (this.getZ() - this.zo) * partialTicks);
        return partVec;
    }

    protected static AABB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AABB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    protected static AABB createBoxScaled(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AABB(x1 * 0.0625 * scale, y1 * 0.0625 * scale, z1 * 0.0625 * scale, x2 * 0.0625 * scale, y2 * 0.0625 * scale, z2 * 0.0625 * scale);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(this.getYRot());
        this.seatTracker.write(buffer);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer)
    {
        this.setYRot(this.yRotO = buffer.readFloat());
        this.seatTracker.read(buffer);
    }

    public boolean canTowTrailer()
    {
        return false;
    }

    public void setTrailerAndPulling(@Nullable TrailerEntity trailer)
    {
        if(trailer != null)
        {
            trailer.setPulling(this);
            if(!this.level().isClientSide())
            {
                this.entityData.set(TRAILER, trailer.getId(), true);
            }
            this.trailer = trailer;
        }
        else
        {
            if (this.trailer != null)
            {
                this.trailer.setPulling(null);
            }
            if(!this.level().isClientSide())
            {
                this.entityData.set(TRAILER, -1, true);
            }
            this.trailer = null;
        }
    }

    @Nullable
    public TrailerEntity getTrailer()
    {
        return trailer;
    }

    public final VehicleProperties getProperties()
    {
        return VehicleProperties.get(this.getType());
    }

    public float getModifiedRotationYaw()
    {
        return this.getYRot();
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(this.getType());
        if(entityId != null)
        {
            return VehicleCrateBlock.create(entityId, this.getColor(), null, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public SeatTracker getSeatTracker()
    {
        return this.seatTracker;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return this.getPassengers().size() < this.getProperties().getSeats().size();
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction)
    {
        super.positionRider(passenger, moveFunction);
        this.updatePassengerPosition(passenger, moveFunction);
    }

    protected void updatePassengerPosition(Entity passenger, MoveFunction moveFunction)
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
                    Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).yRot(-this.getModifiedRotationYaw() * 0.017453292F - ((float) Math.PI / 2F));
                    moveFunction.accept(passenger, this.getX() + seatVec.x, this.getY() + seatVec.y, this.getZ() + seatVec.z);
                    this.applyYawToEntity(passenger);
                }
            }
        }
    }
}
