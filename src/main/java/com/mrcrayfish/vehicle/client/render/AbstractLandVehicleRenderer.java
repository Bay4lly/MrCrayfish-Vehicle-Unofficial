/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.render.AbstractPoweredRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractLandVehicleRenderer<T extends LandVehicleEntity>
extends AbstractPoweredRenderer<T> {
    protected final AbstractVehicleRenderer.PropertyFunction<T, Float> wheelAngleProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.wheelAngle), Float.valueOf(0.0f));
    protected final AbstractVehicleRenderer.PropertyFunction<T, Float> prevWheelAngleProperty = new AbstractVehicleRenderer.PropertyFunction<T, Float>(t -> Float.valueOf(t.prevWheelAngle), Float.valueOf(0.0f));

    public AbstractLandVehicleRenderer(Supplier<VehicleProperties> defaultProperties) {
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
            float additionalYaw = ((LandVehicleEntity)vehicle).prevAdditionalYaw + (((LandVehicleEntity)vehicle).additionalYaw - ((LandVehicleEntity)vehicle).prevAdditionalYaw) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(additionalYaw));
        }
        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        if (((Boolean)this.towTrailerProperty.get(vehicle)).booleanValue()) {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            Vec3 towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(this.getTowBarModel().getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
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
            float wheelieProgress = Mth.lerp((float)partialTicks, (float)((LandVehicleEntity)vehicle).prevWheelieCount, (float)((LandVehicleEntity)vehicle).wheelieCount) / 4.0f;
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

    protected void renderSteeringDebug(PoseStack matrixStack, VehicleProperties properties, @Nullable T vehicle) {
        if (vehicle == null) {
            return;
        }
        if (((Boolean)Config.CLIENT.renderSteeringDebug.get()).booleanValue() && properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.0, -0.5, 0.0);
            matrixStack.translate(0.0, (double)(-properties.getAxleOffset()) * 0.0625, 0.0);
            matrixStack.translate(0.0, (double)(-properties.getWheelOffset()) * 0.0625, 0.0);
            matrixStack.pushPose();
            Vec3 frontAxelVec = properties.getFrontAxelVec();
            frontAxelVec = frontAxelVec.scale(0.0625);
            matrixStack.translate(frontAxelVec.x, 0.0, frontAxelVec.z);
            this.renderSteeringLine(matrixStack, 0xFFFFFF);
            matrixStack.popPose();
            matrixStack.pushPose();
            frontAxelVec = properties.getFrontAxelVec();
            frontAxelVec = frontAxelVec.scale(0.0625);
            Vec3 nextFrontAxelVec = new Vec3(0.0, 0.0, (double)(((PoweredVehicleEntity)vehicle).getSpeed() / 20.0f)).yRot(((LandVehicleEntity)vehicle).renderWheelAngle * ((float)Math.PI / 180));
            frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
            matrixStack.translate(frontAxelVec.x, 0.0, frontAxelVec.z);
            this.renderSteeringLine(matrixStack, 0xFFDD00);
            matrixStack.popPose();
            matrixStack.pushPose();
            Vec3 rearAxelVec = properties.getRearAxelVec();
            rearAxelVec = rearAxelVec.scale(0.0625);
            matrixStack.translate(rearAxelVec.x, 0.0, rearAxelVec.z);
            this.renderSteeringLine(matrixStack, 0xFFFFFF);
            matrixStack.popPose();
            matrixStack.pushPose();
            frontAxelVec = properties.getFrontAxelVec();
            frontAxelVec = frontAxelVec.scale(0.0625);
            nextFrontAxelVec = new Vec3(0.0, 0.0, (double)(((PoweredVehicleEntity)vehicle).getSpeed() / 20.0f)).yRot(((LandVehicleEntity)vehicle).renderWheelAngle * ((float)Math.PI / 180));
            frontAxelVec = frontAxelVec.add(nextFrontAxelVec);
            Vec3 rearAxelVec2 = properties.getRearAxelVec();
            rearAxelVec2 = rearAxelVec2.scale(0.0625);
            double deltaYaw = Math.toDegrees(Math.atan2(rearAxelVec2.z - frontAxelVec.z, rearAxelVec2.x - frontAxelVec.x)) + 90.0;
            if (((LandVehicleEntity)vehicle).isRearWheelSteering()) {
                deltaYaw += 180.0;
            }
            rearAxelVec2 = rearAxelVec2.add(Vec3.directionFromRotation((float)0.0f, (float)((float)deltaYaw)).scale((double)(((PoweredVehicleEntity)vehicle).getSpeed() / 20.0f)));
            matrixStack.translate(rearAxelVec2.x, 0.0, rearAxelVec2.z);
            this.renderSteeringLine(matrixStack, 0xFFDD00);
            matrixStack.popPose();
            matrixStack.pushPose();
            Vec3 nextFrontAxelVec2 = new Vec3(0.0, 0.0, (double)(((PoweredVehicleEntity)vehicle).getSpeed() / 20.0f)).yRot(((LandVehicleEntity)vehicle).wheelAngle * ((float)Math.PI / 180));
            nextFrontAxelVec2 = nextFrontAxelVec2.add(properties.getFrontAxelVec().scale(0.0625));
            Vec3 nextRearAxelVec = new Vec3(0.0, 0.0, (double)(((PoweredVehicleEntity)vehicle).getSpeed() / 20.0f));
            nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625));
            Vec3 nextVehicleVec = nextFrontAxelVec2.add(nextRearAxelVec).scale(0.5);
            nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(0.5));
            matrixStack.pushPose();
            this.renderSteeringLine(matrixStack, 0xFFFFFF);
            matrixStack.popPose();
            matrixStack.pushPose();
            matrixStack.translate(nextVehicleVec.x, 0.0, nextVehicleVec.z);
            this.renderSteeringLine(matrixStack, 0xFFDD00);
            matrixStack.popPose();
            matrixStack.popPose();
            matrixStack.popPose();
        }
    }

    private void renderSteeringLine(PoseStack stack, int color) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        RenderSystem.lineWidth((float)Math.max(2.0f, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0f * 2.0f));
        RenderSystem.enableDepthTest();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(stack.last().pose(), 0.0f, 0.0f, 0.0f).setColor(red, green, blue, 1.0f);
        buffer.addVertex(stack.last().pose(), 0.0f, 2.0f, 0.0f).setColor(red, green, blue, 1.0f);
        BufferUploader.drawWithShader((MeshData)buffer.buildOrThrow());
        RenderSystem.disableDepthTest();
    }

    protected void renderWheel(@Nullable T vehicle, Wheel wheel, ItemStack stack, BakedModel model, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light) {
        if (!wheel.shouldRender()) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate((double)wheel.getOffsetX() * 0.0625 * (double)wheel.getSide().getOffset(), (double)wheel.getOffsetY() * 0.0625, (double)wheel.getOffsetZ() * 0.0625);
        if (wheel.getPosition() == Wheel.Position.FRONT) {
            float wheelAngle = Mth.lerp((float)partialTicks, (float)this.prevWheelAngleProperty.get(vehicle).floatValue(), (float)this.wheelAngleProperty.get(vehicle).floatValue());
            matrixStack.mulPose(Axis.YP.rotationDegrees(wheelAngle));
        }
        if (vehicle != null && ((PoweredVehicleEntity)vehicle).isMoving()) {
            matrixStack.mulPose(Axis.XP.rotationDegrees(-wheel.getWheelRotation((LandVehicleEntity)vehicle, partialTicks)));
        }
        matrixStack.translate((double)(wheel.getWidth() * wheel.getScaleX() / 2.0f) * 0.0625 * (double)wheel.getSide().getOffset(), 0.0, 0.0);
        matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
        if (wheel.getSide() == Wheel.Side.RIGHT) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        }
        int wheelColor = IDyeable.getColorFromStack(stack);
        RenderUtil.renderColoredModel(model, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}

