/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mrcrayfish.controllable.Controllable
 *  net.minecraft.client.Minecraft
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.lwjgl.glfw.GLFW
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.vehicle.client.ClientHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(value=Dist.CLIENT)
public class ControllerHandler {
    public static boolean isRightClicking() {
        boolean isRightClicking = GLFW.glfwGetMouseButton((long)Minecraft.getInstance().getWindow().getWindow(), (int)1) == 1;
        return isRightClicking |= ClientHandler.isControllableLoaded() && Controllable.getController() != null && Controllable.getController().getLTriggerValue() != 0.0f;
    }
}

