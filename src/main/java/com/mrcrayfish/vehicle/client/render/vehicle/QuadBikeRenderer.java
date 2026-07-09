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
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.QuadBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;

public class QuadBikeRenderer
extends AbstractLandVehicleRenderer<QuadBikeEntity> {
    public QuadBikeRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable QuadBikeEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.QUAD_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.375, 0.1875);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-35.0f));
        if (vehicle != null) {
            float wheelAngle = Mth.lerp((float)partialTicks, (float)vehicle.prevWheelAngle, (float)vehicle.wheelAngle);
            float maxSteeringAngle = vehicle.getProperties().getRearAxelVec() != null ? (float)vehicle.getMaxTurnAngle() : 45.0f;
            float turnRotation = wheelAngle / maxSteeringAngle * 15.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        RenderUtil.renderColoredModel(SpecialModels.QUAD_BIKE_HANDLES.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, (Integer)this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(QuadBikeEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks) {
        float wheelAngle = Mth.lerp((float)partialTicks, (float)((Float)this.prevWheelAngleProperty.get(entity)).floatValue(), (float)((Float)this.wheelAngleProperty.get(entity)).floatValue());
        float maxSteeringAngle = entity.getProperties().getRearAxelVec() != null ? (float)entity.getMaxTurnAngle() : 45.0f;
        float steeringWheelRotation = wheelAngle / maxSteeringAngle * 15.0f / 2.0f;
        model.rightArm.xRot = (float)Math.toRadians(-65.0f - steeringWheelRotation);
        model.rightArm.yRot = (float)Math.toRadians(15.0);
        model.leftArm.xRot = (float)Math.toRadians(-65.0f + steeringWheelRotation);
        model.leftArm.yRot = (float)Math.toRadians(-15.0);
        if (entity.getControllingPassenger() != player) {
            model.rightArm.xRot = (float)Math.toRadians(-20.0);
            model.rightArm.yRot = (float)Math.toRadians(0.0);
            model.rightArm.zRot = (float)Math.toRadians(15.0);
            model.leftArm.xRot = (float)Math.toRadians(-20.0);
            model.leftArm.yRot = (float)Math.toRadians(0.0);
            model.leftArm.zRot = (float)Math.toRadians(-15.0);
            model.rightLeg.xRot = (float)Math.toRadians(-85.0);
            model.rightLeg.yRot = (float)Math.toRadians(30.0);
            model.leftLeg.xRot = (float)Math.toRadians(-85.0);
            model.leftLeg.yRot = (float)Math.toRadians(-30.0);
            return;
        }
        model.rightLeg.xRot = (float)Math.toRadians(-45.0);
        model.rightLeg.yRot = (float)Math.toRadians(40.0);
        model.leftLeg.xRot = (float)Math.toRadians(-45.0);
        model.leftLeg.yRot = (float)Math.toRadians(-40.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.QUAD_BIKE_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.QUAD_BIKE_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.375f, 0.1875f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -35.0f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.TOW_BAR, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 180.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.5f, 1.05f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.QUAD_BIKE.get()), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.QUAD_BIKE.get()), parts, transforms);
        };
    }

    @Override
    protected boolean shouldRenderFuelLid() {
        return false;
    }
}

