/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractPoweredRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import java.util.function.Supplier;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class AbstractHelicopterRenderer<T extends HelicopterEntity>
extends AbstractPoweredRenderer<T> {
    public AbstractHelicopterRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        matrixStack.pushPose();
        VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float)bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float)bodyPosition.getRotZ()));
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float)bodyPosition.getScale(), (float)bodyPosition.getScale(), (float)bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0f, properties.getAxleOffset() * 0.0625f, 0.0f);
        matrixStack.translate(0.0f, properties.getWheelOffset() * 0.0625f, 0.0f);
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPreRotations(T entity, PoseStack matrixStack, float partialTicks) {
        matrixStack.mulPose(Axis.ZP.rotationDegrees(((HelicopterEntity)entity).prevBodyRotationX + (((HelicopterEntity)entity).bodyRotationX - ((HelicopterEntity)entity).prevBodyRotationX) * partialTicks));
        matrixStack.mulPose(Axis.XP.rotationDegrees(((HelicopterEntity)entity).prevBodyRotationZ + (((HelicopterEntity)entity).bodyRotationZ - ((HelicopterEntity)entity).prevBodyRotationZ) * partialTicks));
    }
}

