/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Wheel {
    private Vec3 offset;
    private Vec3 scale;
    private float width;
    private Side side;
    private Position position;
    private boolean autoScale;
    private boolean particles;
    private boolean render;

    protected Wheel(Vec3 offset, Vec3 scale, float width, Side side, Position position, boolean autoScale, boolean particles, boolean render) {
        this.offset = offset;
        this.scale = scale;
        this.width = width;
        this.side = side;
        this.position = position;
        this.autoScale = autoScale;
        this.particles = particles;
        this.render = render;
    }

    @OnlyIn(value=Dist.CLIENT)
    public float getWheelRotation(LandVehicleEntity vehicle, float partialTicks) {
        if (this.position == Position.REAR) {
            return vehicle.prevRearWheelRotation + (vehicle.rearWheelRotation - vehicle.prevRearWheelRotation) * partialTicks;
        }
        return vehicle.prevFrontWheelRotation + (vehicle.frontWheelRotation - vehicle.prevFrontWheelRotation) * partialTicks;
    }

    public Vec3 getOffset() {
        return this.offset;
    }

    public Vec3 getScale() {
        return this.scale;
    }

    public float getOffsetX() {
        return (float)this.offset.x;
    }

    public float getOffsetY() {
        return (float)this.offset.y;
    }

    public float getOffsetZ() {
        return (float)this.offset.z;
    }

    public float getWidth() {
        return this.width;
    }

    public float getScaleX() {
        return (float)this.scale.x;
    }

    public float getScaleY() {
        return (float)this.scale.y;
    }

    public float getScaleZ() {
        return (float)this.scale.z;
    }

    public Side getSide() {
        return this.side;
    }

    public Position getPosition() {
        return this.position;
    }

    void updateScale(double scale) {
        double xScale = this.scale.x != 0.0 ? this.scale.x : scale;
        double yScale = this.scale.y != 0.0 ? this.scale.y : scale;
        double zScale = this.scale.z != 0.0 ? this.scale.z : scale;
        this.scale = new Vec3(xScale, yScale, zScale);
    }

    public boolean isAutoScale() {
        return this.autoScale;
    }

    public boolean shouldSpawnParticles() {
        return this.particles;
    }

    public boolean shouldRender() {
        return this.render;
    }

    public Wheel copy() {
        return new Wheel(this.offset, this.scale, this.width, this.side, this.position, this.autoScale, this.particles, this.render);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static enum Side {
        LEFT(-1),
        RIGHT(1),
        NONE(0);

        int offset;

        private Side(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset;
        }
    }

    public static enum Position {
        FRONT,
        REAR,
        NONE;

    }

    public static class Builder {
        private Vec3 offset = Vec3.ZERO;
        private Vec3 scale = Vec3.ZERO;
        private float width = 4.0f;
        private Side side = Side.NONE;
        private Position position = Position.NONE;
        private boolean autoScale = false;
        private boolean particles = false;
        private boolean render = true;

        public Builder setOffset(double x, double y, double z) {
            this.offset = new Vec3(x, y, z);
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = new Vec3(scale, scale, scale);
            return this;
        }

        public Builder setScale(double scaleX, double scaleY, double scaleZ) {
            this.scale = new Vec3(scaleX, scaleY, scaleZ);
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder setPosition(Position position) {
            this.position = position;
            return this;
        }

        public Builder setAutoScale(boolean autoScale) {
            this.autoScale = autoScale;
            return this;
        }

        public Builder setParticles(boolean particles) {
            this.particles = particles;
            return this;
        }

        public Builder setRender(boolean render) {
            this.render = render;
            return this;
        }

        public Wheel build() {
            return new Wheel(this.offset, this.scale, this.width, this.side, this.position, this.autoScale, this.particles, this.render);
        }
    }
}

