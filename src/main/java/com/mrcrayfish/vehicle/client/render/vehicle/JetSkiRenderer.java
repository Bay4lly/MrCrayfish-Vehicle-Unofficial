/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractBoatRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.JetSkiEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class JetSkiRenderer
extends AbstractBoatRenderer<JetSkiEntity> {
    public JetSkiRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable JetSkiEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.JET_SKI_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.355, 0.225);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-45.0f));
        if (vehicle != null) {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 15.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.ATV_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(JetSkiEntity entity, Player player, PlayerModel model, float partialTicks) {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / (float)entity.getMaxTurnAngle();
        float turnRotation = wheelAngleNormal * 12.0f;
        model.rightArm.xRot = (float)Math.toRadians(-65.0f - turnRotation);
        model.rightArm.yRot = (float)Math.toRadians(15.0);
        model.leftArm.xRot = (float)Math.toRadians(-65.0f + turnRotation);
        model.leftArm.yRot = (float)Math.toRadians(-15.0);
        if (entity.getControllingPassenger() != player) {
            model.rightArm.xRot = (float)Math.toRadians(-55.0);
            model.rightArm.yRot = (float)Math.toRadians(0.0);
            model.leftArm.xRot = (float)Math.toRadians(-55.0);
            model.leftArm.yRot = (float)Math.toRadians(0.0);
        }
        model.rightLeg.xRot = (float)Math.toRadians(-65.0);
        model.rightLeg.yRot = (float)Math.toRadians(30.0);
        model.leftLeg.xRot = (float)Math.toRadians(-65.0);
        model.leftLeg.yRot = (float)Math.toRadians(-30.0);
    }

    @Override
    public void applyPlayerRender(JetSkiEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index != -1) {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).multiply(-1.0, 1.0, 1.0).scale(0.0625);
            double scale = 1.0666666666666667;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y - player.getVehicleAttachmentPoint(entity).y + 0.25) * scale + 1.5;
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / (float)entity.getMaxTurnAngle();
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 15.0f));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-8.0f * Math.min(1.0f, currentSpeedNormal)));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Override
    protected boolean shouldRenderFuelLid() {
        return false;
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.JET_SKI_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.ATV_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.375f, 0.25f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -45.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.02f, 0.0f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.JET_SKI.get()), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}


