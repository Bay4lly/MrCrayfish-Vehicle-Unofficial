package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.Matrix4f;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class FluidUtils
{
    private static final Map<ResourceLocation, Integer> CACHE_FLUID_COLOR = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void clearCacheFluidColor()
    {
        CACHE_FLUID_COLOR.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static int getAverageFluidColor(Fluid fluid)
    {
        Integer cachedColor = CACHE_FLUID_COLOR.get(BuiltInRegistries.FLUID.getKey(fluid));
        if(cachedColor != null)
        {
            return cachedColor;
        }
        else
        {
            int fluidColor = -1;
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid).getStillTexture());
            if(sprite != null)
            {
                long totalRed = 0;
                long totalGreen = 0;
                long totalBlue = 0;
                int pixelCount = sprite.contents().width() * sprite.contents().height();
                int red, green, blue;
                for(int i = 0; i < sprite.contents().height(); i++)
                {
                    for(int j = 0; j < sprite.contents().width(); j++)
                    {
                        int color = sprite.getPixelRGBA(0, j, i);
                        red = color & 255;
                        green = color >> 8 & 255;
                        blue = color >> 16 & 255;
                        totalRed += red * red;
                        totalGreen += green * green;
                        totalBlue += blue * blue;
                    }
                }
                fluidColor = (((int) Math.sqrt(totalRed / pixelCount) & 255) << 16) | (((int) Math.sqrt(totalGreen / pixelCount) & 255) << 8) | (((int) Math.sqrt(totalBlue / pixelCount) & 255));
            }
            CACHE_FLUID_COLOR.put(BuiltInRegistries.FLUID.getKey(fluid), fluidColor);
            return fluidColor;
        }
    }

    public static int transferFluid(IFluidHandler source, IFluidHandler target, int maxAmount)
    {
        FluidStack drained = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if(drained.getAmount() > 0)
        {
            int filled = target.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if(filled > 0)
            {
                drained = source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                return target.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidTankInGUI(FluidStack fluid, double x, double y, double percent, int height)
    {
        if(fluid == null || fluid.isEmpty())
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
        if(sprite != null)
        {
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float deltaV = maxV - minV;
            double tankLevel = percent * height;

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            RenderSystem.enableBlend();
            int count = 1 + ((int) Math.ceil(tankLevel)) / 16;
            for(int i = 0; i < count; i++)
            {
                double subHeight = Math.min(16.0, tankLevel - (16.0 * i));
                double offsetY = height - 16.0 * i - subHeight;
                drawQuad(x, y + offsetY, 16, subHeight, minU, (float) (maxV - deltaV * (subHeight / 16.0)), maxU, maxV);
            }
            RenderSystem.disableBlend();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawQuad(double x, double y, double width, double height, float minU, float minV, float maxU, float maxV)
    {
        BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex((float)x, (float)(y + height), 0).setUv(minU, maxV);
        buffer.addVertex((float)(x + width), (float)(y + height), 0).setUv(maxU, maxV);
        buffer.addVertex((float)(x + width), (float)y, 0).setUv(maxU, minV);
        buffer.addVertex((float)x, (float)y, 0).setUv(minU, minV);
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidInLevel(FluidTank tank, Level world, BlockPos pos, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light, FluidSides sides)
    {
        if(tank.isEmpty())
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getStillTexture());
        int waterColor = IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getTintColor(world.getFluidState(pos), world, pos);
        float red = (float) (waterColor >> 16 & 255) / 255.0F;
        float green = (float) (waterColor >> 8 & 255) / 255.0F;
        float blue = (float) (waterColor & 255) / 255.0F;
        float side = 0.9F;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());

        VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.last().pose();

        //left side
        if(sides.test(Direction.WEST))
        {
            buffer.addVertex(matrix, x + width, y, z).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y, z).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y + height, z).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
        }

        //right side
        if(sides.test(Direction.EAST))
        {
            buffer.addVertex(matrix, x, y, z + depth).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y, z + depth).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
        }

        maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());

        if(sides.test(Direction.SOUTH))
        {
            buffer.addVertex(matrix, x + width, y, z + depth).setColor(red * side, green * side, blue * side, 1.0F).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y, z).setColor(red * side, green * side, blue * side, 1.0F).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red * side, green * side, blue * side, 1.0F).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red * side, green * side, blue * side, 1.0F).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
        }

        if(sides.test(Direction.NORTH))
        {
            buffer.addVertex(matrix, x, y, z).setColor(red * side, green * side, blue * side, 1.0F).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y, z + depth).setColor(red * side, green * side, blue * side, 1.0F).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red * side, green * side, blue * side, 1.0F).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y + height, z).setColor(red * side, green * side, blue * side, 1.0F).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
        }

        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());

        if(sides.test(Direction.UP))
        {
            buffer.addVertex(matrix, x, y + height, z).setColor(red, green, blue, 1.0F).setUv(maxU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x, y + height, z + depth).setColor(red, green, blue, 1.0F).setUv(minU, minV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z + depth).setColor(red, green, blue, 1.0F).setUv(minU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
            buffer.addVertex(matrix, x + width, y + height, z).setColor(red, green, blue, 1.0F).setUv(maxU, maxV).setUv2(light & 0xFFFF, light >> 16).setNormal(0.0F, 1.0F, 0.0F);
        }
    }

    public static class FluidSides
    {
        private final EnumMap<Direction, Boolean> map = new EnumMap<>(Direction.class);

        public FluidSides(Direction ... sides)
        {
            Stream.of(Direction.values()).forEach(direction -> this.map.put(direction, false));
            Stream.of(sides).forEach(direction -> this.map.put(direction, true));
        }

        public boolean test(Direction direction)
        {
            return this.map.get(direction);
        }
    }
}
