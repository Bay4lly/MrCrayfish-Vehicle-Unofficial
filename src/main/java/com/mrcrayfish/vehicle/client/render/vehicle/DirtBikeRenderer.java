/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.DirtBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DirtBikeRenderer
extends AbstractMotorcycleRenderer<DirtBikeEntity> {
    public DirtBikeRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable DirtBikeEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        VehicleProperties properties;
        Wheel wheel;
        this.renderDamagedPart(vehicle, SpecialModels.DIRT_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 0.65625);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        if (vehicle != null) {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 25.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Axis.XP.rotationDegrees(22.5f));
        matrixStack.translate(0.0, 0.0, -0.65625);
        this.renderDamagedPart(vehicle, SpecialModels.DIRT_BIKE_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        ItemStack wheelStack = (ItemStack)this.wheelStackProperty.get(vehicle);
        if (!wheelStack.isEmpty() && (wheel = (Wheel)(properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle)).getWheels().stream().filter(wheel1 -> wheel1.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null)) != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate((double)wheel.getOffsetX() * 0.0625, (double)wheel.getOffsetY() * 0.0625, (double)wheel.getOffsetZ() * 0.0625);
            if (vehicle != null) {
                float frontWheelSpin = Mth.lerp((float)partialTicks, (float)vehicle.prevFrontWheelRotation, (float)vehicle.frontWheelRotation);
                if (vehicle.isMoving()) {
                    matrixStack.mulPose(Axis.XP.rotationDegrees(-frontWheelSpin));
                }
            }
            matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            RenderUtil.renderColoredModel(RenderUtil.getModel(wheelStack), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(DirtBikeEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index == 0) {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 8.0f;
            model.rightArm.xRot = (float)Math.toRadians(-55.0f - turnRotation);
            model.leftArm.xRot = (float)Math.toRadians(-55.0f + turnRotation);
        } else if (index == 1) {
            model.rightArm.xRot = (float)Math.toRadians(-45.0);
            model.rightArm.zRot = (float)Math.toRadians(-10.0);
            model.leftArm.xRot = (float)Math.toRadians(-45.0);
            model.leftArm.zRot = (float)Math.toRadians(10.0);
        }
        model.rightLeg.xRot = (float)Math.toRadians(-45.0);
        model.rightLeg.yRot = (float)Math.toRadians(30.0);
        model.leftLeg.xRot = (float)Math.toRadians(-45.0);
        model.leftLeg.yRot = (float)Math.toRadians(-30.0);
    }

    @Override
    public void applyPlayerRender(DirtBikeEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index != -1) {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 1.0666666666666667;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y - player.getVehicleAttachmentPoint(entity).y + 0.25) * scale + 1.5;
            double offsetZ = -seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45.0f;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20.0f));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.DIRT_BIKE_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.DIRT_BIKE_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.DIRT_BIKE.get()), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}


