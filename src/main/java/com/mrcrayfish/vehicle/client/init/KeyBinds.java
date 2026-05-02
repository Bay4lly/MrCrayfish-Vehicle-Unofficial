package com.mrcrayfish.vehicle.client.init;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class KeyBinds
{
    public static final IKeyConflictContext RIDING_VEHICLE = new IKeyConflictContext()
    {
        @Override
        public boolean isActive()
        {
            Player player = Minecraft.getInstance().player;
            if(player != null && player.getVehicle() instanceof VehicleEntity)
            {
                return KeyConflictContext.IN_GAME.isActive();
            }
            return false;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return other == this;
        }
    };

    public static final KeyMapping KEY_HORN = new KeyMapping("key.vehicle.horn", GLFW.GLFW_KEY_H, "key.categories.vehicle");
    public static final KeyMapping KEY_CYCLE_SEATS = new KeyMapping("key.vehicle.cycle_seats", GLFW.GLFW_KEY_C, "key.categories.vehicle");

    static
    {
        KEY_HORN.setKeyConflictContext(RIDING_VEHICLE);
        KEY_CYCLE_SEATS.setKeyConflictContext(RIDING_VEHICLE);
    }

    @SubscribeEvent
    public static void registerKeyBinds(RegisterKeyMappingsEvent event) {
        event.register(KeyBinds.KEY_HORN);
        event.register(KeyBinds.KEY_CYCLE_SEATS);
    }
}
