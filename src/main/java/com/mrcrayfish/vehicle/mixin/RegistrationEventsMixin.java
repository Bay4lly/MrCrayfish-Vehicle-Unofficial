/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.internal.RegistrationEvents
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.mrcrayfish.vehicle.mixin;

import net.neoforged.neoforge.internal.RegistrationEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RegistrationEvents.class}, remap=false)
public class RegistrationEventsMixin {
    private static boolean vehicle$initCalled = false;

    @Inject(method={"init"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void onInit(CallbackInfo ci) {
        if (vehicle$initCalled) {
            ci.cancel();
            return;
        }
        vehicle$initCalled = true;
    }
}

