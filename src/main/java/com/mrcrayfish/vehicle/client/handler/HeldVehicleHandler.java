package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.client.render.layer.LayerHeldVehicle;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleHandler
{
    private static boolean setupExtraLayers = false;

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        if(!setupExtraLayers)
        {
            Map<String, EntityRenderer<? extends Player>> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
            this.patchPlayerRender(skinMap.get("default"));
            this.patchPlayerRender(skinMap.get("slim"));
            setupExtraLayers = true;
        }
    }

    private void patchPlayerRender(EntityRenderer<? extends Player> player)
    {
        if(player instanceof LivingEntityRenderer<?,?>)
        {
            var renderer = (LivingEntityRenderer<Player, EntityModel<Player>>) player;

            if(!renderer.layers.isEmpty())
            {
                renderer.layers.add(new LayerHeldVehicle(renderer));
            }
        }
    }

    public static final Map<UUID, AnimationCounter> idToCounter = new HashMap<>();

    public static void onSetupAngles(Player player, PlayerModel<Player> model, float partialTick) // FIXME
    {
        boolean holdingVehicle = HeldVehicleDataHandler.isHoldingVehicle(player);
        if(holdingVehicle && !idToCounter.containsKey(player.getUUID()))
        {
            idToCounter.put(player.getUUID(), new AnimationCounter(40));
        }
        else if(idToCounter.containsKey(player.getUUID()))
        {
            if(idToCounter.get(player.getUUID()).getProgress(partialTick) == 0F)
            {
                idToCounter.remove(player.getUUID());
                return;
            }
            if(!holdingVehicle)
            {
                AnimationCounter counter = idToCounter.get(player.getUUID());
                player.yBodyRot = player.getYHeadRot() - (player.getYHeadRot() - player.yBodyRotO) * counter.getProgress(partialTick);
            }
        }
        else
        {
            return;
        }

        AnimationCounter counter = idToCounter.get(player.getUUID());
        counter.update(holdingVehicle);
        float progress = counter.getProgress(partialTick);
        model.rightArm.xRot = (float) Math.toRadians(-180F * progress);
        model.rightArm.zRot = (float) Math.toRadians(-5F * progress);
        model.rightArm.y = (player.isCrouching() ? 3.0F : -0.5F) * progress;
        model.leftArm.xRot = (float) Math.toRadians(-180F * progress);
        model.leftArm.zRot = (float) Math.toRadians(5F * progress);
        model.leftArm.y = (player.isCrouching() ? 3.0F : -0.5F) * progress;
    }

    public static class AnimationCounter
    {
        private final int MAX_COUNT;
        private int prevCount;
        private int currentCount;

        public AnimationCounter(int maxCount)
        {
            this.MAX_COUNT = maxCount;
        }

        public int update(boolean increment)
        {
            prevCount = currentCount;
            if(increment)
            {
                if(currentCount < MAX_COUNT)
                {
                    currentCount++;
                }
            }
            else
            {
                if(currentCount > 0)
                {
                    currentCount = Math.max(0, currentCount - 2);
                }
            }
            return currentCount;
        }

        public int getMaxCount()
        {
            return MAX_COUNT;
        }

        public int getCurrentCount()
        {
            return currentCount;
        }

        public float getProgress(float partialTicks)
        {
            return (prevCount + (currentCount - prevCount) * partialTicks) / (float) MAX_COUNT;
        }
    }
}
