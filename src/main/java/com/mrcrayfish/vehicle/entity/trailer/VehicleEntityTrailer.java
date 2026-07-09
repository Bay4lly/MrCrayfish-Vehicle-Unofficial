/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$MoveFunction
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

public class VehicleEntityTrailer
extends TrailerEntity {
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(VehicleEntityTrailer.createScaledBoundingBox(-0.4375, 0.26875, 0.875, 0.4375, 0.53125, 1.5, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = VehicleEntityTrailer.buildInteractionBoxMap();

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }
        HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    }

    public VehicleEntityTrailer(EntityType<? extends VehicleEntityTrailer> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.5;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return true;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (passenger instanceof VehicleEntity) {
            Vec3 offset = ((VehicleEntity)passenger).getProperties().getTrailerOffset().yRot((float)Math.toRadians(-this.getYRot()));
            moveFunction.accept(passenger, this.getX() + offset.x, this.getY() + this.getPassengersRidingOffset() + offset.y, this.getZ() + offset.z);
            passenger.yRotO = this.yRotO;
            passenger.setYRot(this.getYRot());
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof VehicleEntity && this.getPassengers().size() == 0;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap() {
        return interactionBoxMapStatic;
    }

    @Override
    @Nullable
    @OnlyIn(value=Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes() {
        return Collections.singletonList(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick) {
        if (result.getPartHit() == CONNECTION_BOX && rightClick) {
            PacketHandler.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
            return true;
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public double getHitchOffset() {
        return -25.0;
    }
}

