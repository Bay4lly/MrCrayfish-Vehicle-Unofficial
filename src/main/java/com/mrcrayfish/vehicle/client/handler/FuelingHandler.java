package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class FuelingHandler
{
    private int fuelTickCounter;
    private boolean fueling;
    private boolean renderNozzle;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        if(this.fueling)
        {
            this.fuelTickCounter++;
        }

        EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
        if(result != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING))
        {
            if(this.fuelTickCounter % 20 == 0)
            {
                Vec3 vec = result.getLocation();
                player.level().playSound(player, vec.x(), vec.y(), vec.z(), ModSounds.ITEM_JERRY_CAN_LIQUID_GLUG.get(), SoundSource.PLAYERS, 0.6F, 1.0F + 0.1F * player.level().random.nextFloat());
            }
            if(!this.fueling)
            {
                this.fuelTickCounter = 0;
                this.fueling = true;
            }
        }
        else
        {
            this.fueling = false;
        }
    }

    static void applyFuelingPose(Player player, PlayerModel<?> model)
    {
        boolean rightInteractionHanded = player.getMainArm() == HumanoidArm.RIGHT;
        if(rightInteractionHanded)
        {
            model.rightArm.xRot = (float) Math.toRadians(-20F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.rightArm.zRot = (float) Math.toRadians(0F);
        }
        else
        {
            model.leftArm.xRot = (float) Math.toRadians(-20F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.zRot = (float) Math.toRadians(0F);
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
        /*if(event.getHand() == InteractionHand.OFF_HAND && this.fuelingHandOffset > -1)
        {
            double offset = Math.sin((this.fuelTickCounter + minecraft.getRenderPartialTicks()) / 3.0) * 0.1;
            matrixStack.rotate(Axis.XP.rotationDegrees(25F));
            matrixStack.translate(0, -0.35 - this.fuelingHandOffset, 0.2);
        }*/

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        PoseStack matrixStack = event.getPoseStack();
        EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
        if(result != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING) && event.getHand() == EntityRayTracer.instance().getContinuousInteractionInteractionHand())
        {
            double offset = Math.sin((this.fuelTickCounter + minecraft.getFrameTime()) / 3.0) * 0.1;
            matrixStack.translate(0, 0.35 + offset, -0.2);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-25F));
        }

        if(ModDataKeys.GAS_PUMP.getValue(player).isPresent())
        {
            if(event.getSwingProgress() > 0) // WHY? TEST THIS TODO
            {
                this.renderNozzle = true;
            }

            if(event.getHand() == InteractionHand.MAIN_HAND && this.renderNozzle)
            {
                if(event.getSwingProgress() > 0 && event.getSwingProgress() <= 0.25) //WHAT IS THIS?
                    return;

                event.setCanceled(true);

                boolean mainInteractionHand = event.getHand() == InteractionHand.MAIN_HAND;
                HumanoidArm handSide = mainInteractionHand ? player.getMainArm() : player.getMainArm().getOpposite();
                int handOffset = handSide == HumanoidArm.RIGHT ? 1 : -1;
                MultiBufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
                int light = minecraft.getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTick());

                matrixStack.pushPose();
                matrixStack.translate(handOffset * 0.65, -0.27, -0.72);
                matrixStack.mulPose(Axis.XP.rotationDegrees(45F));
                RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY); //TODO check
                matrixStack.popPose();
            }
        }
        else
        {
            this.renderNozzle = false;
        }
    }

    public static <T extends Entity> void onModelRenderPost(T entity, EntityModel<T> model, PoseStack matrixStack)
    {
        if(!(entity instanceof Player))
            return;

        Player player = (Player) entity;

        if(!ModDataKeys.GAS_PUMP.getValue(player).isPresent())
            return;

        matrixStack.pushPose();

        if(model.young)
        {
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }

        if(player.isCrouching())
        {
            matrixStack.translate(0.0, 0.2, 0.0);
        }

        ((PlayerModel<?>) model).translateToHand(HumanoidArm.RIGHT, matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(180F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
        boolean leftInteractionHanded = player.getMainArm() == HumanoidArm.LEFT;
        matrixStack.translate((leftInteractionHanded ? -1 : 1) / 16.0, 0.125, -0.625);
        matrixStack.translate(0, -9 * 0.0625F, 5.75 * 0.0625F);

        MultiBufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, 15728880, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }
}
