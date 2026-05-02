package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.function.Supplier;

public class MessageFuelItem implements IMessage<MessageFuelItem>
{
    private InteractionHand hand;

    public MessageFuelItem()
    {
    }

    public MessageFuelItem(InteractionHand hand)
    {
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelItem message, FriendlyByteBuf buffer)
    {
        buffer.writeEnum(message.hand);
    }

    @Override
    public MessageFuelItem decode(FriendlyByteBuf buffer)
    {
        return new MessageFuelItem(buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(MessageFuelItem message, Supplier<Context> supplier)
    {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
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
                if(!Config.SERVER.validFuels.get().contains(ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString())) return;

                ItemStack held = player.getItemInHand(message.hand);
                Optional<IFluidHandlerItem> opt = held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if(!opt.isPresent()) return;
                IFluidHandlerItem handler = opt.get();

                int fillRate = Config.SERVER.jerryCanFillRate.get();
                int canFill = handler.getTankCapacity(0) - handler.getFluidInTank(0).getAmount();
                int simulateDrain = tank.drain(fillRate, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE).getAmount();
                int transfer = Math.min(canFill, Math.min(fillRate, simulateDrain));
                if(transfer <= 0) return;

                FluidStack drained = tank.drain(transfer, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                if(drained.isEmpty()) return;
                handler.fill(drained, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
