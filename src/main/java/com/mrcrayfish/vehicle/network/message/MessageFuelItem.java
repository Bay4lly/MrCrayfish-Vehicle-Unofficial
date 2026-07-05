package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Optional;

public class MessageFuelItem implements IMessage<MessageFuelItem>
{
    public static final CustomPacketPayload.Type<MessageFuelItem> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fuel_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFuelItem> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageFuelItem().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private InteractionHand hand;

    public MessageFuelItem()
    {
    }

    public MessageFuelItem(InteractionHand hand)
    {
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelItem message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeEnum(message.hand);
    }

    @Override
    public MessageFuelItem decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageFuelItem(buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(MessageFuelItem message, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = ((ServerPlayer) context.player());
            if(player != null)
            {
                Optional<BlockPos> pumpPosOpt = ModDataKeys.GAS_PUMP.getValue(player);
                if(!pumpPosOpt.isPresent()) return;
                BlockPos pumpPos = pumpPosOpt.get();
                BlockEntity tile = player.level().getBlockEntity(pumpPos.below());
                if(!(tile instanceof GasPumpTankTileEntity)) return;

                GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity) tile;
                FluidTank tank = gasPumpTank.getFluidTank();
                FluidStack stack = tank.getFluid();
                if(stack.isEmpty()) return;
                if(!Config.SERVER.validFuels.get().contains(BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString())) return;

                ItemStack held = player.getItemInHand(message.hand);
                Optional<IFluidHandlerItem> opt = FluidUtil.getFluidHandler(held);
                if(!opt.isPresent()) return;
                IFluidHandlerItem handler = opt.get();

                int fillRate = Config.SERVER.jerryCanFillRate.get();
                int canFill = handler.getTankCapacity(0) - handler.getFluidInTank(0).getAmount();
                int simulateDrain = tank.drain(fillRate, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE).getAmount();
                int transfer = Math.min(canFill, Math.min(fillRate, simulateDrain));
                if(transfer <= 0) return;

                FluidStack drained = tank.drain(transfer, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                if(drained.isEmpty()) return;
                handler.fill(drained, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }
}
