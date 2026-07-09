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
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.joml.Matrix4f
 */
package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class FluidUtils {
    private static final Map<ResourceLocation, Integer> CACHE_FLUID_COLOR = new HashMap<ResourceLocation, Integer>();

    @OnlyIn(value=Dist.CLIENT)
    public static void clearCacheFluidColor() {
        CACHE_FLUID_COLOR.clear();
    }

    @OnlyIn(value=Dist.CLIENT)
    public static int getAverageFluidColor(Fluid fluid) {
        Integer cachedColor = CACHE_FLUID_COLOR.get(BuiltInRegistries.FLUID.getKey(fluid));
        if (cachedColor != null) {
            return cachedColor;
        }
        int fluidColor = -1;
        TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of((Fluid)fluid).getStillTexture());
        if (sprite != null) {
            long totalRed = 0L;
            long totalGreen = 0L;
            long totalBlue = 0L;
            int pixelCount = sprite.contents().width() * sprite.contents().height();
            for (int i = 0; i < sprite.contents().height(); ++i) {
                for (int j = 0; j < sprite.contents().width(); ++j) {
                    int color = sprite.getPixelRGBA(0, j, i);
                    int red = color & 0xFF;
                    int green = color >> 8 & 0xFF;
                    int blue = color >> 16 & 0xFF;
                    totalRed += (long)(red * red);
                    totalGreen += (long)(green * green);
                    totalBlue += (long)(blue * blue);
                }
            }
            fluidColor = ((int)Math.sqrt(totalRed / (long)pixelCount) & 0xFF) << 16 | ((int)Math.sqrt(totalGreen / (long)pixelCount) & 0xFF) << 8 | (int)Math.sqrt(totalBlue / (long)pixelCount) & 0xFF;
        }
        CACHE_FLUID_COLOR.put(BuiltInRegistries.FLUID.getKey(fluid), fluidColor);
        return fluidColor;
    }

    public static int transferFluid(IFluidHandler source, IFluidHandler target, int maxAmount) {
        int filled;
        FluidStack drained = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() > 0 && (filled = target.fill(drained, IFluidHandler.FluidAction.SIMULATE)) > 0) {
            drained = source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            return target.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        }
        return 0;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void drawFluidTankInGUI(FluidStack fluid, double x, double y, double percent, int height) {
        if (fluid == null || fluid.isEmpty()) {
            return;
        }
        TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of((Fluid)fluid.getFluid()).getStillTexture());
        if (sprite != null) {
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float deltaV = maxV - minV;
            double tankLevel = percent * (double)height;
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            int count = 1 + (int)Math.ceil(tankLevel) / 16;
            for (int i = 0; i < count; ++i) {
                double subHeight = Math.min(16.0, tankLevel - 16.0 * (double)i);
                double offsetY = (double)height - 16.0 * (double)i - subHeight;
                FluidUtils.drawQuad(x, y + offsetY, 16.0, subHeight, minU, (float)((double)maxV - (double)deltaV * (subHeight / 16.0)), maxU, maxV);
            }
            RenderSystem.disableBlend();
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void drawQuad(double x, double y, double width, double height, float minU, float minV, float maxU, float maxV) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex((float)x, (float)(y + height), 0.0f).setUv(minU, maxV);
        buffer.addVertex((float)(x + width), (float)(y + height), 0.0f).setUv(maxU, maxV);
        buffer.addVertex((float)(x + width), (float)y, 0.0f).setUv(maxU, minV);
        buffer.addVertex((float)x, (float)y, 0.0f).setUv(minU, minV);
        BufferUploader.drawWithShader((MeshData)buffer.buildOrThrow());
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void drawFluidInLevel(FluidTank tank, Level world, BlockPos pos, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light, FluidSides sides) {
        if (tank.isEmpty()) {
            return;
        }
        TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of((Fluid)tank.getFluid().getFluid()).getStillTexture());
        int waterColor = IClientFluidTypeExtensions.of((Fluid)tank.getFluid().getFluid()).getTintColor(world.getFluidState(pos), (BlockAndTintGetter)world, pos);
        float red = (float)(waterColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(waterColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(waterColor & 0xFF) / 255.0f;
        float side = 0.9f;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());
        VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.last().pose();
        if (sides.test(Direction.WEST)) {
            buffer.addVertex(matrix, x + width, y, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y + height, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        }
        if (sides.test(Direction.EAST)) {
            buffer.addVertex(matrix, x, y, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red - 0.25f, green - 0.25f, blue - 0.25f, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        }
        maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        if (sides.test(Direction.SOUTH)) {
            buffer.addVertex(matrix, x + width, y, z + depth).setColor(red * side, green * side, blue * side, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y, z).setColor(red * side, green * side, blue * side, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red * side, green * side, blue * side, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red * side, green * side, blue * side, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        }
        if (sides.test(Direction.NORTH)) {
            buffer.addVertex(matrix, x, y, z).setColor(red * side, green * side, blue * side, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y, z + depth).setColor(red * side, green * side, blue * side, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red * side, green * side, blue * side, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y + height, z).setColor(red * side, green * side, blue * side, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        }
        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());
        if (sides.test(Direction.UP)) {
            buffer.addVertex(matrix, x, y + height, z).setColor(red, green, blue, 1.0f).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red, green, blue, 1.0f).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red, green, blue, 1.0f).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red, green, blue, 1.0f).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0f, 1.0f, 0.0f);
        }
    }

    public static class FluidSides {
        private final EnumMap<Direction, Boolean> map = new EnumMap(Direction.class);

        public FluidSides(Direction ... sides) {
            Stream.of(Direction.values()).forEach(direction -> this.map.put((Direction)direction, false));
            Stream.of(sides).forEach(direction -> this.map.put((Direction)direction, true));
        }

        public boolean test(Direction direction) {
            return this.map.get(direction);
        }
    }
}

