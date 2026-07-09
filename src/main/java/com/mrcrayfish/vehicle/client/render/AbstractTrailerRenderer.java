/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTrailerRenderer<T extends TrailerEntity>
extends AbstractVehicleRenderer<T> {
    public AbstractTrailerRenderer(Supplier<VehicleProperties> defaultProperties) {
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
            float pitch = ((TrailerEntity)vehicle).xRotO + (vehicle.getXRot() - ((TrailerEntity)vehicle).xRotO) * partialTicks;
            double hitchPivotZ = ((TrailerEntity)vehicle).getHitchOffset() * 0.0625;
            matrixStack.translate(0.0, 0.0, hitchPivotZ);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-pitch));
            matrixStack.translate(0.0, 0.0, -hitchPivotZ);
        }
        if (((Boolean)this.towTrailerProperty.get(vehicle)).booleanValue()) {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            Vec3 towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.getTowBarModel().getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float)bodyPosition.getScale(), (float)bodyPosition.getScale(), (float)bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, (double)properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, (double)properties.getWheelOffset() * 0.0625, 0.0);
        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
        matrixStack.popPose();
    }

    protected void renderWheel(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, boolean right, float offsetX, float offsetY, float offsetZ, float wheelScale, float partialTicks, int light) {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, offsetY, offsetZ);
        if (right) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        }
        if (vehicle != null) {
            float wheelRotation = ((TrailerEntity)vehicle).prevWheelRotation + (((TrailerEntity)vehicle).wheelRotation - ((TrailerEntity)vehicle).prevWheelRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(right ? wheelRotation : -wheelRotation));
        }
        matrixStack.scale(wheelScale, wheelScale, wheelScale);
        RenderUtil.renderColoredModel(RenderUtil.getModel(new ItemStack((ItemLike)ModItems.STANDARD_WHEEL.get())), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}

