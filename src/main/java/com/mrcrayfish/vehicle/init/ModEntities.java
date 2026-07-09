/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.FertilizerTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.FluidTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.SeederTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.VehicleEntityTrailer;
import com.mrcrayfish.vehicle.entity.vehicle.ATVEntity;
import com.mrcrayfish.vehicle.entity.vehicle.AluminumBoatEntity;
import com.mrcrayfish.vehicle.entity.vehicle.BathEntity;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.entity.vehicle.CompactHelicopterEntity;
import com.mrcrayfish.vehicle.entity.vehicle.CouchEntity;
import com.mrcrayfish.vehicle.entity.vehicle.DirtBikeEntity;
import com.mrcrayfish.vehicle.entity.vehicle.DuneBuggyEntity;
import com.mrcrayfish.vehicle.entity.vehicle.GoKartEntity;
import com.mrcrayfish.vehicle.entity.vehicle.GolfCartEntity;
import com.mrcrayfish.vehicle.entity.vehicle.JetSkiEntity;
import com.mrcrayfish.vehicle.entity.vehicle.LawnMowerEntity;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBikeEntity;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBusEntity;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.entity.vehicle.OffRoaderEntity;
import com.mrcrayfish.vehicle.entity.vehicle.QuadBikeEntity;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SmartCarEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SofacopterEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SpeedBoatEntity;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.entity.vehicle.TractorEntity;
import com.mrcrayfish.vehicle.util.VehicleUtil;
import java.util.function.BiFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.ENTITY_TYPE, (String)"vehicle");
    public static final DeferredHolder<EntityType<?>, EntityType<QuadBikeEntity>> QUAD_BIKE = VehicleUtil.createEntityType(REGISTER, "quad_bike", QuadBikeEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<ATVEntity>> ATV = VehicleUtil.createEntityType(REGISTER, "atv", ATVEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<DuneBuggyEntity>> DUNE_BUGGY = VehicleUtil.createEntityType(REGISTER, "dune_buggy", DuneBuggyEntity::new, 0.75f, 0.75f);
    public static final DeferredHolder<EntityType<?>, EntityType<GoKartEntity>> GO_KART = VehicleUtil.createEntityType(REGISTER, "go_kart", GoKartEntity::new, 1.5f, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<ShoppingCartEntity>> SHOPPING_CART = VehicleUtil.createEntityType(REGISTER, "shopping_cart", ShoppingCartEntity::new, 1.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<MiniBikeEntity>> MINI_BIKE = VehicleUtil.createEntityType(REGISTER, "mini_bike", MiniBikeEntity::new, 1.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<BumperCarEntity>> BUMPER_CAR = VehicleUtil.createEntityType(REGISTER, "bumper_car", BumperCarEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<CompactHelicopterEntity>> COMPACT_HELICOPTER = VehicleUtil.createEntityType(REGISTER, "compact_helicopter", CompactHelicopterEntity::new, 2.0f, 2.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<JetSkiEntity>> JET_SKI = VehicleUtil.createEntityType(REGISTER, "jet_ski", JetSkiEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<SpeedBoatEntity>> SPEED_BOAT = VehicleUtil.createEntityType(REGISTER, "speed_boat", SpeedBoatEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<AluminumBoatEntity>> ALUMINUM_BOAT = VehicleUtil.createEntityType(REGISTER, "aluminum_boat", AluminumBoatEntity::new, 2.25f, 0.875f);
    public static final DeferredHolder<EntityType<?>, EntityType<SmartCarEntity>> SMART_CAR = VehicleUtil.createEntityType(REGISTER, "smart_car", SmartCarEntity::new, 1.85f, 1.15f);
    public static final DeferredHolder<EntityType<?>, EntityType<LawnMowerEntity>> LAWN_MOWER = VehicleUtil.createEntityType(REGISTER, "lawn_mower", LawnMowerEntity::new, 1.2f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<MopedEntity>> MOPED = VehicleUtil.createEntityType(REGISTER, "moped", MopedEntity::new, 1.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<SportsPlaneEntity>> SPORTS_PLANE = VehicleUtil.createEntityType(REGISTER, "sports_plane", SportsPlaneEntity::new, 3.0f, 1.6875f);
    public static final DeferredHolder<EntityType<?>, EntityType<GolfCartEntity>> GOLF_CART = VehicleUtil.createEntityType(REGISTER, "golf_cart", GolfCartEntity::new, 2.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<OffRoaderEntity>> OFF_ROADER = VehicleUtil.createEntityType(REGISTER, "off_roader", OffRoaderEntity::new, 2.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<TractorEntity>> TRACTOR = VehicleUtil.createEntityType(REGISTER, "tractor", TractorEntity::new, 1.5f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<MiniBusEntity>> MINI_BUS = VehicleUtil.createEntityType(REGISTER, "mini_bus", MiniBusEntity::new, 2.0f, 2.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<DirtBikeEntity>> DIRT_BIKE = VehicleUtil.createEntityType(REGISTER, "dirt_bike", DirtBikeEntity::new, 1.0f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<VehicleEntityTrailer>> VEHICLE_TRAILER = VehicleUtil.createEntityType(REGISTER, "vehicle_trailer", VehicleEntityTrailer::new, 1.5f, 0.75f);
    public static final DeferredHolder<EntityType<?>, EntityType<StorageTrailerEntity>> STORAGE_TRAILER = VehicleUtil.createEntityType(REGISTER, "storage_trailer", StorageTrailerEntity::new, 1.0f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<FluidTrailerEntity>> FLUID_TRAILER = VehicleUtil.createEntityType(REGISTER, "fluid_trailer", FluidTrailerEntity::new, 1.5f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<SeederTrailerEntity>> SEEDER = VehicleUtil.createEntityType(REGISTER, "seeder", SeederTrailerEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<FertilizerTrailerEntity>> FERTILIZER = VehicleUtil.createEntityType(REGISTER, "fertilizer", FertilizerTrailerEntity::new, 1.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<CouchEntity>> SOFA = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "couch", CouchEntity::new, 1.0f, 1.0f, true);
    public static final DeferredHolder<EntityType<?>, EntityType<BathEntity>> BATH = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "bath", BathEntity::new, 1.0f, 1.0f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<SofacopterEntity>> SOFACOPTER = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "sofacopter", SofacopterEntity::new, 1.0f, 1.0f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityJack>> JACK = ModEntities.registerEntity("jack", EntityJack::new, 0.0f, 0.0f);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String id, BiFunction<EntityType<T>, Level, T> function, float width, float height) {
        return REGISTER.register(id, () -> EntityType.Builder.of(function::apply, (MobCategory)MobCategory.MISC).sized(width, height).setTrackingRange(256).setUpdateInterval(1).noSummon().fireImmune().setShouldReceiveVelocityUpdates(true).build(id));
    }
}

