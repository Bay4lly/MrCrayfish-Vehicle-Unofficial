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
import com.mrcrayfish.vehicle.entity.vehicle.CompactHelicopterEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CompactHelicopterRenderer
extends AbstractHelicopterRenderer<CompactHelicopterEntity> {
    public CompactHelicopterRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
        this.setRenderEngine(false);
    }

    @Override
    protected void render(@Nullable CompactHelicopterEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        float bladeRotation;
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(-0.46875, 0.25, 0.84375);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_JOYSTICK.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, 2.3125, -0.5625);
        if (vehicle != null) {
            bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(bladeRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_BLADES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.1875, 1.53125, -5.09375);
        if (vehicle != null) {
            bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(bladeRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_TAIL_ROTOR.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(CompactHelicopterEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks) {
        model.rightArm.xRot = (float)Math.toRadians(-20.0);
        model.rightArm.yRot = (float)Math.toRadians(0.0);
        model.rightArm.zRot = (float)Math.toRadians(0.0);
        model.leftArm.xRot = (float)Math.toRadians(-20.0);
        model.leftArm.yRot = (float)Math.toRadians(0.0);
        model.leftArm.zRot = (float)Math.toRadians(0.0);
        model.rightArm.y = 2.0f;
        model.leftArm.y = 2.0f;
        model.rightArm.z = 0.0f;
        model.leftArm.z = 0.0f;
        model.rightLeg.xRot = (float)Math.toRadians(-90.0);
        model.rightLeg.yRot = (float)Math.toRadians(15.0);
        model.leftLeg.xRot = (float)Math.toRadians(-90.0);
        model.leftLeg.yRot = (float)Math.toRadians(-15.0);
    }

    @Override
    public void applyPlayerRender(CompactHelicopterEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
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
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.HELICOPTER_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.COMPACT_HELICOPTER.get()), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            transforms.add(EntityRayTracer.MatrixTransformation.createTranslation(-0.46875f, 0.25f, 0.84375f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.HELICOPTER_JOYSTICK, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
        };
    }
}


