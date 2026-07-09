/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.joml.Matrix4f
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractTrailerRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class FluidTrailerRenderer
extends AbstractTrailerRenderer<FluidTrailerEntity> {
    public FluidTrailerRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable FluidTrailerEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.FLUID_TRAILER.getModel(), matrixStack, renderTypeBuffer, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, false, -0.71875f, -0.5f, -0.15625f, 1.25f, partialTicks, light);
        this.renderWheel(vehicle, matrixStack, renderTypeBuffer, true, 0.71875f, -0.5f, -0.15625f, 1.25f, partialTicks, light);
        if (vehicle != null && vehicle.getTank() != null) {
            float height = 9.9f * ((float)vehicle.getTank().getFluidAmount() / (float)vehicle.getTank().getCapacity()) * 0.0625f;
            this.drawFluid(vehicle, vehicle.getTank(), matrixStack, renderTypeBuffer, -0.3875f, -0.1875f, -0.99f, 0.7625f, height, 1.67f, light);
        }
    }

    private void drawFluid(FluidTrailerEntity vehicle, FluidTank tank, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light) {
        Fluid fluid = tank.getFluid().getFluid();
        if (fluid == Fluids.EMPTY) {
            return;
        }
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of((Fluid)fluid);
        TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getStillTexture());
        int fluidColor = clientFluid.getTintColor(fluid.defaultFluidState(), (BlockAndTintGetter)vehicle.getCommandSenderWorld(), vehicle.blockPosition());
        float red = (float)(fluidColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(fluidColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(fluidColor & 0xFF) / 255.0f;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * width, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());
        VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.last().pose();
        buffer.addVertex(matrix, x + width, y, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x, y, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x, y + height, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x + width, y + height, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x, y, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x + width, y, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x, y + height, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());
        buffer.addVertex(matrix, x, y + height, z).setColor(red, green, blue, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x, y + height, z + depth).setColor(red, green, blue, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red, green, blue, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        buffer.addVertex(matrix, x + width, y + height, z).setColor(red, green, blue, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.FLUID_TRAILER, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
    }
}

