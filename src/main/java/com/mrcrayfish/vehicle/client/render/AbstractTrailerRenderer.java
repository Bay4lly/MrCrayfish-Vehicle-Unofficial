package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractTrailerRenderer<T extends TrailerEntity> extends AbstractVehicleRenderer<T>
{
    public AbstractTrailerRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void setupTransformsAndRender(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        var bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        // Rotate trailers around their hitch point instead of center so they stay visually attached.
        if(vehicle != null)
        {
            float pitch = vehicle.xRotO + (vehicle.getXRot() - vehicle.xRotO) * partialTicks;
            double hitchPivotZ = vehicle.getHitchOffset() * 0.0625;
            matrixStack.translate(0.0, 0.0, hitchPivotZ);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-pitch));
            matrixStack.translate(0.0, 0.0, -hitchPivotZ);
        }

        if(this.towTrailerProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
            Vec3 towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.getTowBarModel().getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        matrixStack.popPose();
    }

    //TODO Eventually converted to the wheel system. Consider it a pulled vehicle rather than powered
    protected void renderWheel(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks, int light)
    {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, offsetY, offsetZ);
        if(right)
        {
            matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
        }
        if(vehicle != null)
        {
            float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(right ? wheelRotation : -wheelRotation));
        }
        matrixStack.scale(wheelScale, wheelScale, wheelScale);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack(ModItems.STANDARD_WHEEL.get())), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
