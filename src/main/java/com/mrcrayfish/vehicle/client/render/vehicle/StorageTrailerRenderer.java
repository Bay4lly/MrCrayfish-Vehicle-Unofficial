/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class StorageTrailerRenderer
extends AbstractTrailerRenderer<StorageTrailerEntity> {
    private static final ResourceLocation TEXTURE_CHRISTMAS = ResourceLocation.parse((String)"textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = ResourceLocation.parse((String)"textures/entity/chest/normal.png");
    private final boolean isChristmas;

    public StorageTrailerRenderer(Supplier<VehicleProperties> properties) {
        super(properties);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
    }

    @Override
    public void render(@Nullable StorageTrailerEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.STORAGE_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -0.71875f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 0.71875f, -0.5f, 0.0f, 1.25f, partialTicks, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0625, 0.0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        matrixStack.scale(0.9f, 0.9f, 0.9f);
        ItemStack chest = new ItemStack((ItemLike)Blocks.CHEST);
        RenderUtil.renderModel(chest, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(chest));
        matrixStack.popPose();
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.STORAGE_TRAILER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
    }
}

