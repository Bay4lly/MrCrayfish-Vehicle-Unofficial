package com.mrcrayfish.vehicle.client.screen;

import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class StorageScreen extends AbstractContainerScreen<StorageContainer>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final Inventory playerInventory;
    private final int inventoryRows;

    public StorageScreen(StorageContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.inventoryRows = container.getStorageInventory().getContainerSize() / 9;
        this.imageHeight = 114 + this.inventoryRows * 18;
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY)
    {
        // FIXME see super method
        matrixStack.drawString(this.font, this.title, 8, 6, 4210752, false);
        matrixStack.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        matrixStack.blit(CHEST_GUI_TEXTURE, startX, startY, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        matrixStack.blit(CHEST_GUI_TEXTURE, startX, startY + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
