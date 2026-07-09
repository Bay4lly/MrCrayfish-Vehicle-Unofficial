/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.message.IMessage;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCraftVehicle
implements IMessage<MessageCraftVehicle> {
    public static final CustomPacketPayload.Type<MessageCraftVehicle> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"craft_vehicle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCraftVehicle> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageCraftVehicle)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageCraftVehicle().decode((RegistryFriendlyByteBuf)buf));
    private String vehicleId;
    private BlockPos pos;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageCraftVehicle() {
    }

    public MessageCraftVehicle(String vehicleId, BlockPos pos) {
        this.vehicleId = vehicleId;
        this.pos = pos;
    }

    @Override
    public void encode(MessageCraftVehicle message, RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(message.vehicleId, 128);
        buffer.writeBlockPos(message.pos);
    }

    @Override
    public MessageCraftVehicle decode(RegistryFriendlyByteBuf buffer) {
        return new MessageCraftVehicle(buffer.readUtf(128), buffer.readBlockPos());
    }

    @Override
    public void handle(MessageCraftVehicle message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack workstationWheelStack;
            ItemStack workstationEngineStack;
            ItemStack workstationDyeStack;
            ServerPlayer player = (ServerPlayer)context.player();
            if (player == null) {
                return;
            }
            Level world = player.level();
            if (!(player.containerMenu instanceof WorkstationContainer)) {
                return;
            }
            WorkstationContainer workstation = (WorkstationContainer)player.containerMenu;
            if (!workstation.getPos().equals((Object)message.pos)) {
                return;
            }
            ResourceLocation entityId = ResourceLocation.parse((String)message.vehicleId);
            if (((List)Config.SERVER.disabledVehicles.get()).contains(entityId.toString())) {
                return;
            }
            EntityType entityType = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(entityId);
            if (entityType == null) {
                return;
            }
            if (VehicleRegistry.getRegisteredVehicleTypes().stream().noneMatch(entry -> ((EntityType)entry.get()).equals(entityType))) {
                return;
            }
            WorkstationRecipe recipe = WorkstationRecipes.getRecipe(entityType, world);
            if (recipe == null || !recipe.hasMaterials((Player)player)) {
                return;
            }
            Entity entity = entityType.create(world);
            if (!(entity instanceof VehicleEntity)) {
                return;
            }
            IEngineType engineType = EngineType.NONE;
            VehicleEntity vehicle = (VehicleEntity)entity;
            if (vehicle instanceof PoweredVehicleEntity) {
                ItemStack wheel;
                PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity)entity;
                engineType = entityPoweredVehicle.getProperties().getEngineType();
                WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();
                ItemStack workstationEngine = workstationTileEntity.getItem(1);
                if (workstationEngine.isEmpty() || !(workstationEngine.getItem() instanceof EngineItem)) {
                    return;
                }
                IEngineType engineType2 = ((EngineItem)workstationEngine.getItem()).getEngineType();
                if (engineType != EngineType.NONE && engineType != engineType2) {
                    return;
                }
                if (entityPoweredVehicle.canChangeWheels() && !((wheel = (ItemStack)workstationTileEntity.getInventory().get(2)).getItem() instanceof WheelItem)) {
                    return;
                }
            }
            recipe.consumeMaterials((Player)player);
            WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();
            int color = VehicleEntity.DYE_TO_COLOR[0];
            if (vehicle.canBeColored() && (workstationDyeStack = (ItemStack)workstationTileEntity.getInventory().get(0)).getItem() instanceof DyeItem) {
                DyeItem dyeItem = (DyeItem)workstationDyeStack.getItem();
                color = dyeItem.getDyeColor().getTextureDiffuseColor();
                workstationTileEntity.getInventory().set(0, ItemStack.EMPTY);
            }
            ItemStack engineStack = ItemStack.EMPTY;
            if (engineType != EngineType.NONE && (workstationEngineStack = (ItemStack)workstationTileEntity.getInventory().get(1)).getItem() instanceof EngineItem) {
                engineStack = workstationEngineStack.copy();
                workstationTileEntity.getInventory().set(1, ItemStack.EMPTY);
            }
            ItemStack wheelStack = ItemStack.EMPTY;
            if (vehicle instanceof PoweredVehicleEntity && ((PoweredVehicleEntity)vehicle).canChangeWheels() && (workstationWheelStack = (ItemStack)workstationTileEntity.getInventory().get(2)).getItem() instanceof WheelItem) {
                wheelStack = workstationWheelStack.copy();
                workstationTileEntity.getInventory().set(2, ItemStack.EMPTY);
            }
            ItemStack stack = VehicleCrateBlock.create((HolderLookup.Provider)world.registryAccess(), entityId, color, engineStack, wheelStack);
            world.addFreshEntity((Entity)new ItemEntity(world, (double)message.pos.getX() + 0.5, (double)message.pos.getY() + 1.125, (double)message.pos.getZ() + 0.5, stack));
        });
    }
}

