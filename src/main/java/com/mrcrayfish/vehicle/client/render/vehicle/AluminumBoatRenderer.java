package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractBoatRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.AluminumBoatEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class AluminumBoatRenderer extends AbstractBoatRenderer<AluminumBoatEntity>
{
    private final ModelPart noWater;

    public AluminumBoatRenderer(Supplier<VehicleProperties> properties)
    {
        super(properties);

        // FIXME bake like in vanilla
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition rootPartDefinition = meshDefinition.getRoot();
        rootPartDefinition.addOrReplaceChild(
                "no_water",
                CubeListBuilder.create().texOffs(0, 0).addBox(-15F, -6F, -21F, 30F, 8F, 35F),
                PartPose.ZERO
        );
        this.noWater = rootPartDefinition.bake(128, 64);
    }

    @Override
    protected void render(@Nullable AluminumBoatEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.ALUMINUM_BOAT_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.waterMask());
        this.noWater.render(matrixStack, buffer, light, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void applyPlayerModel(AluminumBoatEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(20F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-20F);
    }

    @Override
    public void applyPlayerRender(AluminumBoatEntity entity, Player player, float partialTicks, PoseStack matrixStack)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;

            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
            matrixStack.mulPose(Axis.XP.rotationDegrees(-8F * Math.min(1.0F, currentSpeedNormal)));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 15F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
            matrixStack.translate(0, 0.1, 0); // FIXME
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.ALUMINUM_BOAT_BODY, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.ALUMINUM_BOAT.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
