/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FuelDrumRenderer
implements BlockEntityRenderer<FuelDrumTileEntity> {
    public static final RenderType LABEL_BACKGROUND = RenderType.create((String)"vehicle:fuel_drum_label_background", (VertexFormat)DefaultVertexFormat.POSITION_COLOR, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (RenderType.CompositeState)RenderType.CompositeState.builder().createCompositeState(false));
    public static final RenderType LABEL_FLUID = RenderType.create((String)"vehicle:fuel_drum_label_fluid", (VertexFormat)DefaultVertexFormat.POSITION_TEX, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (RenderType.CompositeState)RenderType.CompositeState.builder().setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, true)).createCompositeState(false));
    private final BlockEntityRenderDispatcher renderer;

    public FuelDrumRenderer(BlockEntityRendererProvider.Context dispatcher) {
        this.renderer = dispatcher.getBlockEntityRenderDispatcher();
    }

    public void render(FuelDrumTileEntity fuelDrumTileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int lightTexture, int overlayTexture) {
        BlockHitResult result;
        if (Minecraft.getInstance().player.isCrouching() && fuelDrumTileEntity.hasFluid() && this.renderer.cameraHitResult != null && this.renderer.cameraHitResult.getType() == HitResult.Type.BLOCK && (result = (BlockHitResult)this.renderer.cameraHitResult).getBlockPos().equals((Object)fuelDrumTileEntity.getBlockPos())) {
            this.drawFluidLabel(Minecraft.getInstance().font, fuelDrumTileEntity.getFluidTank(), matrixStack, renderTypeBuffer);
        }
    }

    private void drawFluidLabel(Font fontRendererIn, FluidTank tank, PoseStack matrixStack, MultiBufferSource renderTypeBuffer) {
        if (tank.getFluid().isEmpty()) {
            return;
        }
        FluidStack stack = tank.getFluid();
        TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of((Fluid)tank.getFluid().getFluid()).getStillTexture());
        if (sprite != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.0f, 1.25f, 0.0f);
            matrixStack.mulPose(this.renderer.camera.rotation());
            matrixStack.scale(-0.025f, -0.025f, 0.025f);
            matrixStack.scale(0.5f, 0.5f, 0.0f);
            float level = (float)tank.getFluidAmount() / (float)tank.getCapacity();
            String name = stack.getDisplayName().getString() + " " + level + "%";
            float x = (float)(-fontRendererIn.width(name)) / 20.0f;
            fontRendererIn.drawInBatch(name, x, -14.0f, -1, false, matrixStack.last().pose(), renderTypeBuffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
            matrixStack.popPose();
        }
    }
}

