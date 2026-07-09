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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractPlaneRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public class SportsPlaneRenderer
extends AbstractPlaneRenderer<SportsPlaneEntity> {
    public SportsPlaneRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable SportsPlaneEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.1875, 0.5);
        matrixStack.translate(0.5, 0.0, 0.0);
        matrixStack.translate(0.375, 0.0, 0.0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-5.0f));
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.1875, 0.5);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        matrixStack.translate(0.5, 0.0625, 0.0);
        matrixStack.translate(0.375, 0.0, 0.0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(5.0f));
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale(0.85f, 0.85f, 0.85f);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, 0.0f, -0.1875f, 1.5f, 0.0f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, 0.46875f, -0.1875f, 0.125f, 100.0f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, -0.46875f, -0.1875f, 0.125f, -100.0f, partialTicks, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.09375, 1.3875);
        if (vehicle != null) {
            float propellerRotation = Mth.lerp((float)partialTicks, (float)vehicle.prevPropellerRotation, (float)vehicle.propellerRotation);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(propellerRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_PROPELLER.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    private void renderWheel(@Nullable SportsPlaneEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks, int light) {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, offsetY, offsetZ);
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WHEEL_COVER.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0f, -0.140625f, 0.0f);
        matrixStack.pushPose();
        if (vehicle != null && vehicle.isMoving()) {
            float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(-wheelRotation));
        }
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack((ItemLike)ModItems.STANDARD_WHEEL.get())), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        matrixStack.popPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(legRotation));
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_LEG.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(SportsPlaneEntity entity, Player player, PlayerModel model, float partialTicks) {
        model.rightLeg.xRot = (float)Math.toRadians(-85.0);
        model.rightLeg.yRot = (float)Math.toRadians(10.0);
        model.leftLeg.xRot = (float)Math.toRadians(-85.0);
        model.leftLeg.yRot = (float)Math.toRadians(-10.0);
    }

    @Override
    public void applyPlayerRender(SportsPlaneEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index != -1) {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 1.0666666666666667;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y - player.getVehicleAttachmentPoint(entity).y + 0.25 - 0.5) * scale + 1.5;
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
            float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(bodyRoll));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-bodyPitch));
            matrixStack.translate(-offsetX, -offsetY, -offsetX);
        }
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SPORTS_PLANE.get()), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SPORTS_PLANE.get()), parts, transforms);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_WING, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.1875f, 0.5f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, 180.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.875f, 0.0625f, 0.0f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, 5.0f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_WING, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.875f, -0.1875f, 0.5f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -5.0f));
            transforms.add(EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.5f, 0.0f));
            transforms.add(EntityRayTracer.MatrixTransformation.createScale(0.85f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_WHEEL_COVER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.1875f, 1.5f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_LEG, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.1875f, 1.5f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_WHEEL_COVER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(-0.46875f, -0.1875f, 0.125f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_LEG, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(-0.46875f, -0.1875f, 0.125f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, -100.0f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_WHEEL_COVER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.46875f, -0.1875f, 0.125f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SPORTS_PLANE_LEG, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.46875f, -0.1875f, 0.125f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 100.0f));
        };
    }
}


