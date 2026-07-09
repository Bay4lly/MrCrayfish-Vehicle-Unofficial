/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.HitResult$Type
 */
package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

public abstract class AbstractPoweredRenderer<T extends PoweredVehicleEntity>
extends AbstractVehicleRenderer<T> {
    protected final AbstractVehicleRenderer.PropertyFunction<T, Boolean> renderEngineProperty = new AbstractVehicleRenderer.PropertyFunction<T, Boolean>(rec$ -> ((PoweredVehicleEntity)rec$).shouldRenderEngine(), true);
    protected final AbstractVehicleRenderer.PropertyFunction<T, Boolean> hasEngineProperty = new AbstractVehicleRenderer.PropertyFunction<T, Boolean>(rec$ -> ((PoweredVehicleEntity)rec$).hasEngine(), true);
    protected final AbstractVehicleRenderer.PropertyFunction<T, Boolean> renderFuelPortProperty = new AbstractVehicleRenderer.PropertyFunction<T, Boolean>(rec$ -> ((PoweredVehicleEntity)rec$).shouldRenderFuelPort(), true);
    protected final AbstractVehicleRenderer.PropertyFunction<T, Boolean> requiresFuelProperty = new AbstractVehicleRenderer.PropertyFunction<T, Boolean>(rec$ -> ((PoweredVehicleEntity)rec$).requiresFuel(), true);
    protected final AbstractVehicleRenderer.PropertyFunction<T, ItemStack> engineStackProperty = new AbstractVehicleRenderer.PropertyFunction<T, ItemStack>(rec$ -> ((PoweredVehicleEntity)rec$).getEngineStack(), ItemStack.EMPTY);
    protected final AbstractVehicleRenderer.PropertyFunction<T, ItemStack> wheelStackProperty = new AbstractVehicleRenderer.PropertyFunction<T, ItemStack>(rec$ -> ((PoweredVehicleEntity)rec$).getWheelStack(), ItemStack.EMPTY);

    public AbstractPoweredRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    public void setRenderEngine(boolean renderEngine) {
        this.renderEngineProperty.setDefaultValue(renderEngine);
    }

    public void setEngineStack(ItemStack engine) {
        this.engineStackProperty.setDefaultValue(engine);
    }

    public void setRenderFuelPort(boolean renderFuelPort) {
        this.renderFuelPortProperty.setDefaultValue(renderFuelPort);
    }

    public void setRequiresFuel(boolean requiresFuel) {
        this.requiresFuelProperty.setDefaultValue(requiresFuel);
    }

    public void setWheelStack(ItemStack wheel) {
        this.wheelStackProperty.setDefaultValue(wheel);
    }

    protected void renderEngine(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light) {
        ItemStack engine;
        if (this.renderEngineProperty.get(vehicle).booleanValue() && this.hasEngineProperty.get(vehicle).booleanValue() && !(engine = this.engineStackProperty.get(vehicle)).isEmpty()) {
            VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
            BakedModel engineModel = RenderUtil.getModel(this.engineStackProperty.get(vehicle));
            this.renderEngine((PoweredVehicleEntity)vehicle, properties.getEnginePosition(), engineModel, matrixStack, renderTypeBuffer, light);
        }
    }

    protected void renderFuelPort(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light) {
        if (vehicle != null && ((PoweredVehicleEntity)vehicle).shouldRenderFuelPort() && ((PoweredVehicleEntity)vehicle).requiresFuel()) {
            VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
            PoweredVehicleEntity.FuelPortType fuelPortType = ((PoweredVehicleEntity)vehicle).getFuelPortType();
            EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
            if (result != null && result.getType() == HitResult.Type.ENTITY && result.getEntity() == vehicle && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING)) {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getOpenModel().getModel(), matrixStack, renderTypeBuffer, ((VehicleEntity)vehicle).getColor(), light, OverlayTexture.NO_OVERLAY);
                if (this.shouldRenderFuelLid()) {
                    // empty if block
                }
                ((PoweredVehicleEntity)vehicle).playFuelPortOpenSound();
            } else {
                this.renderPart(properties.getFuelPortPosition(), fuelPortType.getClosedModel().getModel(), matrixStack, renderTypeBuffer, ((VehicleEntity)vehicle).getColor(), light, OverlayTexture.NO_OVERLAY);
                ((PoweredVehicleEntity)vehicle).playFuelPortCloseSound();
            }
        }
    }

    protected void renderKeyPort(@Nullable T vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light) {
        if (vehicle != null && ((PoweredVehicleEntity)vehicle).isKeyNeeded()) {
            VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
            this.renderPart(properties.getKeyPortPosition(), this.getKeyHoleModel().getModel(), matrixStack, renderTypeBuffer, ((VehicleEntity)vehicle).getColor(), light, OverlayTexture.NO_OVERLAY);
            if (!((PoweredVehicleEntity)vehicle).getKeyStack().isEmpty()) {
                this.renderKey(properties.getKeyPosition(), ((PoweredVehicleEntity)vehicle).getKeyStack(), RenderUtil.getModel(((PoweredVehicleEntity)vehicle).getKeyStack()), matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            }
        }
    }
}

