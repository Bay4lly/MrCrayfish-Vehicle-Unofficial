/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidUtil
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.network.message.IMessage;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageFuelItem
implements IMessage<MessageFuelItem> {
    public static final CustomPacketPayload.Type<MessageFuelItem> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"fuel_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFuelItem> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode((MessageFuelItem)msg, (RegistryFriendlyByteBuf)buf), buf -> new MessageFuelItem().decode((RegistryFriendlyByteBuf)buf));
    private InteractionHand hand;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MessageFuelItem() {
    }

    public MessageFuelItem(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelItem message, RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum((Enum)message.hand);
    }

    @Override
    public MessageFuelItem decode(RegistryFriendlyByteBuf buffer) {
        return new MessageFuelItem((InteractionHand)buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(MessageFuelItem message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer)context.player();
            if (player != null) {
                int simulateDrain;
                Optional pumpPosOpt = (Optional)ModDataKeys.GAS_PUMP.getValue(player);
                if (!pumpPosOpt.isPresent()) {
                    return;
                }
                BlockPos pumpPos = (BlockPos)pumpPosOpt.get();
                BlockEntity tile = player.level().getBlockEntity(pumpPos.below());
                if (!(tile instanceof GasPumpTankTileEntity)) {
                    return;
                }
                GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity)tile;
                FluidTank tank = gasPumpTank.getFluidTank();
                FluidStack stack = tank.getFluid();
                if (stack.isEmpty()) {
                    return;
                }
                if (!((List)Config.SERVER.validFuels.get()).contains(BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString())) {
                    return;
                }
                ItemStack held = player.getItemInHand(message.hand);
                Optional opt = FluidUtil.getFluidHandler((ItemStack)held);
                if (!opt.isPresent()) {
                    return;
                }
                IFluidHandlerItem handler = (IFluidHandlerItem)opt.get();
                int fillRate = (Integer)Config.SERVER.jerryCanFillRate.get();
                int canFill = handler.getTankCapacity(0) - handler.getFluidInTank(0).getAmount();
                int transfer = Math.min(canFill, Math.min(fillRate, simulateDrain = tank.drain(fillRate, IFluidHandler.FluidAction.SIMULATE).getAmount()));
                if (transfer <= 0) {
                    return;
                }
                FluidStack drained = tank.drain(transfer, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty()) {
                    return;
                }
                handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }
}

