package com.mrcrayfish.vehicle.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class VehicleProperties
{
    private static final double WHEEL_RADIUS = 8.0;
    private static final DecimalFormat FORMAT;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, new Serializer()).create();
    private static final Map<ResourceLocation, VehicleProperties> ID_TO_PROPERTIES = new HashMap<>();

    static
    {
        // Always use dot
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator('.');
        FORMAT = new DecimalFormat("#.###", s);
    }

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

    private VehicleProperties(float axleOffset, float wheelOffset, Vec3 heldOffset, Vec3 towBarPosition, Vec3 trailerOffset, boolean canChangeWheels, List<Wheel> wheels, PartPosition bodyPosition, PartPosition enginePosition, PartPosition fuelPortPosition, PartPosition keyPortPosition, PartPosition keyPosition, PartPosition displayPosition, Vec3 frontAxelVec, Vec3 rearAxelVec, List<Seat> seats, IEngineType engineType, boolean colored)
    {
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

    public float getAxleOffset()
    {
        return this.axleOffset;
    }

    public float getWheelOffset()
    {
        return this.wheelOffset;
    }

    public Vec3 getHeldOffset()
    {
        return this.heldOffset;
    }

    public Vec3 getTowBarPosition()
    {
        return this.towBarPosition;
    }

    public Vec3 getTrailerOffset()
    {
        return this.trailerOffset;
    }

    public List<Wheel> getWheels()
    {
        return this.wheels;
    }

    @Nullable
    public Wheel getFirstFrontWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
    }

    @Nullable
    public Wheel getFirstRearWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.REAR).findFirst().orElse(null);
    }

    public PartPosition getBodyPosition()
    {
        return this.bodyPosition;
    }

    public PartPosition getEnginePosition()
    {
        return this.enginePosition;
    }

    public PartPosition getFuelPortPosition()
    {
        return this.fuelPortPosition;
    }

    public PartPosition getKeyPortPosition()
    {
        return this.keyPortPosition;
    }

    public PartPosition getKeyPosition()
    {
        return this.keyPosition;
    }

    public PartPosition getDisplayPosition()
    {
        return this.displayPosition;
    }

    @Nullable
    public Vec3 getFrontAxelVec()
    {
        return this.frontAxelVec;
    }

    @Nullable
    public Vec3 getRearAxelVec()
    {
        return this.rearAxelVec;
    }

    public List<Seat> getSeats()
    {
        return ImmutableList.copyOf(this.seats);
    }

    public IEngineType getEngineType()
    {
        return this.engineType;
    }

    public boolean canChangeWheels()
    {
        return this.canChangeWheels;
    }

    public boolean isColored()
    {
        return this.colored;
    }

    public static void loadProperties()
    {
        for(RegistryObject<EntityType<? extends VehicleEntity>> entityType : VehicleRegistry.getRegisteredVehicleTypes())
        {
            ID_TO_PROPERTIES.computeIfAbsent(ForgeRegistries.ENTITY_TYPES.getKey(entityType.get()), VehicleProperties::loadProperties); // FIXME
        }
    }

    private static VehicleProperties loadProperties(ResourceLocation id)
    {
        String resource = String.format("/assets/%s/vehicles/%s.json", id.getNamespace(), id.getPath());
        try(InputStream is = VehicleProperties.class.getResourceAsStream(resource))
        {
            return GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), VehicleProperties.class);
        }
        catch(JsonParseException | IOException e)
        {
            VehicleMod.LOGGER.error("Couldn't load vehicles properties: " + resource, e);
        }
        catch(NullPointerException e)
        {
            VehicleMod.LOGGER.error("Missing vehicle properties file: " + resource, e);
        }
        return null;
    }

    public static VehicleProperties get(EntityType<?> entityType)
    {
        return get(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
    }

    public static VehicleProperties get(ResourceLocation id)
    {
        VehicleProperties properties = ID_TO_PROPERTIES.get(id);
        if(properties == null)
        {
            throw new IllegalArgumentException("No vehicle properties registered for " + id);
        }
        return properties;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onKeyInput(InputEvent.Key event)
    {
        if(FMLLoader.isProduction())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.getOverlay() != null)
            return;

        if(event.getAction() != GLFW.GLFW_PRESS)
            return;

        if(event.getKey() == GLFW.GLFW_KEY_RIGHT_BRACKET)
        {
            for(RegistryObject<EntityType<? extends VehicleEntity>> entityType : VehicleRegistry.getRegisteredVehicleTypes())
            {
                ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType.get());
                ID_TO_PROPERTIES.put(id, loadProperties(id)); // FIXME
            }
        }
    }

    public static class Serializer implements JsonDeserializer<VehicleProperties>, JsonSerializer<VehicleProperties>
    {
        @Override
        public JsonElement serialize(VehicleProperties src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject object = new JsonObject();

            JsonObject general = new JsonObject();
            if(src.engineType != EngineType.NONE) general.addProperty("engineType", src.engineType.getId().toString());
            if(src.colored) general.addProperty("canBeColored", true);
            if(src.canChangeWheels) general.addProperty("canChangeWheels", true);
            if(general.size() > 0) object.add("general", general);

            JsonObject axles = new JsonObject();
            if(src.axleOffset != 0) axles.addProperty("offsetToGround", src.axleOffset);
            if(src.frontAxelVec != null) axles.addProperty("lengthToFront", src.frontAxelVec.z);
            if(src.rearAxelVec != null) axles.addProperty("lengthToRear", src.rearAxelVec.z);
            if(axles.size() > 0) object.add("axles", axles);

            JsonObject display = new JsonObject();
            this.addVec3Property(display, "held", src.heldOffset);
            this.addVec3Property(display, "trailer", src.trailerOffset);
            this.addPartPositionProperty(display, "gui", src.displayPosition);
            if(display.size() > 0) object.add("display", display);

            JsonObject position = new JsonObject();
            this.addPartPositionProperty(position, "body", src.bodyPosition);
            this.addPartPositionProperty(position, "engine", src.enginePosition);
            this.addPartPositionProperty(position, "fuelPort", src.fuelPortPosition);
            this.addPartPositionProperty(position, "keyPort", src.keyPortPosition);
            this.addVec3Property(position, "towBar", src.towBarPosition);
            if(position.size() > 0) object.add("position", position);

            this.writeWheels(src, object);
            this.writeSeats(src, object);

            return object;
        }

        @Override
        public VehicleProperties deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = GsonHelper.convertToJsonObject(element, "vehicle property");
            VehicleProperties.Builder builder = VehicleProperties.builder();

            JsonObject general = GsonHelper.getAsJsonObject(object, "general", new JsonObject());
            builder.setEngineType(this.getAsEngineType(general, "engineType", EngineType.NONE));
            builder.setColored(GsonHelper.getAsBoolean(general, "canBeColored", false));
            builder.setCanChangeWheels(GsonHelper.getAsBoolean(general, "canChangeWheels", false));

            JsonObject axles = GsonHelper.getAsJsonObject(object, "axles", new JsonObject());
            builder.setAxleOffset(GsonHelper.getAsFloat(axles, "offsetToGround", 0F));
            if(axles.has("lengthToFront")) builder.setFrontAxleOffset(GsonHelper.getAsFloat(axles, "lengthToFront", 0F));
            if(axles.has("lengthToRear")) builder.setRearAxleOffset(GsonHelper.getAsFloat(axles, "lengthToRear", 0F));

            JsonObject display = GsonHelper.getAsJsonObject(object, "display", new JsonObject());
            builder.setHeldOffset(this.getAsVec3(display, "held", Vec3.ZERO));
            builder.setTrailerOffset(this.getAsVec3(display, "trailer", Vec3.ZERO));
            this.callIfNonNull(builder::setDisplayPosition, this.getAsPartPosition(display, "gui"));

            JsonObject positions = GsonHelper.getAsJsonObject(object, "position", new JsonObject());
            this.callIfNonNull(builder::setBodyPosition, this.getAsPartPosition(positions, "body"));
            this.callIfNonNull(builder::setEnginePosition, this.getAsPartPosition(positions, "engine"));
            this.callIfNonNull(builder::setFuelPortPosition, this.getAsPartPosition(positions, "fuelPort"));
            this.callIfNonNull(builder::setKeyPortPosition, this.getAsPartPosition(positions, "keyPort"));
            builder.setTowBarPosition(this.getAsVec3(positions, "towBar", Vec3.ZERO));

            this.readWheels(builder, object);
            this.readSeats(builder, object);

            return builder.build(true);
        }

        private IEngineType getAsEngineType(JsonObject object, String memberName, IEngineType defaultValue)
        {
            String rawId = GsonHelper.getAsString(object, memberName, "");
            if(!rawId.isEmpty())
            {
                ResourceLocation id = new ResourceLocation(rawId);
                IEngineType type = VehicleRegistry.getEngineTypeFromId(id);
                return type != null ? type : defaultValue;
            }
            return defaultValue;
        }

        private Vec3 getAsVec3(JsonObject object, String memberName, Vec3 defaultValue)
        {
            if(object.has(memberName))
            {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(object, memberName);
                if(jsonArray.size() != 3)
                {
                    throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonArray.size());
                }
                else
                {
                    double x = GsonHelper.convertToFloat(jsonArray.get(0), memberName + "[0]");
                    double y = GsonHelper.convertToFloat(jsonArray.get(1), memberName + "[1]");
                    double z = GsonHelper.convertToFloat(jsonArray.get(2), memberName + "[2]");
                    return new Vec3(x, y, z);
                }
            }
            return defaultValue;
        }

        private void readWheels(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("wheels"))
            {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(object, "wheels");
                for(JsonElement element : jsonArray)
                {
                    JsonObject wheelObject = element.getAsJsonObject();
                    Vec3 offset = this.getAsVec3(wheelObject, "offset", Vec3.ZERO);
                    Vec3 scale = this.getAsVec3(wheelObject, "scale", Vec3.ZERO);
                    Wheel.Side side = this.getAsEnum(wheelObject, "side", Wheel.Side.class, Wheel.Side.NONE);
                    Wheel.Position position = this.getAsEnum(wheelObject, "position", Wheel.Position.class, Wheel.Position.NONE);
                    boolean autoScale = GsonHelper.getAsBoolean(wheelObject, "autoScale", false);
                    boolean particles = GsonHelper.getAsBoolean(wheelObject, "particles", false);
                    boolean render = GsonHelper.getAsBoolean(wheelObject, "render", true);
                    builder.addWheel(Wheel.builder()
                            .setSide(side)
                            .setPosition(position)
                            .setOffset(offset.x, offset.y, offset.z)
                            .setScale(scale.x, scale.y, scale.z)
                            .setAutoScale(autoScale)
                            .setParticles(particles)
                            .setRender(render));
                }
            }
        }

        private void writeWheels(VehicleProperties properties, JsonObject object)
        {
            if(properties.getWheels().size() > 0)
            {
                JsonArray wheels = new JsonArray();
                for(Wheel wheel : properties.getWheels())
                {
                    JsonObject wheelObject = new JsonObject();
                    wheelObject.addProperty("side", wheel.getSide().name().toLowerCase(Locale.ENGLISH));
                    wheelObject.addProperty("position", wheel.getPosition().name().toLowerCase(Locale.ENGLISH));
                    this.addVec3Property(wheelObject, "offset", wheel.getOffset());
                    this.addVec3Property(wheelObject, "scale", wheel.getScale());
                    if(wheel.isAutoScale()) wheelObject.addProperty("autoScale", wheel.isAutoScale());
                    if(wheel.shouldSpawnParticles()) wheelObject.addProperty("particles", wheel.shouldSpawnParticles());
                    if(!wheel.shouldRender()) wheelObject.addProperty("render", wheel.shouldRender());
                    wheels.add(wheelObject);
                }
                object.add("wheels", wheels);
            }
        }

        private void readSeats(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("seats"))
            {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(object, "seats");
                for(JsonElement element : jsonArray)
                {
                    JsonObject seatObject = element.getAsJsonObject();
                    Vec3 position = this.getAsVec3(seatObject, "position", Vec3.ZERO);
                    boolean driver = GsonHelper.getAsBoolean(seatObject, "driver", false);
                    float yawOffset = GsonHelper.getAsFloat(seatObject, "yawOffset", 0F);
                    builder.addSeat(new Seat(position, driver, yawOffset));
                }
            }
        }

        private void writeSeats(VehicleProperties properties, JsonObject object)
        {
            if(properties.getSeats().size() > 0)
            {
                JsonArray seats = new JsonArray();
                for(Seat seat : properties.getSeats())
                {
                    JsonObject seatObject = new JsonObject();
                    this.addVec3Property(seatObject, "position", seat.getPosition());
                    if(seat.isDriverSeat()) seatObject.addProperty("driver", seat.isDriverSeat());
                    if(seat.getYawOffset() != 0) seatObject.addProperty("yawOffset", seat.getYawOffset());
                    seats.add(seatObject);
                }
                object.add("seats", seats);
            }
        }

        private <T extends Enum> T getAsEnum(JsonObject object, String memberName, Class<T> enumClass, T defaultValue)
        {
            if(object.has(memberName))
            {
                String enumString = GsonHelper.getAsString(object, memberName);
                return Stream.of(enumClass.getEnumConstants()).filter(side -> side.name().equalsIgnoreCase(enumString)).findFirst().orElse(defaultValue);
            }
            return defaultValue;
        }

        @Nullable
        private PartPosition getAsPartPosition(JsonObject object, String memberName)
        {
            if(object.has(memberName))
            {
                JsonObject partPositionObject = object.getAsJsonObject(memberName);
                Vec3 translate = this.getAsVec3(partPositionObject, "translate", Vec3.ZERO);
                Vec3 rotation = this.getAsVec3(partPositionObject, "rotation", Vec3.ZERO);
                double scale = GsonHelper.getAsFloat(partPositionObject, "scale", 1);
                return new PartPosition(translate.x, translate.y, translate.z, rotation.x, rotation.y, rotation.z, scale);
            }
            return null;
        }

        private <T> void callIfNonNull(Consumer<T> consumer, @Nullable T value)
        {
            if(value != null)
            {
                consumer.accept(value);
            }
        }

        private void addVec3Property(JsonObject parent, String memberName, Vec3 vec)
        {
            if(vec != null && !vec.equals(Vec3.ZERO))
            {
                JsonArray array = new JsonArray();
                array.add(Double.parseDouble(FORMAT.format(vec.x)));
                array.add(Double.parseDouble(FORMAT.format(vec.y)));
                array.add(Double.parseDouble(FORMAT.format(vec.z)));
                parent.add(memberName, array);
            }
        }

        private void addPartPositionProperty(JsonObject parent, String memberName, PartPosition position)
        {
            if(position != null && position != PartPosition.DEFAULT)
            {
                JsonObject partPositionObject = new JsonObject();
                this.addVec3Property(partPositionObject, "translate", position.getTranslate());
                this.addVec3Property(partPositionObject, "rotation", position.getRotation());
                if(position.getScale() != 1) partPositionObject.addProperty("scale", Double.parseDouble(FORMAT.format(position.getScale())));
                if(partPositionObject.size() > 0) parent.add(memberName, partPositionObject);
            }
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private float axleOffset;
        private float wheelOffset;
        private Vec3 heldOffset = Vec3.ZERO;
        private Vec3 towBarPosition = Vec3.ZERO;
        private Vec3 trailerOffset = Vec3.ZERO;
        private boolean canChangeWheels = false;
        private List<Wheel> wheels = new ArrayList<>();
        private PartPosition bodyPosition = PartPosition.DEFAULT;
        private PartPosition enginePosition;
        private PartPosition fuelPortPosition;
        private PartPosition keyPortPosition;
        private PartPosition keyPosition;
        private PartPosition displayPosition = PartPosition.DEFAULT;
        private Vec3 frontAxelVec;
        private Vec3 rearAxelVec;
        private List<Seat> seats = new ArrayList<>();
        private IEngineType engineType = EngineType.NONE;
        private boolean colored;

        public Builder setAxleOffset(float axleOffset)
        {
            this.axleOffset = axleOffset;
            return this;
        }

        public Builder setHeldOffset(double x, double y, double z)
        {
            this.heldOffset = new Vec3(x, y, z);
            return this;
        }

        public Builder setHeldOffset(Vec3 vec)
        {
            this.heldOffset = vec;
            return this;
        }

        public Builder setTowBarPosition(double x, double y, double z)
        {
            this.towBarPosition = new Vec3(x, y, z);
            return this;
        }

        public Builder setTowBarPosition(Vec3 vec)
        {
            this.towBarPosition = vec;
            return this;
        }

        public Builder setTrailerOffset(double x, double y, double z)
        {
            this.trailerOffset = new Vec3(x, y, z);
            return this;
        }

        public Builder setTrailerOffset(Vec3 vec)
        {
            this.trailerOffset = vec;
            return this;
        }

        public Builder setCanChangeWheels(boolean canChangeWheels)
        {
            this.canChangeWheels = canChangeWheels;
            return this;
        }

        public Builder addWheel(Wheel.Builder builder)
        {
            this.wheels.add(builder.build());
            return this;
        }

        public Builder setBodyPosition(PartPosition bodyPosition)
        {
            this.bodyPosition = bodyPosition;
            return this;
        }

        public Builder setEnginePosition(PartPosition enginePosition)
        {
            this.enginePosition = enginePosition;
            return this;
        }

        public Builder setFuelPortPosition(PartPosition fuelPortPosition)
        {
            this.fuelPortPosition = fuelPortPosition;
            return this;
        }

        public Builder setKeyPortPosition(PartPosition keyPortPosition)
        {
            this.keyPortPosition = keyPortPosition;
            return this;
        }

        public Builder setKeyPosition(PartPosition keyPosition)
        {
            this.keyPosition = keyPosition;
            return this;
        }

        public Builder setDisplayPosition(PartPosition displayPosition)
        {
            this.displayPosition = displayPosition;
            return this;
        }

        public Builder setFrontAxleOffset(double offset)
        {
            this.frontAxelVec = new Vec3(0, 0, offset);
            return this;
        }

        public Builder setRearAxleOffset(double offset)
        {
            this.rearAxelVec = new Vec3(0, 0, offset);
            return this;
        }

        public Builder addSeat(Seat seat)
        {
            this.seats.add(seat);
            return this;
        }

        public Builder setEngineType(IEngineType engineType)
        {
            this.engineType = engineType;
            return this;
        }

        public Builder setColored(boolean colored)
        {
            this.colored = colored;
            return this;
        }

        public VehicleProperties build(boolean scaleWheels)
        {
            this.validate();
            this.calculateWheelOffset();
            List<Wheel> wheels = scaleWheels ? this.generateScaledWheels() : this.wheels;
            return new VehicleProperties(this.axleOffset, this.wheelOffset, this.heldOffset, this.towBarPosition, this.trailerOffset, this.canChangeWheels, wheels, this.bodyPosition, this.enginePosition, this.fuelPortPosition, this.keyPortPosition, this.keyPosition, this.displayPosition, this.frontAxelVec, this.rearAxelVec, this.seats, this.engineType, this.colored);
        }

        private void validate()
        {
            if(this.seats.stream().filter(Seat::isDriverSeat).count() > 1)
            {
                throw new RuntimeException("Unable to build vehicles properties. The maximum amount of drivers seats is one but tried to add more.");
            }
        }

        private List<Wheel> generateScaledWheels()
        {
            List<Wheel> copy = this.wheels.stream().map(Wheel::copy).collect(Collectors.toList());
            copy.stream().filter(Wheel::isAutoScale).forEach(wheel -> {
                double scale = ((this.wheelOffset + wheel.getOffsetY()) * 2) / WHEEL_RADIUS;
                wheel.updateScale(scale);
            });
            return copy;
        }

        private void calculateWheelOffset()
        {
            this.wheels.stream().filter(wheel -> !wheel.isAutoScale()).max((w1, w2) -> (int) (this.getLowestSittingPosition(w1) - this.getLowestSittingPosition(w2))).ifPresent(wheel -> {
                this.wheelOffset = this.getLowestSittingPosition(wheel);
            });
        }

        private float getLowestSittingPosition(Wheel wheel)
        {
            double radius = WHEEL_RADIUS * wheel.getScaleY();
            double lowestPosition = radius / 2;
            lowestPosition -= wheel.getOffsetY();
            return (float) lowestPosition;
        }
    }
}
