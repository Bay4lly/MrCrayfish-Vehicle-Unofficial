package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class SprayCanHandler
{
    private int lastSlot = -1;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if(event.phase != TickEvent.Phase.END || player == null)
            return;

        int slot = player.getInventory().selected;
        if(this.lastSlot == slot)
            return;

        this.lastSlot = slot;

        if(player.getInventory().getSelected().isEmpty())
            return;

        if(!(player.getInventory().getSelected().getItem() instanceof SprayCanItem))
            return;

        SprayCanItem sprayCan = (SprayCanItem) player.getInventory().getSelected().getItem();
        float pitch = 0.85F + 0.15F * sprayCan.getRemainingSprays(player.getInventory().getSelected());
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.ITEM_SPRAY_CAN_SHAKE.get(), pitch, 0.75F));
    }

    /**
     * Applies a pose to the player model if they are holding a spray can item
     *
     * @param player the player holding the spray can
     * @param model  the model of the player
     */
    static void applySprayCanPose(Player player, PlayerModel<?> model)
    {
        if(player.getVehicle() != null)
            return;

        boolean rightInteractionHanded = player.getMainArm() == HumanoidArm.RIGHT;
        ItemStack rightItem = rightInteractionHanded ? player.getMainHandItem() : player.getOffhandItem();
        ItemStack leftItem = rightInteractionHanded ? player.getOffhandItem() : player.getMainHandItem();
        if(!rightItem.isEmpty() && rightItem.getItem() instanceof SprayCanItem)
        {
            copyModelAngles(model.head, model.rightArm);
            model.rightArm.xRot += Math.toRadians(-80F);
        }
        if(!leftItem.isEmpty() && leftItem.getItem() instanceof SprayCanItem)
        {
            model.leftArm.copyFrom(model.head);
            model.leftArm.xRot += Math.toRadians(-80F);
        }
    }

    /**
     * A simple helper method to copy the rotation angles of a model renderer to another
     *
     * @param source the source model renderer to get the rotations form
     * @param target the target model renderer to apply to rotations to
     */
    private static void copyModelAngles(ModelPart source, ModelPart target)
    {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
