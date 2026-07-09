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
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
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

public class SeederTrailerRenderer
extends AbstractTrailerRenderer<SeederTrailerEntity> {
    protected final AbstractVehicleRenderer.PropertyFunction<SeederTrailerEntity, StorageInventory> storageProperty = new AbstractVehicleRenderer.PropertyFunction<SeederTrailerEntity, StorageInventory>(SeederTrailerEntity::getInventory, null);

    public SeederTrailerRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable SeederTrailerEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.SEEDER_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, -1.09375f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, 1.09375f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        StorageInventory inventory = this.storageProperty.get(vehicle);
        if (inventory != null) {
            int layer = 0;
            int index = 0;
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) continue;
                matrixStack.pushPose();
                matrixStack.translate(-0.65625, -0.1875, -0.125);
                matrixStack.scale(0.45f, 0.45f, 0.45f);
                int count = Math.max(1, stack.getCount() / 16);
                int width = 4;
                int maxLayerCount = 8;
                for (int j = 0; j < count; ++j) {
                    matrixStack.pushPose();
                    int layerIndex = index % maxLayerCount;
                    matrixStack.translate(0.0, (double)layer * 0.05, 0.0);
                    matrixStack.translate((double)(layerIndex % width) * 0.75, 0.0, (double)(layerIndex / width) * 0.5);
                    matrixStack.translate(0.7 * (double)(layer % 2), 0.0, 0.0);
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90.0f));
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(47.0f * (float)index));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(2.0f * (float)layerIndex));
                    matrixStack.translate((double)layer * 0.001, (double)layer * 0.001, (double)layer * 0.001);
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, null, 0);
                    matrixStack.popPose();
                    if (++index % maxLayerCount != 0) continue;
                    ++layer;
                }
                matrixStack.popPose();
            }
        }
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, -0.75, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, -0.5, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, -0.25, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, 0.0, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, 0.25, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, 0.5, partialTicks, light);
        this.renderSpike(vehicle, matrixStack, renderTypeBuffer, 0.75, partialTicks, light);
    }

    private void renderSpike(SeederTrailerEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, double offsetX, float partialTicks, int light) {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, -0.65, 0.0);
        if (vehicle != null) {
            float wheelRotation = vehicle.prevWheelRotation + (vehicle.wheelRotation - vehicle.prevWheelRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(-wheelRotation));
        }
        matrixStack.scale(0.75f, 0.75f, 0.75f);
        RenderUtil.renderColoredModel(SpecialModels.SEED_SPIKER.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SEEDER_TRAILER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
    }
}

