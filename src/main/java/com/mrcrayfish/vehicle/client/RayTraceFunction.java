/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidUtil
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.handler.ControllerHandler;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFuelItem;
import com.mrcrayfish.vehicle.network.message.MessageFuelVehicle;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface RayTraceFunction {
    public static final RayTraceFunction FUNCTION_FUELING = (rayTracer, result, player) -> {
        Optional optional;
        ItemStack stack;
        Entity entity = result.getEntity();
        if (!(entity instanceof PoweredVehicleEntity)) {
            return null;
        }
        PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity)entity;
        if (!poweredVehicle.requiresFuel() || poweredVehicle.getCurrentFuel() >= poweredVehicle.getFuelCapacity()) {
            return null;
        }
        Optional<BlockPos> gasPumpOpt = (Optional<BlockPos>)ModDataKeys.GAS_PUMP.getValue(player);
        if (gasPumpOpt.isPresent() && ControllerHandler.isRightClicking()) {
            GasPumpTankTileEntity gasPumpTank;
            FluidTank tank;
            BlockPos pos = gasPumpOpt.get();
            BlockEntity tileEntity = player.level().getBlockEntity(pos);
            BlockEntity belowEntity = player.level().getBlockEntity(pos.below());
            if (tileEntity instanceof GasPumpTileEntity && belowEntity instanceof GasPumpTankTileEntity) {
                GasPumpTankTileEntity gpt = (GasPumpTankTileEntity)belowEntity;
                FluidStack fStack = gpt.getFluidTank().getFluid();
                if (!fStack.isEmpty() && ((List<String>)Config.SERVER.validFuels.get()).contains(BuiltInRegistries.FLUID.getKey(fStack.getFluid()).toString())) {
                    if (rayTracer.getContinuousInteractionTickCounter() % 2 == 0) {
                        PacketHandler.sendToServer(new MessageFuelVehicle(result.getEntity().getId(), InteractionHand.MAIN_HAND));
                    }
                    return InteractionHand.MAIN_HAND;
                }
            }
        }
        for (InteractionHand hand : InteractionHand.values()) {
            IFluidHandlerItem handler;
            FluidStack fluidStack;
            stack = player.getItemInHand(hand);
            if (stack.isEmpty() || !(stack.getItem() instanceof JerryCanItem) || !ControllerHandler.isRightClicking() || !(optional = FluidUtil.getFluidHandler((ItemStack)stack)).isPresent() || (fluidStack = (handler = (IFluidHandlerItem)optional.get()).getFluidInTank(0)).isEmpty() || !((List<String>)Config.SERVER.validFuels.get()).contains(BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString())) continue;
            if (rayTracer.getContinuousInteractionTickCounter() % 2 == 0) {
                PacketHandler.sendToServer(new MessageFuelVehicle(entity.getId(), hand));
            }
            return hand;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            stack = player.getItemInHand(hand);
            if (stack.isEmpty() || stack.getItem() instanceof JerryCanItem || !(optional = FluidUtil.getFluidHandler((ItemStack)stack)).isPresent()) continue;
            if (rayTracer.getContinuousInteractionTickCounter() % 2 == 0) {
                PacketHandler.sendToServer(new MessageFuelItem(hand));
            }
            return hand;
        }
        return null;
    };

    public InteractionHand apply(EntityRayTracer var1, EntityRayTracer.RayTraceResultRotated var2, Player var3);
}

