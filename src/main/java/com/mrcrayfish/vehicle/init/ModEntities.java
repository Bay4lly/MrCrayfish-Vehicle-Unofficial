package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.trailer.*;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.util.VehicleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<QuadBikeEntity>> QUAD_BIKE = VehicleUtil.createEntityType(REGISTER, "quad_bike", QuadBikeEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<ATVEntity>> ATV = VehicleUtil.createEntityType(REGISTER, "atv", ATVEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<DuneBuggyEntity>> DUNE_BUGGY = VehicleUtil.createEntityType(REGISTER, "dune_buggy", DuneBuggyEntity::new, 0.75F, 0.75F);
    public static final DeferredHolder<EntityType<?>, EntityType<GoKartEntity>> GO_KART = VehicleUtil.createEntityType(REGISTER, "go_kart", GoKartEntity::new, 1.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ShoppingCartEntity>> SHOPPING_CART = VehicleUtil.createEntityType(REGISTER, "shopping_cart", ShoppingCartEntity::new, 1.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<MiniBikeEntity>> MINI_BIKE = VehicleUtil.createEntityType(REGISTER, "mini_bike", MiniBikeEntity::new, 1.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<BumperCarEntity>> BUMPER_CAR = VehicleUtil.createEntityType(REGISTER, "bumper_car", BumperCarEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<CompactHelicopterEntity>> COMPACT_HELICOPTER = VehicleUtil.createEntityType(REGISTER, "compact_helicopter", CompactHelicopterEntity::new, 2.0F, 2.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<JetSkiEntity>> JET_SKI = VehicleUtil.createEntityType(REGISTER, "jet_ski", JetSkiEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<SpeedBoatEntity>> SPEED_BOAT = VehicleUtil.createEntityType(REGISTER, "speed_boat", SpeedBoatEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<AluminumBoatEntity>> ALUMINUM_BOAT = VehicleUtil.createEntityType(REGISTER, "aluminum_boat", AluminumBoatEntity::new, 2.25F, 0.875F);
    public static final DeferredHolder<EntityType<?>, EntityType<SmartCarEntity>> SMART_CAR = VehicleUtil.createEntityType(REGISTER, "smart_car", SmartCarEntity::new, 1.85F, 1.15F);
    public static final DeferredHolder<EntityType<?>, EntityType<LawnMowerEntity>> LAWN_MOWER = VehicleUtil.createEntityType(REGISTER, "lawn_mower", LawnMowerEntity::new, 1.2F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<MopedEntity>> MOPED = VehicleUtil.createEntityType(REGISTER, "moped", MopedEntity::new, 1.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<SportsPlaneEntity>> SPORTS_PLANE = VehicleUtil.createEntityType(REGISTER, "sports_plane", SportsPlaneEntity::new, 3.0F, 1.6875F);
    public static final DeferredHolder<EntityType<?>, EntityType<GolfCartEntity>> GOLF_CART = VehicleUtil.createEntityType(REGISTER, "golf_cart", GolfCartEntity::new, 2.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<OffRoaderEntity>> OFF_ROADER = VehicleUtil.createEntityType(REGISTER, "off_roader", OffRoaderEntity::new, 2.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<TractorEntity>> TRACTOR = VehicleUtil.createEntityType(REGISTER, "tractor", TractorEntity::new, 1.5F, 1.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<MiniBusEntity>> MINI_BUS = VehicleUtil.createEntityType(REGISTER, "mini_bus", MiniBusEntity::new, 2.0F, 2.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<DirtBikeEntity>> DIRT_BIKE = VehicleUtil.createEntityType(REGISTER, "dirt_bike", DirtBikeEntity::new, 1.0F, 1.5F);

    /* Trailers */
    public static final DeferredHolder<EntityType<?>, EntityType<VehicleEntityTrailer>> VEHICLE_TRAILER = VehicleUtil.createEntityType(REGISTER, "vehicle_trailer", VehicleEntityTrailer::new, 1.5F, 0.75F);
    public static final DeferredHolder<EntityType<?>, EntityType<StorageTrailerEntity>> STORAGE_TRAILER = VehicleUtil.createEntityType(REGISTER, "storage_trailer", StorageTrailerEntity::new, 1.0F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<FluidTrailerEntity>> FLUID_TRAILER = VehicleUtil.createEntityType(REGISTER, "fluid_trailer", FluidTrailerEntity::new, 1.5F, 1.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SeederTrailerEntity>> SEEDER = VehicleUtil.createEntityType(REGISTER, "seeder", SeederTrailerEntity::new, 1.5F, 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<FertilizerTrailerEntity>> FERTILIZER = VehicleUtil.createEntityType(REGISTER, "fertilizer", FertilizerTrailerEntity::new, 1.5F, 1.0F);

    /* Special Vehicles */
    public static final DeferredHolder<EntityType<?>, EntityType<CouchEntity>> SOFA = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "couch", CouchEntity::new, 1.0F, 1.0F, true);
    public static final DeferredHolder<EntityType<?>, EntityType<BathEntity>> BATH = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "bath", BathEntity::new, 1.0F, 1.0F, false);
    public static final DeferredHolder<EntityType<?>, EntityType<SofacopterEntity>> SOFACOPTER = VehicleUtil.createModDependentEntityType(REGISTER, "cfm", "sofacopter", SofacopterEntity::new, 1.0F, 1.0F, false);

    /* Other */
    public static final DeferredHolder<EntityType<?>, EntityType<EntityJack>> JACK = registerEntity("jack", EntityJack::new, 0.0F, 0.0F);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String id, BiFunction<EntityType<T>, Level, T> function, float width, float height)
    {
        return ModEntities.REGISTER.register(id, () -> EntityType.Builder.of(function::apply, MobCategory.MISC).sized(width, height).setTrackingRange(256).setUpdateInterval(1).noSummon().fireImmune().setShouldReceiveVelocityUpdates(true).build(id));
    }
}