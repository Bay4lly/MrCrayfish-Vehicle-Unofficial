package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.vehicle.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ModDataKeys
{
    public static final SyncedDataKey<Player, Integer> TRAILER = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.INTEGER)
            .id(new ResourceLocation(Reference.MOD_ID, "trailer"))
            .defaultValueSupplier(() -> -1)
            .resetOnDeath()
            .build();

    public static final SyncedDataKey<Player, Optional<BlockPos>> GAS_PUMP = SyncedDataKey.builder(SyncedClassKey.PLAYER, com.mrcrayfish.vehicle.common.data.Serializers.OPTIONAL_BLOCK_POS)
            .id(new ResourceLocation(Reference.MOD_ID, "gas_pump"))
            .defaultValueSupplier(Optional::empty)
            .resetOnDeath()
            .build();

    public static void register()
    {
        FrameworkAPI.registerSyncedDataKey(TRAILER);
        FrameworkAPI.registerSyncedDataKey(GAS_PUMP);
    }
}
