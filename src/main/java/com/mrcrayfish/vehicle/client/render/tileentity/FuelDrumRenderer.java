package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class FuelDrumRenderer implements BlockEntityRenderer<FuelDrumTileEntity>
{
    public static final RenderType LABEL_BACKGROUND = RenderType.create("vehicle:fuel_drum_label_background", DefaultVertexFormat.POSITION_COLOR, Mode.QUADS, 256, RenderType.CompositeState.builder().createCompositeState(false));
    public static final RenderType LABEL_FLUID = RenderType.create("vehicle:fuel_drum_label_fluid", DefaultVertexFormat.POSITION_TEX, Mode.QUADS, 256, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, true)).createCompositeState(false));

    private final BlockEntityRenderDispatcher renderer;

    public FuelDrumRenderer(BlockEntityRendererProvider.Context dispatcher)
    {
        this.renderer = dispatcher.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(FuelDrumTileEntity fuelDrumTileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int lightTexture, int overlayTexture)
    {
        if(Minecraft.getInstance().player.isCrouching())
        {
            if(fuelDrumTileEntity.hasFluid() && this.renderer.cameraHitResult != null && this.renderer.cameraHitResult.getType() == HitResult.Type.BLOCK)
            {
                BlockHitResult result = (BlockHitResult) this.renderer.cameraHitResult;
                if(result.getBlockPos().equals(fuelDrumTileEntity.getBlockPos()))
                {
                    this.drawFluidLabel(this.renderer.font, fuelDrumTileEntity.getFluidTank(), matrixStack, renderTypeBuffer);
                }
            }
        }
    }

    private void drawFluidLabel(Font fontRendererIn, FluidTank tank, PoseStack matrixStack, MultiBufferSource renderTypeBuffer)
    {
        if(tank.getFluid().isEmpty())
            return;

        FluidStack stack = tank.getFluid();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getStillTexture());
        if(sprite != null)
        {
            matrixStack.pushPose();
            matrixStack.translate(0.0F, 1.25F, 0.0F);
            matrixStack.mulPose(this.renderer.camera.rotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);

            matrixStack.scale(0.5F, 0.5F, 0);

            float level = tank.getFluidAmount() / (float) tank.getCapacity();
            String name = stack.getDisplayName().getString() + " " + level + "%"; // FIXME return level bar rendering
            float x = -fontRendererIn.width(name) / 20.0F; // FIXME magic number
            fontRendererIn.drawInBatch(name, x, -14F, -1, false, matrixStack.last().pose(), renderTypeBuffer, DisplayMode.NORMAL, 0, 15728880);

            matrixStack.popPose();
        }
    }
}
