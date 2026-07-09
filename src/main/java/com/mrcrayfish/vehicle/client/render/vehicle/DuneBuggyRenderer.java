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
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
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
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.DuneBuggyEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DuneBuggyRenderer
extends AbstractLandVehicleRenderer<DuneBuggyEntity> {
    public DuneBuggyRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable DuneBuggyEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        VehicleProperties properties;
        Wheel wheel;
        this.renderDamagedPart(vehicle, SpecialModels.DUNE_BUGGY_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 0.1953125);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        if (vehicle != null) {
            float wheelAngle = Mth.lerp((float)partialTicks, (float)vehicle.prevWheelAngle, (float)vehicle.wheelAngle);
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 15.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Axis.XP.rotationDegrees(22.5f));
        matrixStack.translate(0.0, 0.0, -0.2);
        this.renderDamagedPart(vehicle, SpecialModels.DUNE_BUGGY_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        ItemStack wheelStack = (ItemStack)this.wheelStackProperty.get(vehicle);
        if (!wheelStack.isEmpty() && (wheel = (properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle)).getFirstFrontWheel()) != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -0.355, 0.33);
            if (vehicle != null) {
                float frontWheelSpin = Mth.lerp((float)partialTicks, (float)vehicle.prevFrontWheelRotation, (float)vehicle.frontWheelRotation);
                if (vehicle.isMoving()) {
                    matrixStack.mulPose(Axis.XP.rotationDegrees(-frontWheelSpin));
                }
            }
            matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            int wheelColor = IDyeable.getColorFromStack(wheelStack);
            RenderUtil.renderColoredModel(RenderUtil.getModel(wheelStack), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(DuneBuggyEntity entity, Player player, PlayerModel model, float partialTicks) {
        float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45.0f;
        float turnRotation = wheelAngleNormal * 8.0f;
        model.rightArm.xRot = (float)Math.toRadians(-50.0f - turnRotation);
        model.leftArm.xRot = (float)Math.toRadians(-50.0f + turnRotation);
        model.rightLeg.xRot = (float)Math.toRadians(-65.0);
        model.rightLeg.yRot = (float)Math.toRadians(30.0);
        model.leftLeg.xRot = (float)Math.toRadians(-65.0);
        model.leftLeg.yRot = (float)Math.toRadians(-30.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (entityRayTracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.DUNE_BUGGY_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.DUNE_BUGGY_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.0f, -0.0046875f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.DUNE_BUGGY.get()), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}

