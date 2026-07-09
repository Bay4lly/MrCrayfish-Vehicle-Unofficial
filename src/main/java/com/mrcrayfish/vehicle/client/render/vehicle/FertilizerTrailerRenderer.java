/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FertilizerTrailerRenderer
extends AbstractTrailerRenderer<FertilizerTrailerEntity> {
    protected final AbstractVehicleRenderer.PropertyFunction<FertilizerTrailerEntity, StorageInventory> storageProperty = new AbstractVehicleRenderer.PropertyFunction<FertilizerTrailerEntity, StorageInventory>(FertilizerTrailerEntity::getInventory, null);

    public FertilizerTrailerRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable FertilizerTrailerEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.FERTILIZER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -0.71875f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 0.71875f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        StorageInventory inventory = this.storageProperty.get(vehicle);
        if (inventory != null) {
            int layer = 0;
            int index = 0;
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) continue;
                matrixStack.pushPose();
                matrixStack.translate(-0.34375, -0.1875, -0.1875);
                matrixStack.scale(0.45f, 0.45f, 0.45f);
                int count = Math.max(1, stack.getCount() / 32);
                int width = 3;
                int maxLayerCount = 6;
                for (int j = 0; j < count; ++j) {
                    matrixStack.pushPose();
                    int layerIndex = index % maxLayerCount;
                    matrixStack.translate(0.0, (double)layer * 0.1 + (double)j * 0.0625, 0.0);
                    matrixStack.translate((double)(layerIndex % width) * 0.5, 0.0, (double)(layerIndex / width) * 0.75);
                    matrixStack.translate(0.5 * (double)(layer % 2), 0.0, 0.0);
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90.0f));
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(47.0f * (float)index));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(2.0f * (float)layerIndex));
                    matrixStack.translate((double)layer * 0.001, (double)layer * 0.001, (double)layer * 0.001);
                    Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(stack));
                    matrixStack.popPose();
                    if (++index % maxLayerCount != 0) continue;
                    ++layer;
                }
                matrixStack.popPose();
            }
        }
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.5, -0.4375);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
        if (vehicle != null) {
            float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(-wheelRotation));
        }
        matrixStack.scale(1.25f, 1.25f, 1.25f);
        RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.FERTILIZER_TRAILER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
    }
}

