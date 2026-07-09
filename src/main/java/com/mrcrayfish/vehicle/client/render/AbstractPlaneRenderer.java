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
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class AbstractPlaneRenderer<T extends PlaneEntity>
extends AbstractPoweredRenderer<T> {
    private final AbstractVehicleRenderer.PropertyFunction<T, Float> bodyRotationXProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.bodyRotationX), Float.valueOf(0.0f));
    private final AbstractVehicleRenderer.PropertyFunction<T, Float> prevBodyRotationXProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.prevBodyRotationX), Float.valueOf(0.0f));
    private final AbstractVehicleRenderer.PropertyFunction<T, Float> bodyRotationZProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.bodyRotationZ), Float.valueOf(0.0f));
    private final AbstractVehicleRenderer.PropertyFunction<T, Float> prevBodyRotationZProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.prevBodyRotationZ), Float.valueOf(0.0f));

    public AbstractPlaneRenderer(Supplier<VehicleProperties> defaultProperties) {
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
        matrixStack.translate(0.0, 0.5, 0.0);
        float bodyPitch = this.prevBodyRotationXProperty.get(vehicle).floatValue() + (this.bodyRotationXProperty.get(vehicle).floatValue() - this.prevBodyRotationXProperty.get(vehicle).floatValue()) * partialTicks;
        matrixStack.mulPose(Axis.XP.rotationDegrees(-bodyPitch));
        float bodyRoll = this.prevBodyRotationZProperty.get(vehicle).floatValue() + (this.bodyRotationZProperty.get(vehicle).floatValue() - this.prevBodyRotationZProperty.get(vehicle).floatValue()) * partialTicks;
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-bodyRoll));
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
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

