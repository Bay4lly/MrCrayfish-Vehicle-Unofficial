package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FluidTrailerEntity extends TrailerEntity implements IEntityWithComplexSpawn
{
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 8.5 * 0.0625F, 24 * 0.0625, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = buildInteractionBoxMap();

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap()
    {
        if(!net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) return null;
        Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    }

    protected FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 100)
    {
        @Override
        protected void onContentsChanged()
        {
            syncTank();
        }
    };

    public FluidTrailerEntity(EntityType<? extends FluidTrailerEntity> type, Level worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        if(!level().isClientSide && !player.isCrouching())
        {
            if(FluidUtil.interactWithFluidHandler(player, hand, tank))
            {
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return Collections.singletonList(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(result.getPartHit() == CONNECTION_BOX && rightClick)
        {
            PacketHandler.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
            return true;
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public double getHitchOffset()
    {
        return -25.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Tank", Tag.TAG_COMPOUND))
        {
            this.tank.readFromNBT(this.level().registryAccess(), compound.getCompound("Tank"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        CompoundTag tankTag = new CompoundTag();
        this.tank.writeToNBT(this.level().registryAccess(), tankTag);
        compound.put("Tank", tankTag);
    }

    public FluidTank fluidHandler() { return this.tank; }

    public FluidTank getTank()
    {
        return this.tank;
    }

    public void syncTank()
    {
        if(!this.level().isClientSide)
        {
            PacketHandler.sendToTrackingEntity(this, new MessageEntityFluid(this.getId(), this.tank.getFluid()));
        }
    }

    @Override
    public void writeSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeNbt(this.tank.writeToNBT(this.level().registryAccess(), new CompoundTag()));
    }

    @Override
    public void readSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buffer)
    {
        super.readSpawnData(buffer);
        this.tank.readFromNBT(this.level().registryAccess(), buffer.readNbt());
    }
}
