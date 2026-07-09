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
import com.mrcrayfish.vehicle.entity.vehicle.ATVEntity;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;

public class ATVRenderer
extends AbstractLandVehicleRenderer<ATVEntity> {
    public ATVRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable ATVEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.ATV_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.3375, 0.25);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-45.0f));
        matrixStack.translate(0.0, -0.025, 0.0);
        if (vehicle != null) {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 15.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        RenderUtil.renderColoredModel(SpecialModels.ATV_HANDLES.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, (Integer)this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(ATVEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks) {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45.0f;
        float turnRotation = wheelAngleNormal * 12.0f;
        model.rightArm.xRot = (float)Math.toRadians(-65.0f - turnRotation);
        model.rightArm.yRot = (float)Math.toRadians(15.0);
        model.leftArm.xRot = (float)Math.toRadians(-65.0f + turnRotation);
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
        model.rightLeg.xRot = (float)Math.toRadians(-65.0);
        model.rightLeg.yRot = (float)Math.toRadians(30.0);
        model.leftLeg.xRot = (float)Math.toRadians(-65.0);
        model.leftLeg.yRot = (float)Math.toRadians(-30.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.ATV_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.ATV_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.3375f, 0.25f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -45.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.025f, 0.0f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.TOW_BAR, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 180.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.5f, 1.05f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ATV.get()), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ATV.get()), parts, transforms);
        };
    }

    @Override
    protected boolean shouldRenderFuelLid() {
        return false;
    }
}

