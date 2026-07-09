/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EntityVehicleRenderer<T extends VehicleEntity>
extends EntityRenderer<T> {
    private final AbstractVehicleRenderer<T> wrapper;

    public EntityVehicleRenderer(EntityRendererProvider.Context renderManager, AbstractVehicleRenderer<T> wrapper) {
        super(renderManager);
        this.wrapper = wrapper;
    }

    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }

    public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light) {
        if (!entity.isAlive()) {
            return;
        }
        if (entity.getVehicle() instanceof EntityJack) {
            return;
        }
        matrixStack.pushPose();
        this.wrapper.applyPreRotations(entity, matrixStack, partialTicks);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        this.setupBreakAnimation((VehicleEntity)entity, matrixStack, partialTicks);
        this.wrapper.setupTransformsAndRender(entity, matrixStack, renderTypeBuffer, partialTicks, light);
        matrixStack.popPose();
        EntityRayTracer.instance().renderRayTraceElements(entity, matrixStack, renderTypeBuffer, entityYaw);
    }

    private void setupBreakAnimation(VehicleEntity vehicle, PoseStack matrixStack, float partialTicks) {
        float timeSinceHit = (float)vehicle.getTimeSinceHit() - partialTicks;
        if (timeSinceHit > 0.0f) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin((float)timeSinceHit) * timeSinceHit));
        }
    }
}

