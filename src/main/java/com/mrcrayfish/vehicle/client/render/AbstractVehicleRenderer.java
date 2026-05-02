package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractVehicleRenderer<T extends VehicleEntity & EntityRayTracer.IEntityRayTraceable>
{
    protected final PropertyFunction<T, VehicleProperties> vehiclePropertiesProperty;
    protected final PropertyFunction<T, Boolean> hasDriverProperty = new PropertyFunction<>(t -> t.getControllingPassenger() != null, false);
    protected final PropertyFunction<T, Boolean> towTrailerProperty = new PropertyFunction<>(VehicleEntity::canTowTrailer, false);
    protected final PropertyFunction<T, Integer> colorProperty = new PropertyFunction<>(VehicleEntity::getColor, -1);

    public AbstractVehicleRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        this.vehiclePropertiesProperty = new LazyPropertyFunction<>(VehicleEntity::getProperties, defaultProperties);
    }

    @Nullable
    public abstract EntityRayTracer.IRayTraceTransforms getRayTraceTransforms();

    protected abstract void render(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light);

    public void setupTransformsAndRender(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.pushPose();

        VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
        PartPosition bodyPosition = properties.getBodyPosition();
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) bodyPosition.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) bodyPosition.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) bodyPosition.getRotZ()));

        if(this.towTrailerProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
            Vec3 towBarOffset = properties.getTowBarPosition();
            matrixStack.translate(towBarOffset.x * 0.0625, towBarOffset.y * 0.0625 + 0.5, -towBarOffset.z * 0.0625);
            RenderUtil.renderColoredModel(SpecialModels.TOW_BAR.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }

        matrixStack.translate(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        matrixStack.scale((float) bodyPosition.getScale(), (float) bodyPosition.getScale(), (float) bodyPosition.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.translate(0.0, properties.getAxleOffset() * 0.0625, 0.0);
        matrixStack.translate(0.0, properties.getWheelOffset() * 0.0625, 0.0);

        this.render(vehicle, matrixStack, renderTypeBuffer, partialTicks, light);

        matrixStack.popPose();
    }

    /**
     *
     * @param entity
     * @param partialTicks
     */
    public void applyPreRotations(T entity, PoseStack stack, float partialTicks) {}

    public void applyPlayerModel(T entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks) {}

    public void applyPlayerRender(T entity, Player player, float partialTicks, PoseStack matrixStack) {}

    protected void renderDamagedPart(@Nullable T vehicle, ItemStack part, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, RenderUtil.getModel(part), matrixStack, renderTypeBuffer, light);
    }

    protected void renderDamagedPart(@Nullable T vehicle, BakedModel model, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light)
    {
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, false, light);
        this.renderDamagedPart(vehicle, model, matrixStack, renderTypeBuffer, true, light);
    }

    private void renderDamagedPart(@Nullable T vehicle, BakedModel model, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, boolean renderDamage, int light)
    {
        if(renderDamage && vehicle != null)
        {
            if(vehicle.getDestroyedStage() > 0)
            {
                RenderUtil.renderDamagedVehicleModel(model, ItemDisplayContext.NONE, false, matrixStack, vehicle.getDestroyedStage(), this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
            }
        }
        else
        {
            RenderUtil.renderColoredModel(model, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        }
    }

    /**
     * Renders a part (ItemStack) on the vehicle using the specified PartPosition. The rendering
     * will be cancelled if the PartPosition parameter is null.
     *
     * @param position the render definitions to construct to the part
     * @param model the part to render onto the vehicle
     */
    protected void renderPart(PartPosition position, BakedModel model, PoseStack matrixStack, MultiBufferSource buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null) return;
        matrixStack.pushPose();
        matrixStack.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.translate(0.0, -0.5, 0.0);
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.translate(0.0, 0.5, 0.0);
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) position.getRotZ()));
        RenderUtil.renderColoredModel(model, ItemDisplayContext.NONE, false, matrixStack, buffer, color, lightTexture, overlayTexture);
        matrixStack.popPose();
    }

    protected void renderKey(PartPosition position, ItemStack stack, BakedModel model, PoseStack matrixStack, MultiBufferSource buffer, int color, int lightTexture, int overlayTexture)
    {
        if(position == null) return;
        matrixStack.pushPose();
        matrixStack.translate(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        matrixStack.translate(0.0, -0.25, 0.0);
        matrixStack.scale((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
        matrixStack.mulPose(Axis.XP.rotationDegrees((float) position.getRotX()));
        matrixStack.mulPose(Axis.YP.rotationDegrees((float) position.getRotY()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees((float) position.getRotZ()));
        matrixStack.translate(0.0, 0.0, -0.05);
        RenderUtil.renderModel(stack, ItemDisplayContext.NONE, false, matrixStack, buffer, lightTexture, overlayTexture, model);
        matrixStack.popPose();
    }


    /**
     * Renders the engine (ItemStack) on the vehicle using the specified PartPosition. It adds a
     * subtle shake to the render to simulate it being powered.
     *
     * @param position the render definitions to construct to the part
     */
    protected void renderEngine(@Nullable PoweredVehicleEntity entity, @Nullable PartPosition position, BakedModel model, PoseStack matrixStack, MultiBufferSource buffer, int light)
    {
        matrixStack.pushPose();
        if(entity != null && entity.isEnginePowered() && entity.getControllingPassenger() != null)
        {
            matrixStack.mulPose(Axis.XP.rotationDegrees(0.5F * (entity.tickCount % 2)));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(0.5F * (entity.tickCount % 2)));
            matrixStack.mulPose(Axis.YP.rotationDegrees(-0.5F * (entity.tickCount % 2)));
        }
        this.renderPart(position, model, matrixStack, buffer, -1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    protected ISpecialModel getKeyHoleModel()
    {
        return SpecialModels.KEY_HOLE;
    }

    protected ISpecialModel getTowBarModel()
    {
        return SpecialModels.TOW_BAR;
    }

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    public void setVehicleProperties(VehicleProperties properties)
    {
        this.vehiclePropertiesProperty.setDefaultValue(properties);
    }

    public void setHasDriverProperty(boolean hasDriver)
    {
        this.hasDriverProperty.setDefaultValue(hasDriver);
    }

    public void setCanTowTrailer(boolean canTowTrailer)
    {
        this.towTrailerProperty.setDefaultValue(canTowTrailer);
    }

    public void setColor(int color)
    {
        this.colorProperty.setDefaultValue(color);
    }

    protected static class PropertyFunction<V extends VehicleEntity, T>
    {
        protected Function<V, T> function;
        protected T defaultValue;

        public PropertyFunction(Function<V, T> function, T defaultValue)
        {
            this.function = function;
            this.defaultValue = defaultValue;
        }

        public T get()
        {
            return this.get(null);
        }

        public T get(@Nullable V vehicle)
        {
            return vehicle != null ? this.function.apply(vehicle) : this.defaultValue;
        }

        protected void setDefaultValue(T value)
        {
            this.defaultValue = value;
        }
    }

    protected static class LazyPropertyFunction<V extends VehicleEntity, T> extends PropertyFunction<V, T>
    {
        private final Supplier<T> defaultValueCreator;

        public LazyPropertyFunction(Function<V, T> function, Supplier<T> defaultValue) {
            super(function, null);
            this.defaultValueCreator = defaultValue;
        }

        @Override
        public T get(@org.jetbrains.annotations.Nullable V vehicle)
        {
            if (this.defaultValue == null)
            {
                this.defaultValue = this.defaultValueCreator.get();
            }
            return super.get(vehicle);
        }
    }
}
