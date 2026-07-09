/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.MultiBufferSource
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractPoweredRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class AbstractBoatRenderer<T extends BoatEntity>
extends AbstractPoweredRenderer<T> {
    public AbstractBoatRenderer(Supplier<VehicleProperties> defaultProperties) {
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
            float currentSpeedNormal = (((BoatEntity)vehicle).prevCurrentSpeed + (((BoatEntity)vehicle).currentSpeed - ((BoatEntity)vehicle).prevCurrentSpeed) * partialTicks) / ((PoweredVehicleEntity)vehicle).getMaxSpeed();
            float turnAngleNormal = (((BoatEntity)vehicle).prevTurnAngle + (((BoatEntity)vehicle).turnAngle - ((BoatEntity)vehicle).prevTurnAngle) * partialTicks) / (float)((PoweredVehicleEntity)vehicle).getMaxTurnAngle();
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * -15.0f));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-8.0f * Math.min(1.0f, currentSpeedNormal)));
        }
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale((float)bodyPosition.getScale(), (float)bodyPosition.getScale(), (float)bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, (double)properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, (double)properties.getWheelOffset() * 0.0625, 0.0);
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }
}

