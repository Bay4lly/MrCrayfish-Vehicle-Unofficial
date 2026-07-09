/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.handler.FuelingHandler;
import com.mrcrayfish.vehicle.client.handler.SprayCanHandler;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import java.util.Optional;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PlayerModelHandler {
    public static <T extends Entity> void onPreRender(Player player, PoseStack matrixStack, float partialTicks) {
        Entity ridingEntity = player.getVehicle();
        if (ridingEntity instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)ridingEntity;
            PlayerModelHandler.applyPassengerTransformations(vehicle, player, matrixStack, partialTicks);
            PlayerModelHandler.applyWheelieTransformations(vehicle, player, matrixStack, partialTicks);
        }
    }

    public static void applyPassengerTransformations(VehicleEntity vehicle, Player player, PoseStack matrixStack, float partialTicks) {
        AbstractVehicleRenderer render = VehicleRenderRegistry.getRenderer(vehicle.getType());
        if (render != null) {
            render.applyPlayerRender(vehicle, player, partialTicks, matrixStack);
        }
    }

    public static void applyWheelieTransformations(VehicleEntity vehicle, Player player, PoseStack matrixStack, float partialTicks) {
        if (!(vehicle instanceof LandVehicleEntity)) {
            return;
        }
        LandVehicleEntity landVehicle = (LandVehicleEntity)vehicle;
        if (!landVehicle.canWheelie()) {
            return;
        }
        int seatIndex = vehicle.getSeatTracker().getSeatIndex(player.getUUID());
        if (seatIndex == -1) {
            return;
        }
        VehicleProperties properties = landVehicle.getProperties();
        if (properties.getRearAxelVec() == null) {
            return;
        }
        Seat seat = properties.getSeats().get(seatIndex);
        Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
        double vehicleScale = properties.getBodyPosition().getScale();
        double playerScale = 1.0666666666666667;
        double offsetX = -(seatVec.x * playerScale);
        double offsetY = (seatVec.y - player.getVehicleAttachmentPoint((Entity)vehicle).y + 0.25) * playerScale + 1.5 - (double)properties.getWheelOffset() * 0.0625 * vehicleScale;
        double offsetZ = seatVec.z * playerScale - properties.getRearAxelVec().z * 0.0625 * vehicleScale;
        matrixStack.translate(offsetX, offsetY, offsetZ);
        float wheelieProgress = Mth.lerp((float)partialTicks, (float)landVehicle.prevWheelieCount, (float)landVehicle.wheelieCount) / 4.0f;
        wheelieProgress = (float)(1.0 - Math.pow(1.0 - (double)wheelieProgress, 2.0));
        matrixStack.mulPose(Axis.XP.rotationDegrees(-30.0f * wheelieProgress));
        matrixStack.translate(-offsetX, -offsetY, -offsetZ);
    }

    public static void onSetupAngles(Player player, PlayerModel<Player> model, float partialTick) {
        if (player.equals((Object)Minecraft.getInstance().player) && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            return;
        }
        if (((Optional<?>)ModDataKeys.GAS_PUMP.getValue(player)).isPresent()) {
            FuelingHandler.applyFuelingPose(player, model);
            return;
        }
        SprayCanHandler.applySprayCanPose(player, model);
        PlayerModelHandler.applyPassengerPose(player, model, partialTick);
    }

    private static void applyPassengerPose(Player player, PlayerModel model, float partialTicks) {
        Entity ridingEntity = player.getVehicle();
        if (!(ridingEntity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)ridingEntity;
        AbstractVehicleRenderer render = VehicleRenderRegistry.getRenderer(vehicle.getType());
        if (render != null) {
            render.applyPlayerModel(vehicle, player, (PlayerModel<AbstractClientPlayer>)model, partialTicks);
        }
    }
}

