package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractHelicopterRenderer<T extends HelicopterEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractPoweredRenderer<T>
{
    public AbstractHelicopterRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0, 0.5, 0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0, properties.getAxleOffset() * 0.0625F, 0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0, properties.getWheelOffset() * 0.0625F, 0);

        //Render body
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPreRotations(T entity, PoseStack matrixStack, float partialTicks)
    {
        matrixStack.mulPose(Axis.ZP.rotationDegrees(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks));
        matrixStack.mulPose(Axis.XP.rotationDegrees(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
    }
}
