/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractMotorcycleRenderer<T extends MotorcycleEntity>
extends AbstractLandVehicleRenderer<T> {
    public AbstractMotorcycleRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        matrixStack.pushPose();
        VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float)bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float)bodyPosition.getRotZ()));
        if (vehicle != null) {
            float additionalYaw = ((MotorcycleEntity)vehicle).prevAdditionalYaw + (((MotorcycleEntity)vehicle).additionalYaw - ((MotorcycleEntity)vehicle).prevAdditionalYaw) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(additionalYaw));
            float currentSpeedNormal = (((MotorcycleEntity)vehicle).prevCurrentSpeed + (((MotorcycleEntity)vehicle).currentSpeed - ((MotorcycleEntity)vehicle).prevCurrentSpeed) * partialTicks) / ((PoweredVehicleEntity)vehicle).getMaxSpeed();
            float turnAngleNormal = (((MotorcycleEntity)vehicle).prevTurnAngle + (((MotorcycleEntity)vehicle).turnAngle - ((MotorcycleEntity)vehicle).prevTurnAngle) * partialTicks) / 45.0f;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * -20.0f));
        }
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float)bodyPosition.getScale(), (float)bodyPosition.getScale(), (float)bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, (double)properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, (double)properties.getWheelOffset() * 0.0625, 0.0);
        if (vehicle != null && ((LandVehicleEntity)vehicle).canWheelie()) {
            if (properties.getRearAxelVec() == null) {
                return;
            }
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, (double)(-properties.getAxleOffset()) * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.0, properties.getRearAxelVec().z * 0.0625);
            float wheelieProgress = Mth.lerp((float)partialTicks, (float)((MotorcycleEntity)vehicle).prevWheelieCount, (float)((MotorcycleEntity)vehicle).wheelieCount) / 4.0f;
            wheelieProgress = (float)(1.0 - Math.pow(1.0 - (double)wheelieProgress, 2.0));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-30.0f * wheelieProgress));
            matrixStack.translate(0.0, 0.0, -properties.getRearAxelVec().z * 0.0625);
            matrixStack.translate(0.0, (double)properties.getAxleOffset() * 0.0625, 0.0);
            matrixStack.translate(0.0, 0.5, 0.0);
        }
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        ItemStack wheelStack = (ItemStack)this.wheelStackProperty.get(vehicle);
        if (!wheelStack.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, (double)(-properties.getAxleOffset() * 0.0625f), 0.0);
            BakedModel wheelModel = RenderUtil.getModel(wheelStack);
            properties.getWheels().forEach(wheel -> this.renderWheel(vehicle, (Wheel)wheel, wheelStack, wheelModel, partialTicks, matrixStack, renderTypeBuffer, light));
            matrixStack.popPose();
        }
        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderSteeringDebug(matrixStack, properties, vehicle);
        matrixStack.popPose();
    }
}

