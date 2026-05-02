package com.mrcrayfish.vehicle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractPoweredRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.client.render.CachedVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageCraftVehicle;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkstationScreen extends AbstractContainerScreen<WorkstationContainer>
{
    private static final ResourceLocation GUI = new ResourceLocation("vehicle:textures/gui/workstation.png");
    private static CachedVehicle cachedVehicle;
    private static CachedVehicle prevCachedVehicle;
    private static int currentVehicle = 0;
    private static boolean showRemaining = false;

    private final List<EntityType<?>> vehicleTypes;
    private final List<MaterialItem> materials;
    private List<MaterialItem> filteredMaterials;
    private final Inventory playerInventory;
    private final WorkstationTileEntity workstation;
    private Button btnCraft;
    private CheckBox checkBoxMaterials;
    private boolean validEngine;
    private boolean transitioning;
    private int vehicleScale = 30;
    private int prevVehicleScale = 30;

    public WorkstationScreen(WorkstationContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.workstation = container.getTileEntity();
        this.imageWidth = 256;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 93;
        this.materials = new ArrayList<>();
        this.vehicleTypes = this.getVehicleTypes(playerInventory.player.level());
        this.vehicleTypes.sort(Comparator.comparing(type -> ForgeRegistries.ENTITY_TYPES.getKey(type).getPath()));
    }

    private List<EntityType<?>> getVehicleTypes(Level world)
    {
        return world.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == ModRecipeTypes.WORKSTATION.get())
                .map(recipe -> (WorkstationRecipe) recipe)
                .map(WorkstationRecipe::getVehicle)
                .filter(entityType -> !Config.SERVER.disabledVehicles.get().contains(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType)).toString())) // FIXME
                .collect(Collectors.toList());
    }

    @Override
    public void init()
    {
        super.init();

        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            this.loadVehicle(Math.floorMod(currentVehicle - 1,  this.vehicleTypes.size()));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }).bounds(this.leftPos + 9, this.topPos + 18, 15, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            this.loadVehicle(Math.floorMod(currentVehicle + 1,  this.vehicleTypes.size()));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }).bounds(this.leftPos + 153, this.topPos + 18, 15, 20).build());

        this.btnCraft = this.addRenderableWidget(Button.builder(Component.translatable("gui.vehicle.craft"), button -> {
            ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(this.vehicleTypes.get(currentVehicle));
            Objects.requireNonNull(registryName, "Vehicle registry name must not be null!");
            PacketHandler.instance.sendToServer(new MessageCraftVehicle(registryName.toString(), this.workstation.getBlockPos()));
        }).bounds(this.leftPos + 172, this.topPos + 6, 97, 20).build());

        this.btnCraft.active = false;
        this.checkBoxMaterials = this.addRenderableWidget(new CheckBox(this.leftPos + 172, this.topPos + 51,  Component.translatable("gui.vehicle.show_remaining")));
        this.checkBoxMaterials.setToggled(WorkstationScreen.showRemaining);
        this.loadVehicle(currentVehicle);
    }

    @Override
    public void containerTick()
    {
        super.containerTick();

        this.validEngine = true;

        for(MaterialItem material : this.materials)
        {
            material.tick();
        }

        boolean canCraft = true;
        for(MaterialItem material : this.materials)
        {
            if(!material.isEnabled())
            {
                canCraft = false;
                break;
            }
        }

        if(cachedVehicle.getRenderer() instanceof AbstractPoweredRenderer)
        {
            AbstractPoweredRenderer<?> poweredRenderer = (AbstractPoweredRenderer<?>) cachedVehicle.getRenderer();
            VehicleProperties properties = cachedVehicle.getProperties();
            if(properties.getEngineType() != EngineType.NONE)
            {
                ItemStack engine = this.workstation.getItem(1);
                if(!engine.isEmpty() && engine.getItem() instanceof EngineItem)
                {
                    EngineItem engineItem = (EngineItem) engine.getItem();
                    IEngineType engineType = engineItem.getEngineType();
                    if(properties.getEngineType() == engineType)
                    {
                        poweredRenderer.setEngineStack(engine);
                    }
                    else
                    {
                        canCraft = false;
                        this.validEngine = false;
                        poweredRenderer.setEngineStack(ItemStack.EMPTY);
                    }
                }
                else
                {
                    canCraft = false;
                    this.validEngine = false;
                    poweredRenderer.setEngineStack(ItemStack.EMPTY);
                }
            }

            if(cachedVehicle.getProperties().canChangeWheels())
            {
                ItemStack wheels = this.workstation.getItem(2);
                if(!wheels.isEmpty() && wheels.getItem() instanceof WheelItem)
                {
                    poweredRenderer.setWheelStack(wheels);
                }
                else
                {
                    poweredRenderer.setWheelStack(ItemStack.EMPTY);
                    canCraft = false;
                }
            }
        }
        this.btnCraft.active = canCraft;

        this.prevVehicleScale = this.vehicleScale;
        if(this.transitioning)
        {
            if(this.vehicleScale > 0)
            {
                this.vehicleScale = Math.max(0, this.vehicleScale - 6);
            }
            else
            {
                this.transitioning = false;
            }
        }
        else if(this.vehicleScale < 30)
        {
            this.vehicleScale = Math.min(30, this.vehicleScale + 6);
        }

        this.updateVehicleColor();
    }

    private void updateVehicleColor()
    {
        if(cachedVehicle.getProperties().isColored())
        {
            AbstractVehicleRenderer<?> renderer = cachedVehicle.getRenderer();
            ItemStack dyeStack = this.workstation.getItem(0);
            if(dyeStack.getItem() instanceof DyeItem)
            {
                renderer.setColor(((DyeItem) dyeStack.getItem()).getDyeColor().getTextColor());
            }
            else
            {
                renderer.setColor(VehicleEntity.DYE_TO_COLOR[0]);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
        WorkstationScreen.showRemaining = this.checkBoxMaterials.isToggled();
        return result;
    }

    @SuppressWarnings({"unchecked"})
    private void loadVehicle(int index)
    {
        prevCachedVehicle = cachedVehicle;
        cachedVehicle = new CachedVehicle(this.vehicleTypes.get(index));
        currentVehicle = index;

        AbstractVehicleRenderer<?> renderer = cachedVehicle.getRenderer();
        if(renderer instanceof AbstractLandVehicleRenderer<?>)
        {
            ((AbstractLandVehicleRenderer<?>) renderer).setEngineStack(ItemStack.EMPTY);
            ((AbstractLandVehicleRenderer<?>) renderer).setWheelStack(ItemStack.EMPTY);
        }

        this.materials.clear();

        WorkstationRecipe recipe = WorkstationRecipes.getRecipe(cachedVehicle.getType(), this.minecraft.level);
        if(recipe != null)
        {
            for(int i = 0; i < recipe.getMaterials().size(); i++)
            {
                MaterialItem item = new MaterialItem(recipe.getMaterials().get(i));
                item.updateEnabledState();
                this.materials.add(item);
            }
        }

        if(Config.CLIENT.workstationAnimation.get() && prevCachedVehicle != null && prevCachedVehicle.getType() != cachedVehicle.getType())
        {
            this.transitioning = true;
        }
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        for(int i = 0; i < filteredMaterials.size(); i++)
        {
            int itemX = startX + 172;
            int itemY = startY + i * 19 + 63;
            if(CommonUtils.isMouseWithin(mouseX, mouseY, itemX, itemY, 80, 19))
            {
                MaterialItem materialItem = this.filteredMaterials.get(i);
                if(materialItem != MaterialItem.EMPTY)
                {
                    matrixStack.renderTooltip(this.font, materialItem.getDisplayStack(), mouseX, mouseY); // FIXME
                }
            }
        }

        VehicleProperties properties = cachedVehicle.getProperties();
        if(properties.isColored())
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.optional").withStyle(ChatFormatting.AQUA), Component.translatable("vehicle.tooltip.paint_color").withStyle(ChatFormatting.GRAY)), startX, startY, 172, 29, mouseX, mouseY, 0);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.paint_color"), Component.translatable("vehicle.tooltip.not_applicable").withStyle(ChatFormatting.GRAY)), startX, startY, 172, 29, mouseX, mouseY, 0);
        }

        if(properties.getEngineType() != EngineType.NONE)
        {
            Component engineName = properties.getEngineType().getEngineName();
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.required").withStyle(ChatFormatting.RED), engineName), startX, startY, 192, 29, mouseX, mouseY, 1);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.engine"), Component.translatable("vehicle.tooltip.not_applicable").withStyle(ChatFormatting.GRAY)), startX, startY, 192, 29, mouseX, mouseY, 1);
        }

        if(properties.canChangeWheels())
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.required").withStyle(ChatFormatting.RED), Component.translatable("vehicle.tooltip.wheels")), startX, startY, 212, 29, mouseX, mouseY, 2);
        }
        else
        {
            this.drawSlotTooltip(matrixStack, Lists.newArrayList(Component.translatable("vehicle.tooltip.wheels"), Component.translatable("vehicle.tooltip.not_applicable").withStyle(ChatFormatting.GRAY)), startX, startY, 212, 29, mouseX, mouseY, 2);
        }
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        /* Fixes partial ticks to use percentage from 0 to 1 */
        partialTicks = this.minecraft.getFrameTime();

        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;

        RenderSystem.enableBlend();

        matrixStack.blit(GUI, startX, startY, 0, 0, 173, 184);
        matrixStack.blit(GUI, startX + 173, startY, 78, 184, 173, 0, 1, 184, 256, 256);
        matrixStack.blit(GUI, startX + 251, startY, 174, 0, 24, 184);
        matrixStack.blit(GUI, startX + 256, startY + 64, 12, 241, 12, 15);

        /* Slots */
        VehicleProperties properties = cachedVehicle.getProperties();
        this.drawSlot(matrixStack, startX, startY, 172, 29, 164, 184, 0, false, properties.isColored());
        boolean needsEngine = properties.getEngineType() != EngineType.NONE;
        this.drawSlot(matrixStack, startX, startY, 192, 29, 164, 200, 1, !this.validEngine, needsEngine);
        boolean needsWheels = properties.canChangeWheels();
        this.drawSlot(matrixStack, startX, startY, 212, 29, 164, 216, 2, needsWheels && this.workstation.getItem(2).isEmpty(), needsWheels);

        matrixStack.drawCenteredString(this.font, cachedVehicle.getType().getDescription(), startX + 88, startY + 22, Color.WHITE.getRGB());

        this.filteredMaterials = this.getMaterials();
        for(int i = 0; i < this.filteredMaterials.size(); i++)
        {
            MaterialItem materialItem = this.filteredMaterials.get(i);
            ItemStack stack = materialItem.getDisplayStack();
            if(!stack.isEmpty())
            {
                Lighting.setupFor3DItems();
                if(materialItem.isEnabled())
                {
                    matrixStack.blit(GUI, startX + 172, startY + i * 19 + 63, 0, 184, 80, 19);
                }
                else
                {
                    matrixStack.blit(GUI, startX + 172, startY + i * 19 + 63, 0, 222, 80, 19);
                }

                String name = stack.getHoverName().getString();
                if(this.font.width(name) > 55)
                {
                    name = this.font.plainSubstrByWidth(stack.getHoverName().getString(), 50).trim() + "...";
                }
                matrixStack.drawString(this.font, name, startX + 172 + 22, startY + i * 19 + 6 + 63, Color.WHITE.getRGB());

                matrixStack.renderItem(stack, startX + 172 + 2, startY + i * 19 + 1 + 63);

                if(this.checkBoxMaterials.isToggled())
                {
                    int count = InventoryUtil.getItemStackAmount(this.minecraft.player, stack);
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }

                matrixStack.renderItemDecorations(this.font, stack, startX + 172 + 2, startY + i * 19 + 1 + 63, null); // FIXME
            }
        }

        this.drawVehicle(startX + 88, startY + 90, partialTicks);
    }

    private void drawVehicle(int x, int y, float partialTicks)
    {
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().translate((float) x, (float) y, 1050.0F);
        RenderSystem.getModelViewStack().scale(-1.0F, -1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();

        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);

        float scale = this.prevVehicleScale + (this.vehicleScale - this.prevVehicleScale) * partialTicks;
        matrixStack.scale(scale, scale, scale);

        Quaternionf quaternion = Axis.XP.rotationDegrees(-5F);
        Quaternionf quaternion1 = Axis.YP.rotationDegrees(-(this.minecraft.player.tickCount + partialTicks));
        quaternion.mul(quaternion1);
        matrixStack.mulPose(quaternion);

        CachedVehicle transitionVehicle = this.transitioning ? prevCachedVehicle : cachedVehicle;

        PartPosition position = transitionVehicle.getProperties().getDisplayPosition();
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) position.getRotZ()));
        matrixStack.translate(position.getX(), position.getY(), position.getZ());

        Lighting.setupForEntityInInventory();

        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        renderManager.setRenderShadow(false);
        renderManager.overrideCameraOrientation(quaternion);
        MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> transitionVehicle.getRenderer().setupTransformsAndRender(null, matrixStack, renderTypeBuffer, partialTicks, 15728880));
        renderTypeBuffer.endBatch();
        renderManager.setRenderShadow(true);

        matrixStack.popPose();

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();

        Lighting.setupFor3DItems();
    }

    private void drawSlot(GuiGraphics matrixStack, int startX, int startY, int x, int y, int iconX, int iconY, int slot, boolean required, boolean applicable)
    {
        int textureOffset = required ? 18 : 0;
        matrixStack.blit(GUI, startX + x, startY + y, 198, 20 + textureOffset, 18, 18);
        if(this.workstation.getItem(slot).isEmpty())
        {
            if(applicable)
            {
                matrixStack.blit(GUI, startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), iconY, 16, 16);
            }
            else
            {
                matrixStack.blit(GUI, startX + x + 1, startY + y + 1, iconX + (required ? 16 : 0), 232, 16, 16);
            }
        }
    }

    private void drawSlotTooltip(GuiGraphics matrixStack, List<Component> text, int startX, int startY, int x, int y, int mouseX, int mouseY, int slot)
    {
        if(this.workstation.getItem(slot).isEmpty())
        {
            if(CommonUtils.isMouseWithin(mouseX, mouseY, startX + x, startY + y, 18, 18))
            {
                matrixStack.renderTooltip(this.font, Lists.transform(text, Component::getVisualOrderText), mouseX, mouseY);
            }
        }
    }

    private List<MaterialItem> getMaterials()
    {
        List<MaterialItem> materials = NonNullList.withSize(7, MaterialItem.EMPTY);
        List<MaterialItem> filteredMaterials = this.materials.stream().filter(materialItem -> this.checkBoxMaterials.isToggled() ? !materialItem.isEnabled() : materialItem != MaterialItem.EMPTY).collect(Collectors.toList());
        for(int i = 0; i < filteredMaterials.size() && i < materials.size(); i++)
        {
            materials.set(i, filteredMaterials.get(i));
        }
        return materials;
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY)
    {
        // FIXME see super method
        matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        matrixStack.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    public static class MaterialItem
    {
        public static final MaterialItem EMPTY = new MaterialItem();

        private long lastTime = System.currentTimeMillis();
        private int displayIndex;
        private boolean enabled = false;
        private WorkstationIngredient ingredient = null;
        private final List<ItemStack> displayStacks = new ArrayList<>();

        public MaterialItem() {}

        public MaterialItem(WorkstationIngredient ingredient)
        {
            this.ingredient = ingredient;
            Stream.of(ingredient.getItems()).forEach(stack -> {
                ItemStack displayStack = stack.copy();
                displayStack.setCount(ingredient.getCount());
                this.displayStacks.add(displayStack);
            });
        }

        public WorkstationIngredient getIngredient()
        {
            return this.ingredient;
        }

        public void tick()
        {
            if(this.ingredient == null)
                return;

            this.updateEnabledState();
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastTime >= 1000)
            {
                this.displayIndex = (this.displayIndex + 1) % this.displayStacks.size();
                this.lastTime = currentTime;
            }
        }

        public ItemStack getDisplayStack()
        {
            return this.ingredient != null ? this.displayStacks.get(this.displayIndex) : ItemStack.EMPTY;
        }

        public void updateEnabledState()
        {
            if(this.ingredient != null)
            {
                this.enabled = InventoryUtil.hasWorkstationIngredient(Minecraft.getInstance().player, this.ingredient);
            }
        }

        public boolean isEnabled()
        {
            return this.ingredient == null || this.enabled;
        }
    }
}
