/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.mrcrayfish.vehicle.client.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class HermiteInterpolator {
    private Map<Pair<Integer, Float>, Result> resultCache = new HashMap<Pair<Integer, Float>, Result>();
    private Point[] points;

    public HermiteInterpolator(Point ... points) {
        this.points = points;
    }

    public Result get(int index, float progress) {
        return this.resultCache.computeIfAbsent((Pair<Integer, Float>)Pair.of(index, Float.valueOf(progress)), pair -> {
            Point p1 = this.getPoint(index);
            Point p2 = this.getPoint(index + 1);
            double pX = this.point(p1.pos.x, p2.pos.x, p1.control.x, p2.control.x, progress);
            double pY = this.point(p1.pos.y, p2.pos.y, p1.control.y, p2.control.y, progress);
            double pZ = this.point(p1.pos.z, p2.pos.z, p1.control.z, p2.control.z, progress);
            double aX = this.angle(p1.pos.x, p2.pos.x, p1.control.x, p2.control.x, progress);
            double aY = this.angle(p1.pos.y, p2.pos.y, p1.control.y, p2.control.y, progress);
            double aZ = this.angle(p1.pos.z, p2.pos.z, p1.control.z, p2.control.z, progress);
            return new Result(new Vec3(pX, pY, pZ), new Vec3(aX, aY, aZ));
        });
    }

    public Point getPoint(int index) {
        return this.points[Mth.clamp((int)index, (int)0, (int)(this.points.length - 1))];
    }

    public int getSize() {
        return this.points.length;
    }

    public double point(double p1, double p2, double t1, double t2, double s) {
        double ss = s * s;
        double sss = s * s * s;
        double a1 = 2.0 * sss - 3.0 * ss + 1.0;
        double a2 = -2.0 * sss + 3.0 * ss;
        double a3 = sss - 2.0 * ss + s;
        double a4 = sss - ss;
        return a1 * p1 + a2 * p2 + a3 * t1 + a4 * t2;
    }

    public double angle(double p1, double p2, double t1, double t2, double s) {
        double ss = s * s;
        double a1 = 6.0 * ss - 6.0 * s;
        double a2 = -6.0 * ss + 6.0 * s;
        double a3 = 3.0 * ss - 4.0 * s + 1.0;
        double a4 = 3.0 * ss - 2.0 * s;
        return a1 * p1 + a2 * p2 + a3 * t1 + a4 * t2;
    }

    public static class Point {
        private final Vec3 pos;
        private final Vec3 control;

        public Point(Vec3 pos) {
            this.pos = pos;
            this.control = pos;
        }

        public Point(Vec3 pos, Vec3 control) {
            this.pos = pos;
            this.control = control;
        }
    }

    public static class Result {
        Vec3 point;
        Vec3 direction;

        public Result(Vec3 point, Vec3 direction) {
            this.point = point;
            this.direction = direction;
        }

        public Vec3 getPoint() {
            return this.point;
        }

        public Vec3 getDir() {
            return this.direction;
        }
    }
}

