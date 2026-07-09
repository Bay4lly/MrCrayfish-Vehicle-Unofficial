/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
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
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;

public class BumperCarModel
extends AbstractLandVehicleRenderer<BumperCarEntity> {
    public BumperCarModel(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable BumperCarEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.BUMPER_CAR_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.2, 0.0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-45.0f));
        matrixStack.translate(0.0, -0.02, 0.0);
        matrixStack.scale(0.9f, 0.9f, 0.9f);
        if (vehicle != null) {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 25.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        RenderUtil.renderColoredModel(SpecialModels.GO_KART_STEERING_WHEEL.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, (Integer)this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(BumperCarEntity entity, Player player, PlayerModel model, float partialTicks) {
        model.rightLeg.xRot = (float)Math.toRadians(-85.0);
        model.rightLeg.yRot = (float)Math.toRadians(10.0);
        model.leftLeg.xRot = (float)Math.toRadians(-85.0);
        model.leftLeg.yRot = (float)Math.toRadians(-10.0);
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45.0f;
        float turnRotation = wheelAngleNormal * 6.0f;
        model.rightArm.xRot = (float)Math.toRadians(-65.0f - turnRotation);
        model.rightArm.yRot = (float)Math.toRadians(-7.0);
        model.leftArm.xRot = (float)Math.toRadians(-65.0f + turnRotation);
        model.leftArm.yRot = (float)Math.toRadians(7.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.BUMPER_CAR_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.GO_KART_STEERING_WHEEL, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.2f, 0.0f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -45.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.02f, 0.0f), EntityRayTracer.MatrixTransformation.createScale(0.9f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.BUMPER_CAR.get()), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}

