package com.mrcrayfish.vehicle.common;

import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class Seat
{
    private Vec3 position;
    private boolean driver;
    private float yawOffset;

    protected Seat(Vec3 position)
    {
        this(position, false);
    }

    protected Seat(Vec3 position, float yawOffset)
    {
        this(position, false);
        this.yawOffset = yawOffset;
    }

    protected Seat(Vec3 position, boolean driver)
    {
        this.position = position;
        this.driver = driver;
    }

    public Seat(Vec3 position, boolean driver, float yawOffset)
    {
        this.position = position;
        this.driver = driver;
        this.yawOffset = yawOffset;
    }

    public Vec3 getPosition()
    {
        return position;
    }

    public boolean isDriverSeat()
    {
        return driver;
    }

    public float getYawOffset()
    {
        return yawOffset;
    }

    public static Seat of(double x, double y, double z)
    {
        return new Seat(new Vec3(x, y, z));
    }

    public static Seat of(double x, double y, double z, boolean driver)
    {
        return new Seat(new Vec3(x, y, z), driver);
    }

    public static Seat of(double x, double y, double z, float yawOffset)
    {
        return new Seat(new Vec3(x, y, z), yawOffset);
    }

    public static Seat of(double x, double y, double z, boolean driver, float yawOffset)
    {
        return new Seat(new Vec3(x, y, z), driver, yawOffset);
    }
}
