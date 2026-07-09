/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Pre
 *  net.neoforged.neoforge.client.event.InputEvent$MouseButton$Pre
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package com.mrcrayfish.vehicle.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageInteractKey;
import com.mrcrayfish.vehicle.network.message.MessagePickupVehicle;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EntityRayTracer {
    private static EntityRayTracer instance;
    private final Map<EntityType<? extends IEntityRayTraceable>, IRayTraceTransforms> entityRayTraceTransforms = new HashMap<EntityType<? extends IEntityRayTraceable>, IRayTraceTransforms>();
    private final Map<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRayTraceTrianglesStatic = new HashMap<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>>();
    private final Map<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRayTraceTriangles = new HashMap<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>>();
    private final Map<EntityType<? extends IEntityRayTraceable>, Pair<Float, Float>> entityCrateScalesAndOffsets = new HashMap<EntityType<? extends IEntityRayTraceable>, Pair<Float, Float>>();
    private final Pair<Float, Float> SCALE_AND_OFFSET_DEFAULT = new ImmutablePair<>(Float.valueOf(0.25f), Float.valueOf(0.0f));
    private RayTraceResultRotated continuousInteraction;
    private InteractionHand continuousInteractionInteractionHand;
    private int continuousInteractionTickCounter;

    private EntityRayTracer() {
    }

    public static EntityRayTracer instance() {
        if (instance == null) {
            instance = new EntityRayTracer();
        }
        return instance;
    }

    public void clearDataForReregistration() {
        this.entityRayTraceTrianglesStatic.clear();
        this.entityRayTraceTriangles.clear();
        this.entityCrateScalesAndOffsets.clear();
    }

    @Nullable
    public RayTraceResultRotated getContinuousInteraction() {
        return this.continuousInteraction;
    }

    @Nullable
    public InteractionHand getContinuousInteractionInteractionHand() {
        return this.continuousInteractionInteractionHand;
    }

    public int getContinuousInteractionTickCounter() {
        return this.continuousInteractionTickCounter;
    }

    public synchronized <T extends VehicleEntity> void registerTransforms(EntityType<T> type, IRayTraceTransforms transforms) {
        this.entityRayTraceTransforms.putIfAbsent(type, transforms);
    }

    private static void createBodyTransforms(List<MatrixTransformation> transforms, EntityType<? extends VehicleEntity> entityType) {
        VehicleProperties properties = VehicleProperties.get(entityType);
        PartPosition bodyPosition = properties.getBodyPosition();
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)bodyPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)bodyPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, (float)bodyPosition.getRotZ()));
        transforms.add(MatrixTransformation.createTranslation((float)bodyPosition.getX(), (float)bodyPosition.getY(), (float)bodyPosition.getZ()));
        transforms.add(MatrixTransformation.createScale((float)bodyPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, properties.getAxleOffset() * 0.0625f, 0.0f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, properties.getWheelOffset() * 0.0625f, 0.0f));
    }

    public static void createPartTransforms(ISpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal) {
        ArrayList transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float)partPosition.getX() * 0.0625f, (float)partPosition.getY() * 0.0625f, (float)partPosition.getZ() * 0.0625f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, -0.5f, 0.0f));
        transforms.add(MatrixTransformation.createScale((float)partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, (float)partPosition.getRotZ()));
        EntityRayTracer.createTransformListForPart(model, parts, (List<MatrixTransformation>)transforms, new MatrixTransformation[0]);
    }

    public static void createPartTransforms(ISpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, RayTraceFunction function) {
        ArrayList transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float)partPosition.getX() * 0.0625f, (float)partPosition.getY() * 0.0625f, (float)partPosition.getZ() * 0.0625f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, -0.5f, 0.0f));
        transforms.add(MatrixTransformation.createScale((float)partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, (float)partPosition.getRotZ()));
        EntityRayTracer.createTransformListForPart(model, parts, (List<MatrixTransformation>)transforms, function, new MatrixTransformation[0]);
    }

    public static void createPartTransforms(Item part, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, RayTraceFunction function) {
        ArrayList transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float)partPosition.getX() * 0.0625f, (float)partPosition.getY() * 0.0625f, (float)partPosition.getZ() * 0.0625f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, -0.5f, 0.0f));
        transforms.add(MatrixTransformation.createScale((float)partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, (float)partPosition.getRotZ()));
        EntityRayTracer.createTransformListForPart(new ItemStack((ItemLike)part), parts, (List<MatrixTransformation>)transforms, function, new MatrixTransformation[0]);
    }

    public static void createPartTransforms(double x, double y, double z, Vector3f rotation, double scale, List<MatrixTransformation> transforms) {
        transforms.add(MatrixTransformation.createTranslation((float)x * 0.0625f, (float)y * 0.0625f, (float)z * 0.0625f));
        transforms.add(MatrixTransformation.createTranslation(0.0f, -0.5f, 0.0f));
        transforms.add(MatrixTransformation.createScale((float)scale));
        transforms.add(MatrixTransformation.createTranslation(0.0f, 0.5f, 0.0f));
        if (rotation.x() != 0.0f) {
            transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, rotation.x()));
        }
        if (rotation.y() != 0.0f) {
            transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, rotation.y()));
        }
        if (rotation.z() != 0.0f) {
            transforms.add(MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Z, rotation.y()));
        }
    }

    public static void createFuelPartTransforms(EntityType<? extends VehicleEntity> entityType, ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal) {
        PartPosition fuelPortPosition = VehicleProperties.get(entityType).getFuelPortPosition();
        EntityRayTracer.createPartTransforms(model, fuelPortPosition, parts, transformsGlobal, RayTraceFunction.FUNCTION_FUELING);
    }

    public static void createFuelPartTransforms(Item part, double xMeters, double yMeters, double zMeters, double xPixel, double yPixel, double zPixel, double rotation, double scale, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal) {
        ArrayList partTransforms = Lists.newArrayList();
        partTransforms.add(MatrixTransformation.createTranslation((float)xMeters, (float)yMeters, (float)zMeters));
        EntityRayTracer.createPartTransforms(xPixel, yPixel, zPixel, new Vector3f(0.0f, (float)rotation, 0.0f), scale, partTransforms);
        transformsGlobal.addAll(partTransforms);
        EntityRayTracer.createTransformListForPart(new ItemStack((ItemLike)part), parts, transformsGlobal, RayTraceFunction.FUNCTION_FUELING, new MatrixTransformation[0]);
    }

    public static void createKeyPortTransforms(EntityType<? extends VehicleEntity> entityType, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal) {
        PartPosition keyPortPosition = VehicleProperties.get(entityType).getKeyPortPosition();
        EntityRayTracer.createPartTransforms(SpecialModels.KEY_HOLE, keyPortPosition, parts, transformsGlobal);
    }

    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, @Nullable RayTraceFunction continuousInteraction, MatrixTransformation ... transforms) {
        ArrayList transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart(part, continuousInteraction), transformsAll);
    }

    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation ... transforms) {
        EntityRayTracer.createTransformListForPart(part, parts, transformsGlobal, null, transforms);
    }

    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation ... transforms) {
        EntityRayTracer.createTransformListForPart(new ItemStack((ItemLike)part), parts, transformsGlobal, transforms);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation ... transforms) {
        EntityRayTracer.createTransformListForPart(model, parts, new java.util.ArrayList<MatrixTransformation>(), transforms);
    }

    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation ... transforms) {
        EntityRayTracer.createTransformListForPart(part, parts, new java.util.ArrayList<MatrixTransformation>(), transforms);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, @Nullable RayTraceFunction continuousInteraction, MatrixTransformation ... transforms) {
        ArrayList transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart(model, continuousInteraction), transformsAll);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation ... transforms) {
        EntityRayTracer.createTransformListForPart(model, parts, transformsGlobal, null, transforms);
    }

    private <T extends VehicleEntity> void generateEntityTriangles(EntityType<T> entityType, Map<RayTracePart, List<MatrixTransformation>> transforms) {
        HashMap<RayTracePart, TriangleRayTraceList> partTriangles = new HashMap<RayTracePart, TriangleRayTraceList>();
        for (Map.Entry<RayTracePart, List<MatrixTransformation>> entryPart : transforms.entrySet()) {
            Matrix4f matrix = new Matrix4f();
            matrix.identity();
            for (MatrixTransformation transform : entryPart.getValue()) {
                transform.transform(matrix);
            }
            EntityRayTracer.finalizePartStackMatrix(matrix);
            RayTracePart part = entryPart.getKey();
            partTriangles.put(part, new TriangleRayTraceList(EntityRayTracer.generateTriangles(EntityRayTracer.getModel(part), matrix)));
        }
        this.entityRayTraceTrianglesStatic.put(entityType, partTriangles);
        HashMap<RayTracePart, TriangleRayTraceList> partTrianglesCopy = new HashMap<RayTracePart, TriangleRayTraceList>(partTriangles);
        Map<RayTracePart, TriangleRayTraceList> partTrianglesAll = this.entityRayTraceTriangles.get(entityType);
        if (partTrianglesAll != null) {
            partTrianglesCopy.putAll(partTrianglesAll);
        }
        this.entityRayTraceTriangles.put(entityType, partTrianglesCopy);
    }

    public Pair<Float, Float> getCrateScaleAndOffset(EntityType<? extends VehicleEntity> entityType) {
        Pair<Float, Float> scaleAndOffset = this.entityCrateScalesAndOffsets.get(entityType);
        return scaleAndOffset == null ? this.SCALE_AND_OFFSET_DEFAULT : scaleAndOffset;
    }

    private static BakedModel getModel(RayTracePart part) {
        if (part.model != null) {
            return part.model.getModel();
        }
        return Minecraft.getInstance().getItemRenderer().getModel(part.partStack, null, (LivingEntity)Minecraft.getInstance().player, 0);
    }

    private static List<TriangleRayTrace> generateTriangles(BakedModel model, @Nullable Matrix4f matrix) {
        ArrayList triangles = Lists.newArrayList();
        try {
            RandomSource random = RandomSource.create();
            random.setSeed(42L);
            EntityRayTracer.generateTriangles(model.getQuads(null, null, random), matrix, triangles);
            for (Direction facing : Direction.values()) {
                random.setSeed(42L);
                EntityRayTracer.generateTriangles(model.getQuads(null, facing, random), matrix, triangles);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return triangles;
    }

    private static void generateTriangles(List<BakedQuad> list, @Nullable Matrix4f matrix, List<TriangleRayTrace> triangles) {
        for (BakedQuad quad : list) {
            int[] data = quad.getVertices();
            int size = data.length / 4;
            float[] triangle1 = new float[9];
            float[] triangle2 = new float[9];
            triangle1[0] = Float.intBitsToFloat(data[0]);
            triangle1[1] = Float.intBitsToFloat(data[1]);
            triangle1[2] = Float.intBitsToFloat(data[2]);
            triangle1[3] = triangle2[6] = Float.intBitsToFloat(data[size]);
            triangle1[4] = triangle2[7] = Float.intBitsToFloat(data[size + 1]);
            triangle1[5] = triangle2[8] = Float.intBitsToFloat(data[size + 2]);
            triangle2[0] = Float.intBitsToFloat(data[size *= 2]);
            triangle2[1] = Float.intBitsToFloat(data[size + 1]);
            triangle2[2] = Float.intBitsToFloat(data[size + 2]);
            size = (int)((double)size * 1.5);
            triangle1[6] = triangle2[3] = Float.intBitsToFloat(data[size]);
            triangle1[7] = triangle2[4] = Float.intBitsToFloat(data[size + 1]);
            triangle1[8] = triangle2[5] = Float.intBitsToFloat(data[size + 2]);
            EntityRayTracer.transformTriangleAndAdd(triangle1, matrix, triangles);
            EntityRayTracer.transformTriangleAndAdd(triangle2, matrix, triangles);
        }
    }

    private static void transformTriangleAndAdd(float[] triangle, @Nullable Matrix4f matrix, List<TriangleRayTrace> triangles) {
        triangles.add(new TriangleRayTrace(matrix != null ? EntityRayTracer.getTransformedTriangle(triangle, matrix) : triangle));
    }

    private static float[] getTransformedTriangle(float[] triangle, Matrix4f matrix) {
        float[] triangleNew = new float[9];
        for (int i = 0; i < 9; i += 3) {
            Vector4f vec = new Vector4f(triangle[i], triangle[i + 1], triangle[i + 2], 1.0f);
            matrix.transform(vec);
            triangleNew[i] = vec.x();
            triangleNew[i + 1] = vec.y();
            triangleNew[i + 2] = vec.z();
        }
        return triangleNew;
    }

    public static void finalizePartStackMatrix(Matrix4f matrix) {
        MatrixTransformation.createTranslation(-0.5f, -0.5f, -0.5f).transform(matrix);
    }

    public static void interactWithEntity(IEntityRayTraceable entity, EntityHitResult result) {
        Minecraft.getInstance().gameMode.interact((Player)Minecraft.getInstance().player, (Player)entity, InteractionHand.MAIN_HAND);
        Minecraft.getInstance().gameMode.interactAt((Player)Minecraft.getInstance().player, (Player)entity, result, InteractionHand.MAIN_HAND);
    }

    @SubscribeEvent
    public void rayTraceEntitiesContinuously(ClientTickEvent.Pre event) {
        if (this.continuousInteraction == null || Minecraft.getInstance().player == null) {
            return;
        }
        RayTraceResultRotated result = this.rayTraceEntities(this.continuousInteraction.isRightClick());
        if (result == null || result.getEntity() != this.continuousInteraction.getEntity() || result.getPartHit() != this.continuousInteraction.getPartHit()) {
            this.continuousInteraction = null;
            this.continuousInteractionTickCounter = 0;
            return;
        }
        this.continuousInteractionInteractionHand = result.performContinuousInteraction();
        if (this.continuousInteractionInteractionHand == null) {
            this.continuousInteraction = null;
            this.continuousInteractionTickCounter = 0;
        } else {
            ++this.continuousInteractionTickCounter;
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (((Boolean)Config.CLIENT.reloadRayTracerEachTick.get()).booleanValue()) {
            this.entityRayTraceTransforms.keySet().forEach(type -> this.initializeTransforms((EntityType)type, true));
        }
    }

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseButton.Pre event) {
        boolean leftClick;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getOverlay() != null || mc.screen != null) {
            return;
        }
        boolean rightClick = event.getButton() == 1;
        boolean bl = leftClick = event.getButton() == 0;
        if (!((rightClick || ((Boolean)Config.CLIENT.enabledLeftClick.get()).booleanValue() && leftClick) && event.getAction() != 0)) {
            return;
        }
        if (this.performRayTrace(rightClick)) {
            event.setCanceled(true);
        }
    }

    private boolean performRayTrace(boolean rightClick) {
        RayTraceResultRotated result = this.rayTraceEntities(rightClick);
        if (result != null) {
            this.continuousInteractionInteractionHand = result.performContinuousInteraction();
            if (this.continuousInteractionInteractionHand != null) {
                this.continuousInteraction = result;
                this.continuousInteractionTickCounter = 1;
            }
            return true;
        }
        return false;
    }

    @Nullable
    private <T extends VehicleEntity> RayTraceResultRotated rayTraceEntities(boolean rightClick) {
        double eyeDistance;
        float reach = (float)Minecraft.getInstance().player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 eyeVec = Minecraft.getInstance().player.getEyePosition(1.0f);
        Vec3 forwardVec = eyeVec.add(Minecraft.getInstance().player.getViewVector(1.0f).scale((double)reach));
        AABB box = new AABB(eyeVec, eyeVec).inflate((double)reach);
        RayTraceResultRotated closestRayTraceResult = null;
        double closestDistance = Double.MAX_VALUE;
        for (VehicleEntity entity : Minecraft.getInstance().level.getEntitiesOfClass(VehicleEntity.class, box)) {
            EntityType type = entity.getType();
            if (this.entityRayTraceTransforms.containsKey(type)) {
                double distance;
                this.initializeTransforms(type, false);
                RayTraceResultRotated rayTraceResult = this.rayTraceEntityRotated(entity, eyeVec, forwardVec, reach, rightClick);
                if (rayTraceResult == null || !((distance = rayTraceResult.getDistanceToEyes()) < closestDistance)) continue;
                closestRayTraceResult = rayTraceResult;
                closestDistance = distance;
                continue;
            }
            VehicleMod.LOGGER.warn("The vehicle '" + String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(type)) + "' does not have any registered ray trace transforms.");
        }
        if (closestRayTraceResult != null && (eyeDistance = closestRayTraceResult.getDistanceToEyes()) <= (double)reach) {
            boolean bypass = this.entityRayTraceTrianglesStatic.keySet().contains(closestRayTraceResult.getEntity().getType());
            HitResult result = Minecraft.getInstance().hitResult;
            if (bypass && result != null && result.getType() != HitResult.Type.MISS) {
                AABB boxMC = null;
                if (result.getType() == HitResult.Type.ENTITY) {
                    boxMC = closestRayTraceResult.getEntity().getBoundingBox();
                } else if (result.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult)result).getBlockPos();
                    boxMC = closestRayTraceResult.getEntity().level().getBlockState(pos).getShape((BlockGetter)closestRayTraceResult.getEntity().level(), pos).bounds();
                }
                bypass = boxMC != null && boxMC.contains(eyeVec);
            }
            Vec3 hit = forwardVec;
            if (!bypass && result != null && result.getType() != HitResult.Type.MISS) {
                if (result.getType() == HitResult.Type.ENTITY && ((EntityHitResult)result).getEntity() == closestRayTraceResult.getEntity()) {
                    bypass = true;
                } else {
                    hit = result.getLocation();
                }
            }
            if ((bypass || eyeDistance < hit.distanceTo(eyeVec)) && ((IEntityRayTraceable)closestRayTraceResult.getEntity()).processHit(closestRayTraceResult, rightClick)) {
                return closestRayTraceResult;
            }
        }
        return null;
    }

    private <T extends VehicleEntity> void initializeTransforms(EntityType<T> type, boolean reload) {
        if (!this.entityRayTraceTrianglesStatic.containsKey(type) || reload) {
            ArrayList<MatrixTransformation> transforms = new ArrayList<MatrixTransformation>();
            EntityRayTracer.createBodyTransforms(transforms, type);
            HashMap parts = Maps.newHashMap();
            IRayTraceTransforms rayTraceTransforms = this.entityRayTraceTransforms.get(type);
            rayTraceTransforms.load(this, transforms, parts);
            this.generateEntityTriangles(type, parts);
            float min = 0.0f;
            float max = 0.0f;
            for (Map.Entry<RayTracePart, TriangleRayTraceList> entry : this.entityRayTraceTriangles.get(type).entrySet()) {
                for (TriangleRayTrace triangle : entry.getValue().getTriangles()) {
                    float[] data = triangle.getData();
                    for (int i = 0; i < data.length; i += 3) {
                        float x = data[i];
                        float y = data[i + 1];
                        float z = data[i + 2];
                        if (x < min) {
                            min = x;
                        }
                        if (y < min) {
                            min = y;
                        }
                        if (z < min) {
                            min = z;
                        }
                        if (x > max) {
                            max = x;
                        }
                        if (y > max) {
                            max = y;
                        }
                        if (!(z > max)) continue;
                        max = z;
                    }
                }
            }
            float range = max - min;
            this.entityCrateScalesAndOffsets.put(type, (Pair<Float, Float>)new ImmutablePair<>(Float.valueOf(1.0f / (range * 1.25f)), Float.valueOf(-(min + range * 0.5f))));
        }
    }

    @Nullable
    public RayTraceResultRotated rayTraceEntityRotated(VehicleEntity entity, Vec3 eyeVec, Vec3 forwardVec, double reach, boolean rightClick) {
        Vec3 pos = entity.position();
        double angle = Math.toRadians(-entity.getYRot());
        Vec3 eyeVecRotated = EntityRayTracer.rotateVecXZ(eyeVec, angle, pos);
        Vec3 forwardVecRotated = EntityRayTracer.rotateVecXZ(forwardVec, angle, pos);
        float[] eyes = new float[]{(float)eyeVecRotated.x, (float)eyeVecRotated.y, (float)eyeVecRotated.z};
        Vec3 look = forwardVecRotated.subtract(eyeVecRotated).normalize().scale(reach);
        float[] direction = new float[]{(float)look.x, (float)look.y, (float)look.z};
        double distanceShortest = Double.MAX_VALUE;
        RayTraceResultTriangle lookBox = EntityRayTracer.rayTracePartTriangles(entity, pos, eyeVecRotated, null, distanceShortest, eyes, direction, entity.getApplicableInteractionBoxes(), false, entity.getStaticInteractionBoxMap());
        RayTraceResultTriangle lookPart = EntityRayTracer.rayTracePartTriangles(entity, pos, eyeVecRotated, null, distanceShortest = EntityRayTracer.updateShortestDistance(lookBox, distanceShortest), eyes, direction, entity.getNonApplicableParts(), true, this.entityRayTraceTrianglesStatic.get(entity.getType()));
        if (lookPart != null) {
            return new RayTraceResultRotated(entity, EntityRayTracer.rotateVecXZ(lookPart.getHit(), -angle, pos), lookPart.getDistance(), lookPart.getPart(), rightClick);
        }
        return lookBox == null ? null : new RayTraceResultRotated(entity, EntityRayTracer.rotateVecXZ(lookBox.getHit(), -angle, pos), lookBox.getDistance(), lookBox.getPart(), rightClick);
    }

    private static double updateShortestDistance(RayTraceResultTriangle lookObject, double distanceShortest) {
        if (lookObject != null) {
            distanceShortest = lookObject.getDistance();
        }
        return distanceShortest;
    }

    private static RayTraceResultTriangle rayTracePartTriangles(Entity entity, Vec3 pos, Vec3 eyeVecRotated, RayTraceResultTriangle lookPart, double distanceShortest, float[] eyes, float[] direction, @Nullable List<RayTracePart> partsApplicable, boolean invalidateParts, Map<RayTracePart, TriangleRayTraceList> parts) {
        if (parts != null) {
            for (Map.Entry<RayTracePart, TriangleRayTraceList> entry : parts.entrySet()) {
                if (partsApplicable != null && invalidateParts == partsApplicable.contains(entry.getKey())) continue;
                RayTracePart part = entry.getKey();
                for (TriangleRayTrace triangle : entry.getValue().getTriangles(part, entity)) {
                    double distance;
                    RayTraceResultTriangle lookObjectPutative = RayTraceResultTriangle.calculateIntercept(eyes, direction, pos, triangle.getData(), part);
                    if (lookObjectPutative == null || !((distance = lookObjectPutative.calculateAndSaveDistance(eyeVecRotated)) < distanceShortest)) continue;
                    lookPart = lookObjectPutative;
                    distanceShortest = distance;
                }
            }
        }
        return lookPart;
    }

    private static Vec3 rotateVecXZ(Vec3 vec, double angle, Vec3 rotationPoint) {
        double x = rotationPoint.x + Math.cos(angle) * (vec.x - rotationPoint.x) - Math.sin(angle) * (vec.z - rotationPoint.z);
        double z = rotationPoint.z + Math.sin(angle) * (vec.x - rotationPoint.x) + Math.cos(angle) * (vec.z - rotationPoint.z);
        return new Vec3(x, vec.y, z);
    }

    public <T extends VehicleEntity> void renderRayTraceElements(T entity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float yaw) {
        if (((Boolean)Config.CLIENT.renderOutlines.get()).booleanValue()) {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            RenderSystem.lineWidth((float)Math.max(2.0f, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0f * 2.0f));
            RenderSystem.enableDepthTest();
            Tesselator tessellator = Tesselator.getInstance();
            this.renderRayTraceTriangles(entity, tessellator, matrixStack.last().pose());
            RenderSystem.disableDepthTest();
            VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.lines());
            entity.getApplicableInteractionBoxes().stream().filter(rayTracePart -> rayTracePart.partBox != null).forEach(rayTracePart -> EntityRayTracer.renderShape(matrixStack, builder, Shapes.create((AABB)rayTracePart.partBox), 0.0f, 1.0f, 0.0f, 1.0f));
            matrixStack.popPose();
        }
    }

    private <T extends VehicleEntity> void renderRayTraceTriangles(T entity, Tesselator tessellator, Matrix4f pose) {
        EntityType type = entity.getType();
        this.initializeTransforms(type, false);
        Map<RayTracePart, TriangleRayTraceList> map = this.entityRayTraceTriangles.get(type);
        if (map != null) {
            List partsNonApplicable = entity.getNonApplicableParts();
            for (Map.Entry<RayTracePart, TriangleRayTraceList> entry : map.entrySet()) {
                if (partsNonApplicable != null && partsNonApplicable.contains(entry.getKey())) continue;
                for (TriangleRayTrace triangle : entry.getValue().getTriangles(entry.getKey(), entity)) {
                    triangle.draw(tessellator, pose, 1.0f, 0.0f, 0.0f, 0.4f);
                }
            }
        }
    }

    public static void renderShape(PoseStack matrixStack, VertexConsumer builder, VoxelShape shape, float red, float green, float blue, float alpha) {
        Matrix4f pose = matrixStack.last().pose();
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            builder.addVertex(pose, (float)minX, (float)minY, (float)minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
            builder.addVertex(pose, (float)maxX, (float)maxY, (float)maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
        });
    }

    public static TriangleRayTraceList boxToTriangles(AABB box, @Nullable BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory) {
        ArrayList triangles = Lists.newArrayList();
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, box.maxX, box.minY, box.minZ, box.minX, box.minY, box.minZ);
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, box.maxX, box.minY, box.minZ);
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ);
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ);
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, box.minX, box.maxY, box.minZ);
        EntityRayTracer.getTrianglesFromQuadAndAdd(triangles, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ);
        return new TriangleRayTraceList(triangles, matrixFactory);
    }

    public static TriangleRayTraceList boxToTriangles(AABB box) {
        return EntityRayTracer.boxToTriangles(box, null);
    }

    private static void getTrianglesFromQuadAndAdd(List<TriangleRayTrace> triangles, double ... data) {
        int size = 3;
        float[] triangle1 = new float[9];
        float[] triangle2 = new float[9];
        triangle1[0] = (float)data[0];
        triangle1[1] = (float)data[1];
        triangle1[2] = (float)data[2];
        triangle1[3] = triangle2[6] = (float)data[size];
        triangle1[4] = triangle2[7] = (float)data[size + 1];
        triangle1[5] = triangle2[8] = (float)data[size + 2];
        triangle2[0] = (float)data[size *= 2];
        triangle2[1] = (float)data[size + 1];
        triangle2[2] = (float)data[size + 2];
        size = (int)((double)size * 1.5);
        triangle1[6] = triangle2[3] = (float)data[size];
        triangle1[7] = triangle2[4] = (float)data[size + 1];
        triangle1[8] = triangle2[5] = (float)data[size + 2];
        EntityRayTracer.transformTriangleAndAdd(triangle1, null, triangles);
        EntityRayTracer.transformTriangleAndAdd(triangle2, null, triangles);
    }

    public static class RayTraceResultRotated
    extends EntityHitResult {
        private final RayTracePart partHit;
        private final double distanceToEyes;
        private final boolean rightClick;

        private RayTraceResultRotated(Entity entityHit, Vec3 hitVec, double distanceToEyes, RayTracePart partHit, boolean rightClick) {
            super(entityHit, hitVec);
            this.distanceToEyes = distanceToEyes;
            this.partHit = partHit;
            this.rightClick = rightClick;
        }

        public RayTracePart getPartHit() {
            return this.partHit;
        }

        public double getDistanceToEyes() {
            return this.distanceToEyes;
        }

        public boolean isRightClick() {
            return this.rightClick;
        }

        public InteractionHand performContinuousInteraction() {
            return this.partHit.getContinuousInteraction() == null ? null : this.partHit.getContinuousInteraction().apply(EntityRayTracer.instance(), this, (Player)Minecraft.getInstance().player);
        }

        public <R> boolean equalsContinuousInteraction(RayTraceFunction function) {
            return function.equals(this.partHit.getContinuousInteraction());
        }
    }

    public static class MatrixTransformation {
        private final MatrixTransformationType type;
        private float x;
        private float y;
        private float z;
        private float angle;

        public MatrixTransformation(MatrixTransformationType type, float x, float y, float z) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MatrixTransformation(MatrixTransformationType type, float x, float y, float z, float angle) {
            this(type, x, y, z);
            this.angle = angle;
        }

        public static MatrixTransformation createTranslation(float x, float y, float z) {
            return new MatrixTransformation(MatrixTransformationType.TRANSLATION, x, y, z);
        }

        public static MatrixTransformation createRotation(Vector3f axis, float angle) {
            return new MatrixTransformation(MatrixTransformationType.ROTATION, axis.x(), axis.y(), axis.z(), angle);
        }

        public static MatrixTransformation createScale(float x, float y, float z) {
            return new MatrixTransformation(MatrixTransformationType.SCALE, x, y, z);
        }

        public static MatrixTransformation createScale(float xyz) {
            return new MatrixTransformation(MatrixTransformationType.SCALE, xyz, xyz, xyz);
        }

        public void transform(Matrix4f matrix) {
            PoseStack matrixStack = new PoseStack();
            switch (this.type.ordinal()) {
                case 1: {
                    matrixStack.mulPose(Axis.of((Vector3f)new Vector3f(this.x, this.y, this.z)).rotationDegrees(this.angle));
                    break;
                }
                case 0: {
                    matrixStack.translate(this.x, this.y, this.z);
                    break;
                }
                case 2: {
                    matrixStack.scale(this.x, this.y, this.z);
                }
            }
            matrix.mul((Matrix4fc)matrixStack.last().pose());
        }

        private static enum MatrixTransformationType {
            TRANSLATION,
            ROTATION,
            SCALE;

        }
    }

    public static class RayTracePart {
        private final ItemStack partStack;
        private final AABB partBox;
        private final ISpecialModel model;
        private final RayTraceFunction continuousInteraction;

        public RayTracePart(ItemStack partStack, @Nullable RayTraceFunction continuousInteraction) {
            this(partStack, null, null, continuousInteraction);
        }

        public RayTracePart(AABB partBox, @Nullable RayTraceFunction continuousInteraction) {
            this(ItemStack.EMPTY, partBox, null, continuousInteraction);
        }

        public RayTracePart(ISpecialModel model, @Nullable RayTraceFunction continuousInteraction) {
            this(ItemStack.EMPTY, null, model, continuousInteraction);
        }

        public RayTracePart(AABB partBox) {
            this(ItemStack.EMPTY, partBox, null, null);
        }

        private RayTracePart(ItemStack partStack, @Nullable AABB partBox, @Nullable ISpecialModel model, @Nullable RayTraceFunction continuousInteraction) {
            this.partStack = partStack;
            this.partBox = partBox;
            this.model = model;
            this.continuousInteraction = continuousInteraction;
        }

        public ItemStack getStack() {
            return this.partStack;
        }

        @Nullable
        public AABB getBox() {
            return this.partBox;
        }

        @Nullable
        public ISpecialModel getModel() {
            return this.model;
        }

        public RayTraceFunction getContinuousInteraction() {
            return this.continuousInteraction;
        }
    }

    public static class TriangleRayTraceList {
        private final List<TriangleRayTrace> triangles;
        private final BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory;

        public TriangleRayTraceList(List<TriangleRayTrace> triangles) {
            this(triangles, null);
        }

        public TriangleRayTraceList(List<TriangleRayTrace> triangles, @Nullable BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory) {
            this.triangles = triangles;
            this.matrixFactory = matrixFactory;
        }

        public List<TriangleRayTrace> getTriangles(RayTracePart part, Entity entity) {
            if (this.matrixFactory != null) {
                ArrayList triangles = Lists.newArrayList();
                Matrix4f matrix = this.matrixFactory.apply(part, entity);
                for (TriangleRayTrace triangle : this.triangles) {
                    triangles.add(new TriangleRayTrace(EntityRayTracer.getTransformedTriangle(triangle.getData(), matrix)));
                }
                return triangles;
            }
            return this.triangles;
        }

        public List<TriangleRayTrace> getTriangles() {
            return this.triangles;
        }
    }

    public static class TriangleRayTrace {
        private final float[] data;

        public TriangleRayTrace(float[] data) {
            this.data = data;
        }

        public float[] getData() {
            return this.data;
        }

        public void draw(Tesselator tessellator, Matrix4f pose, float red, float green, float blue, float alpha) {
            BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            buffer.addVertex(pose, this.data[6], this.data[7], this.data[8]).setColor(red, green, blue, alpha);
            buffer.addVertex(pose, this.data[0], this.data[1], this.data[2]).setColor(red, green, blue, alpha);
            buffer.addVertex(pose, this.data[3], this.data[4], this.data[5]).setColor(red, green, blue, alpha);
            BufferUploader.drawWithShader((MeshData)buffer.buildOrThrow());
        }
    }

    public static interface IEntityRayTraceable {
        @OnlyIn(value=Dist.CLIENT)
        default public boolean processHit(RayTraceResultRotated result, boolean rightClick) {
            boolean isContinuous;
            if (result.getPartHit().getModel() == SpecialModels.KEY_HOLE) {
                PacketHandler.sendToServer(new MessageInteractKey((Entity)this));
                return true;
            }
            Minecraft mc = Minecraft.getInstance();
            boolean bl = isContinuous = result.partHit.getContinuousInteraction() != null;
            if (isContinuous || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY || ((EntityHitResult)mc.hitResult).getEntity() != this) {
                boolean notRiding;
                LocalPlayer player = mc.player;
                boolean bl2 = notRiding = player.getVehicle() != this;
                if (!rightClick && notRiding) {
                    mc.gameMode.attack((Player)player, (Entity)this);
                    return true;
                }
                if (result.getPartHit().model != null || result.getPartHit().partStack != null) {
                    if (notRiding) {
                        if (player.isCrouching() && !player.isSpectator()) {
                            PacketHandler.sendToServer(new MessagePickupVehicle((Entity)this));
                            return true;
                        }
                        if (!isContinuous) {
                            EntityRayTracer.interactWithEntity(this, result);
                        }
                    }
                    return notRiding;
                }
            }
            return false;
        }

        @OnlyIn(value=Dist.CLIENT)
        default public Map<RayTracePart, TriangleRayTraceList> getStaticInteractionBoxMap() {
            return Maps.newHashMap();
        }

        @OnlyIn(value=Dist.CLIENT)
        default public Map<RayTracePart, TriangleRayTraceList> getDynamicInteractionBoxMap() {
            return Maps.newHashMap();
        }

        @OnlyIn(value=Dist.CLIENT)
        default public List<RayTracePart> getApplicableInteractionBoxes() {
            return Collections.emptyList();
        }

        @Nullable
        @OnlyIn(value=Dist.CLIENT)
        default public List<RayTracePart> getNonApplicableParts() {
            return null;
        }
    }

    public static interface IRayTraceTransforms {
        public void load(EntityRayTracer var1, List<MatrixTransformation> var2, HashMap<RayTracePart, List<MatrixTransformation>> var3);
    }

    private static class RayTraceResultTriangle {
        private static final float EPSILON = 1.0E-6f;
        private final float x;
        private final float y;
        private final float z;
        private final RayTracePart part;
        private double distance;

        public RayTraceResultTriangle(RayTracePart part, float x, float y, float z) {
            this.part = part;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3 getHit() {
            return new Vec3((double)this.x, (double)this.y, (double)this.z);
        }

        public RayTracePart getPart() {
            return this.part;
        }

        public double calculateAndSaveDistance(Vec3 eyeVec) {
            this.distance = eyeVec.distanceTo(this.getHit());
            return this.distance;
        }

        public double getDistance() {
            return this.distance;
        }

        public static RayTraceResultTriangle calculateIntercept(float[] eyes, float[] direction, Vec3 posEntity, float[] data, RayTracePart part) {
            float[] vec0 = new float[]{data[0] + (float)posEntity.x, data[1] + (float)posEntity.y, data[2] + (float)posEntity.z};
            float[] vec1 = new float[]{data[3] + (float)posEntity.x, data[4] + (float)posEntity.y, data[5] + (float)posEntity.z};
            float[] vec2 = new float[]{data[6] + (float)posEntity.x, data[7] + (float)posEntity.y, data[8] + (float)posEntity.z};
            float[] edge1 = new float[3];
            float[] edge2 = new float[3];
            float[] tvec = new float[3];
            float[] pvec = new float[3];
            float[] qvec = new float[3];
            RayTraceResultTriangle.subtract(edge1, vec1, vec0);
            RayTraceResultTriangle.subtract(edge2, vec2, vec0);
            RayTraceResultTriangle.crossProduct(pvec, direction, edge2);
            float det = RayTraceResultTriangle.dotProduct(edge1, pvec);
            if (det <= -1.0E-6f || det >= 1.0E-6f) {
                float inv_det = 1.0f / det;
                RayTraceResultTriangle.subtract(tvec, eyes, vec0);
                float u = RayTraceResultTriangle.dotProduct(tvec, pvec) * inv_det;
                if (u >= 0.0f && u <= 1.0f) {
                    RayTraceResultTriangle.crossProduct(qvec, tvec, edge1);
                    float v = RayTraceResultTriangle.dotProduct(direction, qvec) * inv_det;
                    if (v >= 0.0f && u + v <= 1.0f && inv_det * RayTraceResultTriangle.dotProduct(edge2, qvec) > 1.0E-6f) {
                        return new RayTraceResultTriangle(part, edge1[0] * u + edge2[0] * v + vec0[0], edge1[1] * u + edge2[1] * v + vec0[1], edge1[2] * u + edge2[2] * v + vec0[2]);
                    }
                }
            }
            return null;
        }

        private static void crossProduct(float[] result, float[] v1, float[] v2) {
            result[0] = v1[1] * v2[2] - v1[2] * v2[1];
            result[1] = v1[2] * v2[0] - v1[0] * v2[2];
            result[2] = v1[0] * v2[1] - v1[1] * v2[0];
        }

        private static float dotProduct(float[] v1, float[] v2) {
            return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        }

        private static void subtract(float[] result, float[] v1, float[] v2) {
            result[0] = v1[0] - v2[0];
            result[1] = v1[1] - v2[1];
            result[2] = v1[2] - v2[2];
        }
    }
}

