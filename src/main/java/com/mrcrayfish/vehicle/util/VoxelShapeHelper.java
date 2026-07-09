/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.mrcrayfish.vehicle.util;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeHelper {
    public static VoxelShape combineAll(Collection<VoxelShape> shapes) {
        VoxelShape result = Shapes.empty();
        for (VoxelShape shape : shapes) {
            result = Shapes.joinUnoptimized((VoxelShape)result, (VoxelShape)shape, (BooleanOp)BooleanOp.OR);
        }
        return result.optimize();
    }

    public static VoxelShape setMaxHeight(VoxelShape source, double height) {
        AtomicReference<VoxelShape> result = new AtomicReference<VoxelShape>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            VoxelShape shape = Shapes.box((double)minX, (double)minY, (double)minZ, (double)maxX, (double)height, (double)maxZ);
            result.set(Shapes.joinUnoptimized((VoxelShape)((VoxelShape)result.get()), (VoxelShape)shape, (BooleanOp)BooleanOp.OR));
        });
        return result.get().optimize();
    }

    public static VoxelShape limitHorizontal(VoxelShape source) {
        AtomicReference<VoxelShape> result = new AtomicReference<VoxelShape>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            VoxelShape shape = Shapes.box((double)VoxelShapeHelper.limit(minX), (double)minY, (double)VoxelShapeHelper.limit(minZ), (double)VoxelShapeHelper.limit(maxX), (double)maxY, (double)VoxelShapeHelper.limit(maxZ));
            result.set(Shapes.joinUnoptimized((VoxelShape)((VoxelShape)result.get()), (VoxelShape)shape, (BooleanOp)BooleanOp.OR));
        });
        return result.get().optimize();
    }

    public static VoxelShape[] getRotatedShapes(VoxelShape source) {
        VoxelShape shapeNorth = VoxelShapeHelper.rotate(source, Direction.NORTH);
        VoxelShape shapeEast = VoxelShapeHelper.rotate(source, Direction.EAST);
        VoxelShape shapeSouth = VoxelShapeHelper.rotate(source, Direction.SOUTH);
        VoxelShape shapeWest = VoxelShapeHelper.rotate(source, Direction.WEST);
        return new VoxelShape[]{shapeSouth, shapeWest, shapeNorth, shapeEast};
    }

    public static VoxelShape rotate(VoxelShape source, Direction direction) {
        double[] adjustedValues = VoxelShapeHelper.adjustValues(direction, source.min(Direction.Axis.X), source.min(Direction.Axis.Z), source.max(Direction.Axis.X), source.max(Direction.Axis.Z));
        return Shapes.box((double)adjustedValues[0], (double)source.min(Direction.Axis.Y), (double)adjustedValues[1], (double)adjustedValues[2], (double)source.max(Direction.Axis.Y), (double)adjustedValues[3]);
    }

    private static double[] adjustValues(Direction direction, double var1, double var2, double var3, double var4) {
        switch (direction) {
            case WEST: {
                double var_temp_1 = var1;
                var1 = 1.0 - var3;
                double var_temp_2 = var2;
                var2 = 1.0 - var4;
                var3 = 1.0 - var_temp_1;
                var4 = 1.0 - var_temp_2;
                break;
            }
            case NORTH: {
                double var_temp_3 = var1;
                var1 = var2;
                var2 = 1.0 - var3;
                var3 = var4;
                var4 = 1.0 - var_temp_3;
                break;
            }
            case SOUTH: {
                double var_temp_4 = var1;
                var1 = 1.0 - var4;
                double var_temp_5 = var2;
                var2 = var_temp_4;
                double var_temp_6 = var3;
                var3 = 1.0 - var_temp_5;
                var4 = var_temp_6;
                break;
            }
        }
        return new double[]{var1, var2, var3, var4};
    }

    private static double limit(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

