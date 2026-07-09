/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.fml.loading.FMLLoader
 *  net.neoforged.neoforge.client.event.InputEvent$Key
 *  net.neoforged.neoforge.registries.DeferredHolder
 */
package com.mrcrayfish.vehicle.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.Wheel;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid="vehicle", value={Dist.CLIENT}, bus=EventBusSubscriber.Bus.MOD)
public class VehicleProperties {
    private static final double WHEEL_RADIUS = 8.0;
    private static final DecimalFormat FORMAT;
    private static final Gson GSON;
    private static final Map<ResourceLocation, VehicleProperties> ID_TO_PROPERTIES;
    private final float axleOffset;
    private final float wheelOffset;
    private final Vec3 heldOffset;
    private final Vec3 towBarPosition;
    private final Vec3 trailerOffset;
    private final boolean canChangeWheels;
    private final List<Wheel> wheels;
    private final PartPosition bodyPosition;
    private final PartPosition enginePosition;
    private final PartPosition fuelPortPosition;
    private final PartPosition keyPortPosition;
    private final PartPosition keyPosition;
    private final PartPosition displayPosition;
    private final Vec3 frontAxelVec;
    private final Vec3 rearAxelVec;
    private final List<Seat> seats;
    private final IEngineType engineType;
    private final boolean colored;

    private VehicleProperties(float axleOffset, float wheelOffset, Vec3 heldOffset, Vec3 towBarPosition, Vec3 trailerOffset, boolean canChangeWheels, List<Wheel> wheels, PartPosition bodyPosition, PartPosition enginePosition, PartPosition fuelPortPosition, PartPosition keyPortPosition, PartPosition keyPosition, PartPosition displayPosition, Vec3 frontAxelVec, Vec3 rearAxelVec, List<Seat> seats, IEngineType engineType, boolean colored) {
        this.axleOffset = axleOffset;
        this.wheelOffset = wheelOffset;
        this.heldOffset = heldOffset;
        this.towBarPosition = towBarPosition;
        this.trailerOffset = trailerOffset;
        this.canChangeWheels = canChangeWheels;
        this.wheels = wheels;
        this.bodyPosition = bodyPosition;
        this.enginePosition = enginePosition;
        this.fuelPortPosition = fuelPortPosition;
        this.keyPortPosition = keyPortPosition;
        this.keyPosition = keyPosition;
        this.displayPosition = displayPosition;
        this.frontAxelVec = frontAxelVec;
        this.rearAxelVec = rearAxelVec;
        this.seats = seats;
        this.engineType = engineType;
        this.colored = colored;
    }

    public float getAxleOffset() {
        return this.axleOffset;
    }

    public float getWheelOffset() {
        return this.wheelOffset;
    }

    public Vec3 getHeldOffset() {
        return this.heldOffset;
    }

    public Vec3 getTowBarPosition() {
        return this.towBarPosition;
    }

    public Vec3 getTrailerOffset() {
        return this.trailerOffset;
    }

    public List<Wheel> getWheels() {
        return this.wheels;
    }

    @Nullable
    public Wheel getFirstFrontWheel() {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
    }

    @Nullable
    public Wheel getFirstRearWheel() {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.REAR).findFirst().orElse(null);
    }

    public PartPosition getBodyPosition() {
        return this.bodyPosition;
    }

    public PartPosition getEnginePosition() {
        return this.enginePosition;
    }

    public PartPosition getFuelPortPosition() {
        return this.fuelPortPosition;
    }

    public PartPosition getKeyPortPosition() {
        return this.keyPortPosition;
    }

    public PartPosition getKeyPosition() {
        return this.keyPosition;
    }

    public PartPosition getDisplayPosition() {
        return this.displayPosition;
    }

    @Nullable
    public Vec3 getFrontAxelVec() {
        return this.frontAxelVec;
    }

    @Nullable
    public Vec3 getRearAxelVec() {
        return this.rearAxelVec;
    }

    public List<Seat> getSeats() {
        return ImmutableList.copyOf(this.seats);
    }

    public IEngineType getEngineType() {
        return this.engineType;
    }

    public boolean canChangeWheels() {
        return this.canChangeWheels;
    }

    public boolean isColored() {
        return this.colored;
    }

    public static void loadProperties() {
        for (DeferredHolder<EntityType<?>, EntityType<? extends VehicleEntity>> deferredHolder : VehicleRegistry.getRegisteredVehicleTypes()) {
            ID_TO_PROPERTIES.computeIfAbsent(BuiltInRegistries.ENTITY_TYPE.getKey(deferredHolder.get()), VehicleProperties::loadProperties);
        }
    }

    private static VehicleProperties loadProperties(ResourceLocation id) {
        String resource = String.format("/assets/%s/vehicles/%s.json", id.getNamespace(), id.getPath());
        InputStream is = VehicleProperties.class.getResourceAsStream(resource);
        if (is == null) {
            VehicleMod.LOGGER.error("Missing vehicle properties file: " + resource);
            return null;
        }
        try {
            VehicleProperties vehicleProperties = (VehicleProperties)GSON.fromJson((Reader)new InputStreamReader(is, StandardCharsets.UTF_8), VehicleProperties.class);
            is.close();
            return vehicleProperties;
        } catch (JsonParseException | IOException e) {
            VehicleMod.LOGGER.error("Couldn't load vehicles properties: " + resource, e);
        }
        return null;
    }

    public static VehicleProperties get(EntityType<?> entityType) {
        return VehicleProperties.get(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    public static VehicleProperties get(ResourceLocation id) {
        VehicleProperties properties = ID_TO_PROPERTIES.get(id);
        if (properties == null) {
            throw new IllegalArgumentException("No vehicle properties registered for " + String.valueOf(id));
        }
        return properties;
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void onKeyInput(InputEvent.Key event) {
        if (FMLLoader.isProduction()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() != null) {
            return;
        }
        if (event.getAction() != 1) {
            return;
        }
        if (event.getKey() == 93) {
            for (DeferredHolder<EntityType<?>, EntityType<? extends VehicleEntity>> deferredHolder : VehicleRegistry.getRegisteredVehicleTypes()) {
                ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(deferredHolder.get());
                ID_TO_PROPERTIES.put(id, VehicleProperties.loadProperties(id));
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    static {
        GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, (Object)new Serializer()).create();
        ID_TO_PROPERTIES = new HashMap<ResourceLocation, VehicleProperties>();
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator('.');
        FORMAT = new DecimalFormat("#.###", s);
    }

    public static class Builder {
        private float axleOffset;
        private float wheelOffset;
        private Vec3 heldOffset = Vec3.ZERO;
        private Vec3 towBarPosition = Vec3.ZERO;
        private Vec3 trailerOffset = Vec3.ZERO;
        private boolean canChangeWheels = false;
        private List<Wheel> wheels = new ArrayList<Wheel>();
        private PartPosition bodyPosition = PartPosition.DEFAULT;
        private PartPosition enginePosition;
        private PartPosition fuelPortPosition;
        private PartPosition keyPortPosition;
        private PartPosition keyPosition;
        private PartPosition displayPosition = PartPosition.DEFAULT;
        private Vec3 frontAxelVec;
        private Vec3 rearAxelVec;
        private List<Seat> seats = new ArrayList<Seat>();
        private IEngineType engineType = EngineType.NONE;
        private boolean colored;

        public Builder setAxleOffset(float axleOffset) {
            this.axleOffset = axleOffset;
            return this;
        }

        public Builder setHeldOffset(double x, double y, double z) {
            this.heldOffset = new Vec3(x, y, z);
            return this;
        }

        public Builder setHeldOffset(Vec3 vec) {
            this.heldOffset = vec;
            return this;
        }

        public Builder setTowBarPosition(double x, double y, double z) {
            this.towBarPosition = new Vec3(x, y, z);
            return this;
        }

        public Builder setTowBarPosition(Vec3 vec) {
            this.towBarPosition = vec;
            return this;
        }

        public Builder setTrailerOffset(double x, double y, double z) {
            this.trailerOffset = new Vec3(x, y, z);
            return this;
        }

        public Builder setTrailerOffset(Vec3 vec) {
            this.trailerOffset = vec;
            return this;
        }

        public Builder setCanChangeWheels(boolean canChangeWheels) {
            this.canChangeWheels = canChangeWheels;
            return this;
        }

        public Builder addWheel(Wheel.Builder builder) {
            this.wheels.add(builder.build());
            return this;
        }

        public Builder setBodyPosition(PartPosition bodyPosition) {
            this.bodyPosition = bodyPosition;
            return this;
        }

        public Builder setEnginePosition(PartPosition enginePosition) {
            this.enginePosition = enginePosition;
            return this;
        }

        public Builder setFuelPortPosition(PartPosition fuelPortPosition) {
            this.fuelPortPosition = fuelPortPosition;
            return this;
        }

        public Builder setKeyPortPosition(PartPosition keyPortPosition) {
            this.keyPortPosition = keyPortPosition;
            return this;
        }

        public Builder setKeyPosition(PartPosition keyPosition) {
            this.keyPosition = keyPosition;
            return this;
        }

        public Builder setDisplayPosition(PartPosition displayPosition) {
            this.displayPosition = displayPosition;
            return this;
        }

        public Builder setFrontAxleOffset(double offset) {
            this.frontAxelVec = new Vec3(0.0, 0.0, offset);
            return this;
        }

        public Builder setRearAxleOffset(double offset) {
            this.rearAxelVec = new Vec3(0.0, 0.0, offset);
            return this;
        }

        public Builder addSeat(Seat seat) {
            this.seats.add(seat);
            return this;
        }

        public Builder setEngineType(IEngineType engineType) {
            this.engineType = engineType;
            return this;
        }

        public Builder setColored(boolean colored) {
            this.colored = colored;
            return this;
        }

        public VehicleProperties build(boolean scaleWheels) {
            this.validate();
            this.calculateWheelOffset();
            List<Wheel> wheels = scaleWheels ? this.generateScaledWheels() : this.wheels;
            return new VehicleProperties(this.axleOffset, this.wheelOffset, this.heldOffset, this.towBarPosition, this.trailerOffset, this.canChangeWheels, wheels, this.bodyPosition, this.enginePosition, this.fuelPortPosition, this.keyPortPosition, this.keyPosition, this.displayPosition, this.frontAxelVec, this.rearAxelVec, this.seats, this.engineType, this.colored);
        }

        private void validate() {
            if (this.seats.stream().filter(Seat::isDriverSeat).count() > 1L) {
                throw new RuntimeException("Unable to build vehicles properties. The maximum amount of drivers seats is one but tried to add more.");
            }
        }

        private List<Wheel> generateScaledWheels() {
            List<Wheel> copy = this.wheels.stream().map(Wheel::copy).collect(Collectors.toList());
            copy.stream().filter(Wheel::isAutoScale).forEach(wheel -> {
                double scale = (double)((this.wheelOffset + wheel.getOffsetY()) * 2.0f) / 8.0;
                wheel.updateScale(scale);
            });
            return copy;
        }

        private void calculateWheelOffset() {
            this.wheels.stream().filter(wheel -> !wheel.isAutoScale()).max((w1, w2) -> (int)(this.getLowestSittingPosition((Wheel)w1) - this.getLowestSittingPosition((Wheel)w2))).ifPresent(wheel -> {
                this.wheelOffset = this.getLowestSittingPosition((Wheel)wheel);
            });
        }

        private float getLowestSittingPosition(Wheel wheel) {
            double radius = 8.0 * (double)wheel.getScaleY();
            double lowestPosition = radius / 2.0;
            return (float)(lowestPosition -= (double)wheel.getOffsetY());
        }
    }

    public static class Serializer
    implements JsonDeserializer<VehicleProperties>,
    JsonSerializer<VehicleProperties> {
        public JsonElement serialize(VehicleProperties src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            JsonObject general = new JsonObject();
            if (src.engineType != EngineType.NONE) {
                general.addProperty("engineType", src.engineType.getId().toString());
            }
            if (src.colored) {
                general.addProperty("canBeColored", Boolean.valueOf(true));
            }
            if (src.canChangeWheels) {
                general.addProperty("canChangeWheels", Boolean.valueOf(true));
            }
            if (general.size() > 0) {
                object.add("general", (JsonElement)general);
            }
            JsonObject axles = new JsonObject();
            if (src.axleOffset != 0.0f) {
                axles.addProperty("offsetToGround", (Number)Float.valueOf(src.axleOffset));
            }
            if (src.frontAxelVec != null) {
                axles.addProperty("lengthToFront", (Number)src.frontAxelVec.z);
            }
            if (src.rearAxelVec != null) {
                axles.addProperty("lengthToRear", (Number)src.rearAxelVec.z);
            }
            if (axles.size() > 0) {
                object.add("axles", (JsonElement)axles);
            }
            JsonObject display = new JsonObject();
            this.addVec3Property(display, "held", src.heldOffset);
            this.addVec3Property(display, "trailer", src.trailerOffset);
            this.addPartPositionProperty(display, "gui", src.displayPosition);
            if (display.size() > 0) {
                object.add("display", (JsonElement)display);
            }
            JsonObject position = new JsonObject();
            this.addPartPositionProperty(position, "body", src.bodyPosition);
            this.addPartPositionProperty(position, "engine", src.enginePosition);
            this.addPartPositionProperty(position, "fuelPort", src.fuelPortPosition);
            this.addPartPositionProperty(position, "keyPort", src.keyPortPosition);
            this.addVec3Property(position, "towBar", src.towBarPosition);
            if (position.size() > 0) {
                object.add("position", (JsonElement)position);
            }
            this.writeWheels(src, object);
            this.writeSeats(src, object);
            return object;
        }

        public VehicleProperties deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = GsonHelper.convertToJsonObject((JsonElement)element, (String)"vehicle property");
            Builder builder = VehicleProperties.builder();
            JsonObject general = GsonHelper.getAsJsonObject((JsonObject)object, (String)"general", (JsonObject)new JsonObject());
            builder.setEngineType(this.getAsEngineType(general, "engineType", EngineType.NONE));
            builder.setColored(GsonHelper.getAsBoolean((JsonObject)general, (String)"canBeColored", (boolean)false));
            builder.setCanChangeWheels(GsonHelper.getAsBoolean((JsonObject)general, (String)"canChangeWheels", (boolean)false));
            JsonObject axles = GsonHelper.getAsJsonObject((JsonObject)object, (String)"axles", (JsonObject)new JsonObject());
            builder.setAxleOffset(GsonHelper.getAsFloat((JsonObject)axles, (String)"offsetToGround", (float)0.0f));
            if (axles.has("lengthToFront")) {
                builder.setFrontAxleOffset(GsonHelper.getAsFloat((JsonObject)axles, (String)"lengthToFront", (float)0.0f));
            }
            if (axles.has("lengthToRear")) {
                builder.setRearAxleOffset(GsonHelper.getAsFloat((JsonObject)axles, (String)"lengthToRear", (float)0.0f));
            }
            JsonObject display = GsonHelper.getAsJsonObject((JsonObject)object, (String)"display", (JsonObject)new JsonObject());
            builder.setHeldOffset(this.getAsVec3(display, "held", Vec3.ZERO));
            builder.setTrailerOffset(this.getAsVec3(display, "trailer", Vec3.ZERO));
            this.callIfNonNull(builder::setDisplayPosition, this.getAsPartPosition(display, "gui"));
            JsonObject positions = GsonHelper.getAsJsonObject((JsonObject)object, (String)"position", (JsonObject)new JsonObject());
            this.callIfNonNull(builder::setBodyPosition, this.getAsPartPosition(positions, "body"));
            this.callIfNonNull(builder::setEnginePosition, this.getAsPartPosition(positions, "engine"));
            this.callIfNonNull(builder::setFuelPortPosition, this.getAsPartPosition(positions, "fuelPort"));
            this.callIfNonNull(builder::setKeyPortPosition, this.getAsPartPosition(positions, "keyPort"));
            builder.setTowBarPosition(this.getAsVec3(positions, "towBar", Vec3.ZERO));
            this.readWheels(builder, object);
            this.readSeats(builder, object);
            return builder.build(true);
        }

        private IEngineType getAsEngineType(JsonObject object, String memberName, IEngineType defaultValue) {
            String rawId = GsonHelper.getAsString((JsonObject)object, (String)memberName, (String)"");
            if (!rawId.isEmpty()) {
                ResourceLocation id = ResourceLocation.parse((String)rawId);
                IEngineType type = VehicleRegistry.getEngineTypeFromId(id);
                return type != null ? type : defaultValue;
            }
            return defaultValue;
        }

        private Vec3 getAsVec3(JsonObject object, String memberName, Vec3 defaultValue) {
            if (object.has(memberName)) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray((JsonObject)object, (String)memberName);
                if (jsonArray.size() != 3) {
                    throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonArray.size());
                }
                double x = GsonHelper.convertToFloat((JsonElement)jsonArray.get(0), (String)(memberName + "[0]"));
                double y = GsonHelper.convertToFloat((JsonElement)jsonArray.get(1), (String)(memberName + "[1]"));
                double z = GsonHelper.convertToFloat((JsonElement)jsonArray.get(2), (String)(memberName + "[2]"));
                return new Vec3(x, y, z);
            }
            return defaultValue;
        }

        private void readWheels(Builder builder, JsonObject object) {
            if (object.has("wheels")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray((JsonObject)object, (String)"wheels");
                for (JsonElement element : jsonArray) {
                    JsonObject wheelObject = element.getAsJsonObject();
                    Vec3 offset = this.getAsVec3(wheelObject, "offset", Vec3.ZERO);
                    Vec3 scale = this.getAsVec3(wheelObject, "scale", Vec3.ZERO);
                    Wheel.Side side = this.getAsEnum(wheelObject, "side", Wheel.Side.class, Wheel.Side.NONE);
                    Wheel.Position position = this.getAsEnum(wheelObject, "position", Wheel.Position.class, Wheel.Position.NONE);
                    boolean autoScale = GsonHelper.getAsBoolean((JsonObject)wheelObject, (String)"autoScale", (boolean)false);
                    boolean particles = GsonHelper.getAsBoolean((JsonObject)wheelObject, (String)"particles", (boolean)false);
                    boolean render = GsonHelper.getAsBoolean((JsonObject)wheelObject, (String)"render", (boolean)true);
                    builder.addWheel(Wheel.builder().setSide(side).setPosition(position).setOffset(offset.x, offset.y, offset.z).setScale(scale.x, scale.y, scale.z).setAutoScale(autoScale).setParticles(particles).setRender(render));
                }
            }
        }

        private void writeWheels(VehicleProperties properties, JsonObject object) {
            if (properties.getWheels().size() > 0) {
                JsonArray wheels = new JsonArray();
                for (Wheel wheel : properties.getWheels()) {
                    JsonObject wheelObject = new JsonObject();
                    wheelObject.addProperty("side", wheel.getSide().name().toLowerCase(Locale.ENGLISH));
                    wheelObject.addProperty("position", wheel.getPosition().name().toLowerCase(Locale.ENGLISH));
                    this.addVec3Property(wheelObject, "offset", wheel.getOffset());
                    this.addVec3Property(wheelObject, "scale", wheel.getScale());
                    if (wheel.isAutoScale()) {
                        wheelObject.addProperty("autoScale", Boolean.valueOf(wheel.isAutoScale()));
                    }
                    if (wheel.shouldSpawnParticles()) {
                        wheelObject.addProperty("particles", Boolean.valueOf(wheel.shouldSpawnParticles()));
                    }
                    if (!wheel.shouldRender()) {
                        wheelObject.addProperty("render", Boolean.valueOf(wheel.shouldRender()));
                    }
                    wheels.add((JsonElement)wheelObject);
                }
                object.add("wheels", (JsonElement)wheels);
            }
        }

        private void readSeats(Builder builder, JsonObject object) {
            if (object.has("seats")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray((JsonObject)object, (String)"seats");
                for (JsonElement element : jsonArray) {
                    JsonObject seatObject = element.getAsJsonObject();
                    Vec3 position = this.getAsVec3(seatObject, "position", Vec3.ZERO);
                    boolean driver = GsonHelper.getAsBoolean((JsonObject)seatObject, (String)"driver", (boolean)false);
                    float yawOffset = GsonHelper.getAsFloat((JsonObject)seatObject, (String)"yawOffset", (float)0.0f);
                    builder.addSeat(new Seat(position, driver, yawOffset));
                }
            }
        }

        private void writeSeats(VehicleProperties properties, JsonObject object) {
            if (properties.getSeats().size() > 0) {
                JsonArray seats = new JsonArray();
                for (Seat seat : properties.getSeats()) {
                    JsonObject seatObject = new JsonObject();
                    this.addVec3Property(seatObject, "position", seat.getPosition());
                    if (seat.isDriverSeat()) {
                        seatObject.addProperty("driver", Boolean.valueOf(seat.isDriverSeat()));
                    }
                    if (seat.getYawOffset() != 0.0f) {
                        seatObject.addProperty("yawOffset", (Number)Float.valueOf(seat.getYawOffset()));
                    }
                    seats.add((JsonElement)seatObject);
                }
                object.add("seats", (JsonElement)seats);
            }
        }

        private <T extends Enum> T getAsEnum(JsonObject object, String memberName, Class<T> enumClass, T defaultValue) {
            if (object.has(memberName)) {
                String enumString = GsonHelper.getAsString((JsonObject)object, (String)memberName);
                return (T)Stream.of((Enum[])enumClass.getEnumConstants()).filter(side -> side.name().equalsIgnoreCase(enumString)).findFirst().orElse(defaultValue);
            }
            return defaultValue;
        }

        @Nullable
        private PartPosition getAsPartPosition(JsonObject object, String memberName) {
            if (object.has(memberName)) {
                JsonObject partPositionObject = object.getAsJsonObject(memberName);
                Vec3 translate = this.getAsVec3(partPositionObject, "translate", Vec3.ZERO);
                Vec3 rotation = this.getAsVec3(partPositionObject, "rotation", Vec3.ZERO);
                double scale = GsonHelper.getAsFloat((JsonObject)partPositionObject, (String)"scale", (float)1.0f);
                return new PartPosition(translate.x, translate.y, translate.z, rotation.x, rotation.y, rotation.z, scale);
            }
            return null;
        }

        private <T> void callIfNonNull(Consumer<T> consumer, @Nullable T value) {
            if (value != null) {
                consumer.accept(value);
            }
        }

        private void addVec3Property(JsonObject parent, String memberName, Vec3 vec) {
            if (vec != null && !vec.equals((Object)Vec3.ZERO)) {
                JsonArray array = new JsonArray();
                array.add((Number)Double.parseDouble(FORMAT.format(vec.x)));
                array.add((Number)Double.parseDouble(FORMAT.format(vec.y)));
                array.add((Number)Double.parseDouble(FORMAT.format(vec.z)));
                parent.add(memberName, (JsonElement)array);
            }
        }

        private void addPartPositionProperty(JsonObject parent, String memberName, PartPosition position) {
            if (position != null && position != PartPosition.DEFAULT) {
                JsonObject partPositionObject = new JsonObject();
                this.addVec3Property(partPositionObject, "translate", position.getTranslate());
                this.addVec3Property(partPositionObject, "rotation", position.getRotation());
                if (position.getScale() != 1.0) {
                    partPositionObject.addProperty("scale", (Number)Double.parseDouble(FORMAT.format(position.getScale())));
                }
                if (partPositionObject.size() > 0) {
                    parent.add(memberName, (JsonElement)partPositionObject);
                }
            }
        }
    }
}

