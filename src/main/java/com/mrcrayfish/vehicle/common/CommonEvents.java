/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.entity.living.LivingDeathEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickEmpty
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.neoforged.neoforge.event.tick.PlayerTickEvent$Post
 */
package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.FluidPipeItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageThrowVehicle;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class CommonEvents {
    private final Map<UUID, Integer> crouchTicks = new WeakHashMap<UUID, Integer>();
    private static final List<String> IGNORE_ITEMS;
    private static final List<String> IGNORE_SOUNDS;
    private static final List<String> IGNORE_ENTITIES;

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (CommonEvents.pickUpVehicle(event.getLevel(), event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
        }
    }

    public static boolean pickUpVehicle(Level world, Player player, InteractionHand hand, Entity targetEntity) {
        if (hand == InteractionHand.MAIN_HAND && !world.isClientSide && player.isCrouching() && !player.isSpectator() && ((Boolean)Config.SERVER.pickUpVehicles.get()).booleanValue()) {
            EntityType entityType;
            Entity vehicle;
            CompoundTag tagCompound;
            Optional optional;
            if (!HeldVehicleDataHandler.isHoldingVehicle(player)) {
                if (targetEntity instanceof VehicleEntity && !targetEntity.isVehicle() && targetEntity.isAlive()) {
                    CompoundTag tagCompound2 = new CompoundTag();
                    String id = CommonEvents.getEntityString(targetEntity);
                    if (id != null) {
                        ((VehicleEntity)targetEntity).setTrailerAndPulling(null);
                        tagCompound2.putString("id", id);
                        targetEntity.saveWithoutId(tagCompound2);
                        HeldVehicleDataHandler.setHeldVehicle(player, tagCompound2);
                        targetEntity.remove(Entity.RemovalReason.DISCARDED);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        return true;
                    }
                }
            } else if (targetEntity instanceof TrailerEntity && !targetEntity.isVehicle() && targetEntity.isAlive() && (optional = EntityType.byString((String)(tagCompound = HeldVehicleDataHandler.getHeldVehicle(player)).getString("id"))).isPresent() && (vehicle = (entityType = (EntityType)optional.get()).create(world)) instanceof VehicleEntity && ((VehicleEntity)vehicle).canMountTrailer()) {
                vehicle.load(tagCompound);
                vehicle.absMoveTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), targetEntity.getYRot(), targetEntity.getXRot());
                HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());
                world.addFreshEntity(vehicle);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 1.0f);
                vehicle.startRiding(targetEntity);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        Player player = event.getEntity();
        Level world = event.getLevel();
        if (!world.isClientSide()) {
            TrailerEntity trailer;
            Entity entity;
            int pulledTrailerId = (Integer)ModDataKeys.TRAILER.getValue(player);
            if (pulledTrailerId != -1 && !HeldVehicleDataHandler.isHoldingVehicle(player) && event.getFace() == Direction.UP && (entity = world.getEntity(pulledTrailerId)) instanceof TrailerEntity && (trailer = (TrailerEntity)entity).getPullingEntity() == player) {
                HitResult result = player.pick(10.0, 0.0f, false);
                Vec3 clickedVec = result.getLocation();
                if (clickedVec != null) {
                    float rotation = (player.getYHeadRot() + 90.0f) % 360.0f;
                    trailer.absMoveTo(clickedVec.x, clickedVec.y, clickedVec.z, rotation, trailer.getXRot());
                }
                trailer.resetPullingOrMaybeTrailer();
                ModDataKeys.TRAILER.setValue(player, -1);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
            if (HeldVehicleDataHandler.isHoldingVehicle(player)) {
                if (event.getFace() == Direction.UP) {
                    BlockPos pos = event.getPos();
                    BlockEntity tileEntity = event.getLevel().getBlockEntity(pos);
                    if (tileEntity instanceof JackTileEntity) {
                        JackTileEntity jack = (JackTileEntity)tileEntity;
                        if (jack.getJack() == null) {
                            CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                            EntityType.byString((String)tagCompound.getString("id")).ifPresent(entityType -> {
                                Entity spawnedEntity = entityType.create(world);
                                if (spawnedEntity instanceof VehicleEntity) {
                                    spawnedEntity.load(tagCompound);
                                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());
                                    spawnedEntity.fallDistance = 0.0f;
                                    spawnedEntity.setYRot((player.getYHeadRot() + 90.0f) % 360.0f);
                                    jack.setVehicle((VehicleEntity)spawnedEntity);
                                    if (jack.getJack() != null) {
                                        EntityJack entityJack = jack.getJack();
                                        entityJack.rideTick();
                                        spawnedEntity.moveTo(spawnedEntity.getX(), spawnedEntity.getY(), spawnedEntity.getZ(), spawnedEntity.getYRot(), spawnedEntity.getXRot());
                                    }
                                    world.addFreshEntity(spawnedEntity);
                                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 1.0f);
                                }
                            });
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        return;
                    }
                }
                if (player.isCrouching()) {
                    HitResult result = player.pick(10.0, 0.0f, false);
                    Vec3 clickedVec = result.getLocation();
                    if (clickedVec == null || event.getFace() != Direction.UP) {
                        event.setCanceled(true);
                        return;
                    }
                    CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                    EntityType.byString((String)tagCompound.getString("id")).ifPresent(entityType -> {
                        Entity spawnedEntity = entityType.create(player.level());
                        if (spawnedEntity instanceof VehicleEntity) {
                            spawnedEntity.load(tagCompound);
                            float rotation = (player.getYHeadRot() + 90.0f) % 360.0f;
                            Vec3 heldOffset = ((VehicleEntity)spawnedEntity).getProperties().getHeldOffset().yRot((float)Math.toRadians(-player.getYHeadRot()));
                            spawnedEntity.absMoveTo(clickedVec.x + heldOffset.x * 0.0625, clickedVec.y, clickedVec.z + heldOffset.z * 0.0625, rotation, 0.0f);
                            spawnedEntity.fallDistance = 0.0f;
                            if (!world.noCollision(spawnedEntity, spawnedEntity.getBoundingBox().inflate(0.0, -0.1, 0.0))) {
                                return;
                            }
                            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());
                            world.addFreshEntity(spawnedEntity);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 1.0f);
                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS);
                        }
                    });
                }
            }
        } else if (HeldVehicleDataHandler.isHoldingVehicle(player)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        Level world = event.getLevel();
        if (world.isClientSide) {
            Player player = event.getEntity();
            float reach = (float)player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            reach = player.isCreative() ? reach : reach - 0.5f;
            HitResult result = player.pick((double)reach, 0.0f, false);
            if (result.getType() == HitResult.Type.BLOCK) {
                return;
            }
            if (HeldVehicleDataHandler.isHoldingVehicle(player) && player.isCrouching()) {
                PacketHandler.sendToServer(new MessageThrowVehicle());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        Level world = event.getLevel();
        if (world.isClientSide) {
            Player player = event.getEntity();
            float reach = (float)player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
            reach = player.isCreative() ? reach : reach - 0.5f;
            HitResult result = player.pick((double)reach, 0.0f, false);
            if (result.getType() == HitResult.Type.BLOCK) {
                return;
            }
            if (HeldVehicleDataHandler.isHoldingVehicle(player)) {
                if (player.isCrouching()) {
                    PacketHandler.sendToServer(new MessageThrowVehicle());
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static String getEntityString(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            this.dropVehicle(player);
        }
    }

    private void dropVehicle(Player player) {
        CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if (!tagCompound.isEmpty()) {
            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());
            EntityType.byString((String)tagCompound.getString("id")).ifPresent(entityType -> {
                Entity vehicle = entityType.create(player.level());
                if (vehicle instanceof VehicleEntity) {
                    vehicle.load(tagCompound);
                    float rotation = (player.getYHeadRot() + 90.0f) % 360.0f;
                    Vec3 heldOffset = ((VehicleEntity)vehicle).getProperties().getHeldOffset().yRot((float)Math.toRadians(-player.getYHeadRot()));
                    vehicle.absMoveTo(player.getX() + heldOffset.x * 0.0625, player.getY() + (double)player.getEyeHeight() + heldOffset.y * 0.0625, player.getZ() + heldOffset.z * 0.0625, rotation, 0.0f);
                    player.level().addFreshEntity(vehicle);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        BlockEntity tileEntity;
        Optional pos;
        Player player = event.getEntity();
        Level world = player.level();
        if (!world.isClientSide()) {
            UUID uuid = player.getUUID();
            if (player.isCrouching()) {
                int trailerId;
                int ticks = this.crouchTicks.getOrDefault(uuid, 0) + 1;
                this.crouchTicks.put(uuid, ticks);
                if (ticks >= 10 && (trailerId = ((Integer)ModDataKeys.TRAILER.getValue(player)).intValue()) != -1) {
                    Entity entity = world.getEntity(trailerId);
                    if (entity instanceof TrailerEntity) {
                        ((TrailerEntity)entity).resetPullingOrMaybeTrailer();
                    }
                    ModDataKeys.TRAILER.setValue(player, -1);
                }
            } else {
                this.crouchTicks.remove(uuid);
            }
        }
        if (!world.isClientSide && player.isSpectator()) {
            this.dropVehicle(player);
        }
        if ((pos = (Optional)ModDataKeys.GAS_PUMP.getValue(player)).isPresent() && !((tileEntity = world.getBlockEntity((BlockPos)pos.get())) instanceof GasPumpTileEntity)) {
            ModDataKeys.GAS_PUMP.setValue(player, Optional.empty());
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (((Optional)ModDataKeys.GAS_PUMP.getValue((Player)event.getEntity())).isPresent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockEntity relativeTileEntity;
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.getBlock() != ModBlocks.GAS_PUMP.get() && ((Optional)ModDataKeys.GAS_PUMP.getValue((Player)event.getEntity())).isPresent()) {
            event.setCanceled(true);
        } else if (event.getItemStack().getItem() instanceof FluidPipeItem && (relativeTileEntity = event.getLevel().getBlockEntity(event.getPos())) != null && event.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, event.getPos(), event.getFace()) != null) {
            event.setUseBlock(TriState.FALSE);
            event.setUseItem(TriState.TRUE);
        }
    }

    static {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add("body");
        builder.add("atv");
        builder.add("go_kart");
        IGNORE_ITEMS = builder.build();
        builder = ImmutableList.builder();
        builder.add("idle");
        builder.add("driving");
        IGNORE_SOUNDS = builder.build();
        builder = ImmutableList.builder();
        builder.add("vehicle_atv");
        builder.add("couch");
        builder.add("bath");
        IGNORE_ENTITIES = builder.build();
    }
}


