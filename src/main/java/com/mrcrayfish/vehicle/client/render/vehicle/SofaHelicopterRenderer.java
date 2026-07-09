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
import com.mrcrayfish.vehicle.client.render.AbstractHelicopterRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SofacopterEntity;
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

public class SofaHelicopterRenderer
extends AbstractHelicopterRenderer<SofacopterEntity> {
    public SofaHelicopterRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable SofacopterEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        matrixStack.pushPose();
        this.renderDamagedPart(vehicle, SpecialModels.RED_SOFA.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.5, 0.0);
        this.renderDamagedPart(vehicle, SpecialModels.SOFA_HELICOPTER_ARM.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, 2.0, 0.0);
        if (vehicle != null) {
            float bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(bladeRotation));
        }
        matrixStack.scale(1.5f, 1.5f, 1.5f);
        this.renderDamagedPart(vehicle, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(SofacopterEntity entity, Player player, PlayerModel model, float partialTicks) {
        model.rightArm.xRot = (float)Math.toRadians(-55.0);
        model.rightArm.yRot = (float)Math.toRadians(25.0);
        model.leftArm.xRot = (float)Math.toRadians(-55.0);
        model.leftArm.yRot = (float)Math.toRadians(-25.0);
        model.rightLeg.xRot = (float)Math.toRadians(-90.0);
        model.rightLeg.yRot = (float)Math.toRadians(15.0);
        model.leftLeg.xRot = (float)Math.toRadians(-90.0);
        model.leftLeg.yRot = (float)Math.toRadians(-15.0);
    }

    @Override
    public void applyPlayerRender(SofacopterEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index != -1) {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).multiply(-1.0, 1.0, 1.0).scale(0.0625);
            double scale = 1.0666666666666667;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y - player.getVehicleAttachmentPoint(entity).y + 0.25 + 0.3) * scale + 1.5;
            double offsetZ = seatVec.z * scale;
            float entityYaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks)));
            matrixStack.mulPose(Axis.XP.rotationDegrees(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
            matrixStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.RED_SOFA, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 90.0f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SOFA_HELICOPTER_ARM, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SOFACOPTER.get()), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SOFACOPTER.get()), parts, transforms);
        };
    }
}


