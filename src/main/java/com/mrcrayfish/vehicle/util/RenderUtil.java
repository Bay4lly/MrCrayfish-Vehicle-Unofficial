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
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.neoforged.neoforge.client.ClientHooks
 */
package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.ClientHooks;

public class RenderUtil {
    public static void drawTexturedModalRect(double x, double y, int textureX, int textureY, double width, double height) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex((float)x, (float)(y + height), 0.0f).setUv((float)textureX * 0.00390625f, (float)((double)textureY + height) * 0.00390625f);
        bufferbuilder.addVertex((float)(x + width), (float)(y + height), 0.0f).setUv((float)((double)textureX + width) * 0.00390625f, (float)((double)textureY + height) * 0.00390625f);
        bufferbuilder.addVertex((float)(x + width), (float)y, 0.0f).setUv((float)((double)textureX + width) * 0.00390625f, (float)textureY * 0.00390625f);
        bufferbuilder.addVertex((float)x, (float)y, 0.0f).setUv((float)textureX * 0.00390625f, (float)textureY * 0.00390625f);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
    }

    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor) {
        float redStart = (float)(leftColor >> 24 & 0xFF) / 255.0f;
        float greenStart = (float)(leftColor >> 16 & 0xFF) / 255.0f;
        float blueStart = (float)(leftColor >> 8 & 0xFF) / 255.0f;
        float alphaStart = (float)(leftColor & 0xFF) / 255.0f;
        float redEnd = (float)(rightColor >> 24 & 0xFF) / 255.0f;
        float greenEnd = (float)(rightColor >> 16 & 0xFF) / 255.0f;
        float blueEnd = (float)(rightColor >> 8 & 0xFF) / 255.0f;
        float alphaEnd = (float)(rightColor & 0xFF) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex((float)right, (float)top, 0.0f).setColor(greenEnd, blueEnd, alphaEnd, redEnd);
        bufferbuilder.addVertex((float)left, (float)top, 0.0f).setColor(greenStart, blueStart, alphaStart, redStart);
        bufferbuilder.addVertex((float)left, (float)bottom, 0.0f).setColor(greenStart, blueStart, alphaStart, redStart);
        bufferbuilder.addVertex((float)right, (float)bottom, 0.0f).setColor(greenEnd, blueEnd, alphaEnd, redEnd);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static BakedModel getModel(ItemStack stack) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
    }

    public static void renderColoredModel(BakedModel model, ItemDisplayContext transformType, boolean leftInteractionHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int color, int lightTexture, int overlayTexture) {
        matrixStack.pushPose();
        ClientHooks.handleCameraTransforms((PoseStack)matrixStack, (BakedModel)model, (ItemDisplayContext)transformType, (boolean)leftInteractionHanded);
        matrixStack.translate(-0.5, -0.5, -0.5);
        if (!model.isCustomRenderer()) {
            VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(Sheets.cutoutBlockSheet());
            RenderUtil.renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.popPose();
    }

    public static void renderDamagedVehicleModel(BakedModel model, ItemDisplayContext transformType, boolean leftInteractionHanded, PoseStack matrixStack, int stage, int color, int lightTexture, int overlayTexture) {
        matrixStack.pushPose();
        ClientHooks.handleCameraTransforms((PoseStack)matrixStack, (BakedModel)model, (ItemDisplayContext)transformType, (boolean)leftInteractionHanded);
        matrixStack.translate(-0.5, -0.5, -0.5);
        if (!model.isCustomRenderer()) {
            Minecraft mc = Minecraft.getInstance();
            PoseStack.Pose entry = matrixStack.last();
            SheetedDecalTextureGenerator vertexBuilder = new SheetedDecalTextureGenerator(mc.renderBuffers().crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(stage)), matrixStack.last(), 1.0f);
            RenderUtil.renderModel(model, ItemStack.EMPTY, color, lightTexture, overlayTexture, matrixStack, (VertexConsumer)vertexBuilder);
        }
        matrixStack.popPose();
    }

    public static void renderModel(ItemStack stack, ItemDisplayContext transformType, boolean leftInteractionHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int lightTexture, int overlayTexture, BakedModel model) {
        if (!stack.isEmpty()) {
            boolean tridentFlag;
            matrixStack.pushPose();
            boolean isGui = transformType == ItemDisplayContext.GUI;
            boolean bl = tridentFlag = isGui || transformType == ItemDisplayContext.GROUND || transformType == ItemDisplayContext.FIXED;
            if (stack.getItem() == Items.TRIDENT && tridentFlag) {
                model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ResourceLocation.parse((String)"minecraft:trident"), "inventory"));
            }
            model = ClientHooks.handleCameraTransforms((PoseStack)matrixStack, (BakedModel)model, (ItemDisplayContext)transformType, (boolean)leftInteractionHanded);
            matrixStack.translate(-0.5, -0.5, -0.5);
            if (!model.isCustomRenderer() && (stack.getItem() != Items.TRIDENT || tridentFlag)) {
                RenderType renderType = ItemBlockRenderTypes.getRenderType((ItemStack)stack, (boolean)false);
                if (isGui && Objects.equals(renderType, Sheets.translucentCullBlockSheet())) {
                    renderType = Sheets.translucentCullBlockSheet();
                }
                VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer((MultiBufferSource)renderTypeBuffer, (RenderType)renderType, (boolean)true, (boolean)stack.hasFoil());
                RenderUtil.renderModel(model, stack, -1, lightTexture, overlayTexture, matrixStack, vertexBuilder);
            } else {
                Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer().renderByItem(stack, transformType, matrixStack, renderTypeBuffer, lightTexture, overlayTexture);
            }
            matrixStack.popPose();
        }
    }

    private static void renderModel(BakedModel model, ItemStack stack, int color, int lightTexture, int overlayTexture, PoseStack matrixStack, VertexConsumer vertexBuilder) {
        RandomSource random = RandomSource.create();
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            RenderUtil.renderQuads(matrixStack, vertexBuilder, model.getQuads(null, direction, random), stack, color, lightTexture, overlayTexture);
        }
        random.setSeed(42L);
        RenderUtil.renderQuads(matrixStack, vertexBuilder, model.getQuads(null, null, random), stack, color, lightTexture, overlayTexture);
    }

    private static void renderQuads(PoseStack matrixStack, VertexConsumer vertexBuilder, List<BakedQuad> quads, ItemStack stack, int color, int lightTexture, int overlayTexture) {
        boolean useItemColor = !stack.isEmpty() && color == -1;
        PoseStack.Pose entry = matrixStack.last();
        for (BakedQuad quad : quads) {
            int tintColor = 0xFFFFFF;
            if (quad.isTinted()) {
                tintColor = useItemColor ? Minecraft.getInstance().getItemColors().getColor(stack, quad.getTintIndex()) : color;
            }
            float red = (float)(tintColor >> 16 & 0xFF) / 255.0f;
            float green = (float)(tintColor >> 8 & 0xFF) / 255.0f;
            float blue = (float)(tintColor & 0xFF) / 255.0f;
            float[] brightnesses = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
            int[] lights = new int[]{lightTexture, lightTexture, lightTexture, lightTexture};
            vertexBuilder.putBulkData(entry, quad, brightnesses, red, green, blue, 1.0f, lights, overlayTexture, false);
        }
    }

    public static List<Component> lines(FormattedText text, int maxWidth) {
        List<FormattedText> lines = Minecraft.getInstance().font.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
        return lines.stream().map(t -> Component.literal(t.getString()).withStyle(ChatFormatting.GRAY)).collect(Collectors.toList());
    }
}

