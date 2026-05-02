package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractPlaneRenderer<T extends PlaneEntity & EntityRayTracer.IEntityRayTraceable> extends AbstractPoweredRenderer<T>
{
    private final PropertyFunction<T, Float> bodyRotationXProperty = new PropertyFunction<>(t -> t.bodyRotationX, 0F);
    private final PropertyFunction<T, Float> prevBodyRotationXProperty = new PropertyFunction<>(t -> t.prevBodyRotationX, 0F);
    private final PropertyFunction<T, Float> bodyRotationZProperty = new PropertyFunction<>(t -> t.bodyRotationZ, 0F);
    private final PropertyFunction<T, Float> prevBodyRotationZProperty = new PropertyFunction<>(t -> t.prevBodyRotationZ, 0F);

    public AbstractPlaneRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    public void setupTransformsAndRender(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        matrixStack.translate(0.0, 0.5, 0.0);

        float bodyPitch = this.prevBodyRotationXProperty.get(vehicle) + (this.bodyRotationXProperty.get(vehicle) - this.prevBodyRotationXProperty.get(vehicle)) * partialTicks;
        matrixStack.mulPose(Axis.XP.rotationDegrees(-bodyPitch));

        float bodyRoll = this.prevBodyRotationZProperty.get(vehicle) + (this.bodyRotationZProperty.get(vehicle) - this.prevBodyRotationZProperty.get(vehicle)) * partialTicks;
        matrixStack.mulPose(Axis.ZP.rotationDegrees(-bodyRoll));

        matrixStack.translate(0.0, -0.5, 0.0);

        //Translate the body
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());

        //Apply vehicle scale
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);

        //Translate the vehicle so it's axles are half way into the ground
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);

        //Translate the vehicle so it's actually riding on it's wheels
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        //Render body
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        this.renderEngine(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderFuelPort(vehicle, matrixStack, renderTypeBuffer, light);
        this.renderKeyPort(vehicle, matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }
}
