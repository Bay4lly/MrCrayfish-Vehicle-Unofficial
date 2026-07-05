package com.mrcrayfish.vehicle.mixin;

import net.neoforged.neoforge.internal.RegistrationEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Guards against RegistrationEvents.init() being called twice when Framework
 * triggers a second resource reload during startup.
 */
@Mixin(value = RegistrationEvents.class, remap = false)
public class RegistrationEventsMixin
{
    private static boolean vehicle$initCalled = false;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onInit(CallbackInfo ci)
    {
        if(vehicle$initCalled)
        {
            ci.cancel();
            return;
        }
        vehicle$initCalled = true;
    }
}
