package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Author: MrCrayfish
 */
public class CommonEvents
{
    private final Map<UUID, Integer> crouchTicks = new WeakHashMap<>();
    private static final List<String> IGNORE_ITEMS;
    private static final List<String> IGNORE_SOUNDS;
    private static final List<String> IGNORE_ENTITIES;

    static
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
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

    // MissingMappingsEvent removed in NeoForge 1.21.1; use datamap system instead

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if(pickUpVehicle(event.getLevel(), event.getEntity(), event.getHand(), event.getTarget()))
        {
            event.setCanceled(true);
        }
    }

    public static boolean pickUpVehicle(Level world, Player player, InteractionHand hand, Entity targetEntity)
    {
        if(hand == InteractionHand.MAIN_HAND && !world.isClientSide && player.isCrouching() && !player.isSpectator() && Config.SERVER.pickUpVehicles.get())
        {
            if(!HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(targetEntity instanceof VehicleEntity && !targetEntity.isVehicle() && targetEntity.isAlive())
                {
                    CompoundTag tagCompound = new CompoundTag();
                    String id = getEntityString(targetEntity);
                    if(id != null)
                    {
                        ((VehicleEntity) targetEntity).setTrailerAndPulling(null);

                        tagCompound.putString("id", id);
                        targetEntity.saveWithoutId(tagCompound);

                        //Updates the held vehicle capability
                        HeldVehicleDataHandler.setHeldVehicle(player, tagCompound);

                        //Removes the entity from the world
                        targetEntity.remove(RemovalReason.DISCARDED);

                        //Plays pick up sound
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                        return true;
                    }
                }
            }
            else if(targetEntity instanceof TrailerEntity && !targetEntity.isVehicle() && targetEntity.isAlive())
            {
                CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                Optional<EntityType<?>> optional = EntityType.byString(tagCompound.getString("id"));
                if(optional.isPresent())
                {
                    EntityType<?> entityType = optional.get();
                    Entity vehicle = entityType.create(world);
                    if(vehicle instanceof VehicleEntity && ((VehicleEntity) vehicle).canMountTrailer())
                    {
                        vehicle.load(tagCompound);
                        vehicle.absMoveTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), targetEntity.getYRot(), targetEntity.getXRot());

                        //Updates the player capability
                        HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());

                        //Plays place sound
                        world.addFreshEntity(vehicle);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
                        vehicle.startRiding(targetEntity);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getHand() == InteractionHand.OFF_HAND) return;

        Player player = event.getEntity();
        Level world = event.getLevel();
        if(!world.isClientSide())
        {
            int pulledTrailerId = ModDataKeys.TRAILER.getValue(player);
            if(pulledTrailerId != -1 && !HeldVehicleDataHandler.isHoldingVehicle(player) && event.getFace() == Direction.UP)
            {
                Entity entity = world.getEntity(pulledTrailerId);
                if(entity instanceof TrailerEntity trailer && trailer.getPullingEntity() == player)
                {
                    HitResult result = player.pick(10.0, 0.0F, false);
                    Vec3 clickedVec = result.getLocation();
                    if(clickedVec != null)
                    {
                        float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                        trailer.absMoveTo(clickedVec.x, clickedVec.y, clickedVec.z, rotation, trailer.getXRot());
                    }

                    trailer.resetPullingOrMaybeTrailer();
                    ModDataKeys.TRAILER.setValue(player, -1);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            if(HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(event.getFace() == Direction.UP)
                {
                    BlockPos pos = event.getPos();
                    BlockEntity tileEntity = event.getLevel().getBlockEntity(pos);
                    if(tileEntity instanceof JackTileEntity)
                    {
                        JackTileEntity jack = (JackTileEntity) tileEntity;
                        if(jack.getJack() == null)
                        {
                            CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                            EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
                            {
                                Entity entity = entityType.create(world);
                                if(entity instanceof VehicleEntity)
                                {
                                    entity.load(tagCompound);

                                    //Updates the player capability
                                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());

                                    entity.fallDistance = 0.0F;
                                    entity.setYRot((player.getYHeadRot() + 90F) % 360.0F);

                                    jack.setVehicle((VehicleEntity) entity);
                                    if(jack.getJack() != null)
                                    {
                                        EntityJack entityJack = jack.getJack();
                                        entityJack.rideTick();
                                        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                                    }
                                    world.addFreshEntity(entity);
                                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                            });
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        return;
                    }
                }

                if(player.isCrouching())
                {
                    //Vec3 clickedVec = event.getHitVec(); //TODO WHY DID FORGE REMOVE THIS. GOING TO CREATE A PATCH
                    HitResult result = player.pick(10.0, 0.0F, false);
                    Vec3 clickedVec = result.getLocation();
                    if(clickedVec == null || event.getFace() != Direction.UP)
                    {
                        event.setCanceled(true);
                        return;
                    }

                    CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                    EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
                    {
                        Entity entity = entityType.create(player.level());
                        if(entity instanceof VehicleEntity)
                        {
                            entity.load(tagCompound);

                            //Sets the positions and spawns the entity
                            float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                            Vec3 heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));

                            entity.absMoveTo(clickedVec.x + heldOffset.x * 0.0625D, clickedVec.y, clickedVec.z + heldOffset.z * 0.0625D, rotation, 0F);
                            entity.fallDistance = 0.0F;

                            //Checks if vehicle intersects with any blocks
                            if(!world.noCollision(entity, entity.getBoundingBox().inflate(0, -0.1, 0)))
                                return;

                            //Updates the player capability
                            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());

                            //Plays place sound
                            world.addFreshEntity(entity);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);

                            event.setCanceled(true);
                            event.setCancellationResult(InteractionResult.SUCCESS);
                        }
                    });
                }
            }
        }
        else if(HeldVehicleDataHandler.isHoldingVehicle(player))
        {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if(event.getHand() == InteractionHand.OFF_HAND)
            return;
        Level world = event.getLevel();
        if(world.isClientSide)
        {
            Player player = event.getEntity();
            float reach = (float) player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE).getValue();
            reach = player.isCreative() ? reach : reach - 0.5F;
            HitResult result = player.pick(reach, 0.0F, false);
            if(result.getType() == HitResult.Type.BLOCK)
                return;
            if(HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(player.isCrouching())
                {
                    PacketHandler.sendToServer(new MessageThrowVehicle());
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if(event.getHand() == InteractionHand.OFF_HAND)
            return;
        Level world = event.getLevel();
        if(world.isClientSide)
        {
            Player player = event.getEntity();
            float reach = (float) player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE).getValue();
            reach = player.isCreative() ? reach : reach - 0.5F;
            HitResult result = player.pick(reach, 0.0F, false);
            if(result.getType() == HitResult.Type.BLOCK)
                return;
            if(HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(player.isCrouching())
                {
                    PacketHandler.sendToServer(new MessageThrowVehicle());
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static String getEntityString(Entity entity)
    {
        // FIXME
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player)
        {
            Player player = (Player) entity;
            this.dropVehicle(player);
        }
    }

    private void dropVehicle(Player player)
    {
        CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if(!tagCompound.isEmpty())
        {
            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundTag());

            EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
            {
                Entity vehicle = entityType.create(player.level());
                if(vehicle instanceof VehicleEntity)
                {
                    vehicle.load(tagCompound);
                    float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                    Vec3 heldOffset = ((VehicleEntity) vehicle).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));
                    vehicle.absMoveTo(player.getX() + heldOffset.x * 0.0625D, player.getY() + player.getEyeHeight() + heldOffset.y * 0.0625D, player.getZ() + heldOffset.z * 0.0625D, rotation, 0F);
                    player.level().addFreshEntity(vehicle);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event)
    {
        Player player = event.getEntity();
        Level world = player.level();
        if(!world.isClientSide())
            {
                UUID uuid = player.getUUID();
                if(player.isCrouching())
                {
                    int ticks = this.crouchTicks.getOrDefault(uuid, 0) + 1;
                    this.crouchTicks.put(uuid, ticks);
                    if(ticks >= 10)
                    {
                        int trailerId = ModDataKeys.TRAILER.getValue(player);
                        if(trailerId != -1)
                        {
                            Entity entity = world.getEntity(trailerId);
                            if(entity instanceof TrailerEntity)
                            {
                                ((TrailerEntity) entity).resetPullingOrMaybeTrailer();
                            }
                            ModDataKeys.TRAILER.setValue(player, -1);
                        }
                    }
                }
                else
                {
                    this.crouchTicks.remove(uuid);
                }
            }

            if(!world.isClientSide && player.isSpectator())
            {
                this.dropVehicle(player);
            }

            Optional<BlockPos> pos = ModDataKeys.GAS_PUMP.getValue(player);
            if(pos.isPresent())
            {
                BlockEntity tileEntity = world.getBlockEntity(pos.get());
                if(!(tileEntity instanceof GasPumpTileEntity))
                {
                    ModDataKeys.GAS_PUMP.setValue(player, Optional.empty());
                }
            }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event)
    {
        if(ModDataKeys.GAS_PUMP.getValue(event.getEntity()).isPresent())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP.get() && ModDataKeys.GAS_PUMP.getValue(event.getEntity()).isPresent())
        {
            event.setCanceled(true);
        }
        else if(event.getItemStack().getItem() instanceof FluidPipeItem)
        {
            BlockEntity relativeTileEntity = event.getLevel().getBlockEntity(event.getPos());
            if(relativeTileEntity != null && event.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, event.getPos(), event.getFace()) != null)
            {
                event.setUseBlock(net.neoforged.neoforge.common.util.TriState.FALSE);
                event.setUseItem(net.neoforged.neoforge.common.util.TriState.TRUE);
            }
        }
    }
}
