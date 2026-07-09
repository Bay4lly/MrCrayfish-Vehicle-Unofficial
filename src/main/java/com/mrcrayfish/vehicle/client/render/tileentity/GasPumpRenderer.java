/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.PlayerSkin$Model
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package com.mrcrayfish.vehicle.client.render.tileentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.GasPumpBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CollisionHelper;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import javax.annotation.Nullable;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class GasPumpRenderer
implements BlockEntityRenderer<GasPumpTileEntity> {
    private final Font font;

    public GasPumpRenderer(BlockEntityRendererProvider.Context ctx) {
        this.font = ctx.getFont();
    }

    public int getViewDistance() {
        return 65536;
    }

    public void render(GasPumpTileEntity gasPump, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        double[] nozzlePos;
        BlockState state = gasPump.getBlockState();
        if (state.getBlock() != ModBlocks.GAS_PUMP.get()) {
            return;
        }
        if (!((Boolean)state.getValue((Property)GasPumpBlock.TOP)).booleanValue()) {
            return;
        }
        Direction facing = (Direction)state.getValue((Property)GasPumpBlock.DIRECTION);
        double[] hoseStartPos = CollisionHelper.fixRotation(facing, 0.620625, 1.05, 0.620625, 1.05);
        matrixStack.pushPose();
        if (gasPump.getFuelingEntity() != null) {
            Player player = gasPump.getFuelingEntity();
            Vec3 nozzleVec = this.getNozzlePosition(player, gasPump.getBlockPos(), partialTicks);
            Vec3 lookVec = this.getLookVector(player, partialTicks);
            HermiteInterpolator.Point nozzlePoint = new HermiteInterpolator.Point(nozzleVec, new Vec3(lookVec.x * 3.0, lookVec.y * 3.0, lookVec.z * 3.0));
            gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vec3(hoseStartPos[0], 0.6425, hoseStartPos[1]), new Vec3(0.0, -5.0, 0.0)), nozzlePoint));
            gasPump.setRecentlyUsed(true);
        } else if (gasPump.getCachedSpline() == null || gasPump.isRecentlyUsed()) {
            nozzlePos = CollisionHelper.fixRotation(facing, 0.345, 1.06, 0.345, 1.06);
            HermiteInterpolator.Point nozzlePoint = new HermiteInterpolator.Point(new Vec3(nozzlePos[0], 0.1, nozzlePos[1]), new Vec3(0.0, 3.0, 0.0));
            gasPump.setCachedSpline(new HermiteInterpolator(new HermiteInterpolator.Point(new Vec3(hoseStartPos[0], 0.6425, hoseStartPos[1]), new Vec3(0.0, -5.0, 0.0)), nozzlePoint));
            gasPump.setRecentlyUsed(false);
        }
        this.drawHose(gasPump.getCachedSpline(), matrixStack, renderTypeBuffer, light, this.getHoseColour(gasPump));
        if (gasPump.getFuelingEntity() == null) {
            matrixStack.pushPose();
            nozzlePos = CollisionHelper.fixRotation(facing, 0.29, 1.06, 0.29, 1.06);
            matrixStack.translate(nozzlePos[0], 0.5, nozzlePos[1]);
            matrixStack.mulPose(Axis.YP.rotationDegrees((float)facing.get2DDataValue() * -90.0f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            matrixStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            matrixStack.scale(0.8f, 0.8f, 0.8f);
            RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees((float)facing.get2DDataValue() * -90.0f));
        matrixStack.translate(-0.5, 0.0, -0.5);
        matrixStack.translate(0.5, 0.6875, 0.1875);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        matrixStack.translate(0.0, 0.0, 0.07);
        matrixStack.pushPose();
        matrixStack.scale(0.015f, -0.015f, 0.015f);
        if (gasPump.getTank() != null) {
            int amount = (int)Math.ceil(100.0 * ((double)gasPump.getTank().getFluidAmount() / (double)gasPump.getTank().getCapacity()));
            String percent = String.format("%d%%", amount);
            int width = this.font.width(percent);
            this.font.drawInBatch(percent, (float)(-width) / 2.0f, 10.0f, 0xFFFFFF, false, matrixStack.last().pose(), renderTypeBuffer, Font.DisplayMode.NORMAL, 0, light);
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0625, 0.0);
        matrixStack.scale(0.01f, -0.01f, 0.01f);
        String label = "Fuelium";
        int width = this.font.width(label);
        this.font.drawInBatch(label, (float)(-width) / 2.0f, 10.0f, 0x55FF55, false, matrixStack.last().pose(), renderTypeBuffer, Font.DisplayMode.NORMAL, 0, light);
        matrixStack.popPose();
        matrixStack.popPose();
        matrixStack.popPose();
    }

    private void drawHose(@Nullable HermiteInterpolator spline, PoseStack matrixStack, MultiBufferSource buffer, int light, Triple<Float, Float, Float> color) {
        if (spline == null) {
            return;
        }
        float red = ((Float)color.getLeft()).floatValue();
        float green = ((Float)color.getMiddle()).floatValue();
        float blue = ((Float)color.getRight()).floatValue();
        float diameter = 0.0625f;
        matrixStack.pushPose();
        VertexConsumer builder = buffer.getBuffer(HoseRenderType.HOSE);
        int segments = (Integer)Config.CLIENT.hoseSegments.get();
        for (int i = 0; i < spline.getSize() - 1; ++i) {
            for (int j = 0; j < segments; ++j) {
                float percent = (float)j / (float)segments;
                HermiteInterpolator.Result start = spline.get(i, percent);
                HermiteInterpolator.Result end = spline.get(i, (float)(j + 1) / (float)segments);
                Matrix4f startMatrix = new Matrix4f();
                startMatrix.identity();
                EntityRayTracer.MatrixTransformation.createTranslation((float)start.getPoint().x(), (float)start.getPoint().y(), (float)start.getPoint().z()).transform(startMatrix);
                if (i == 0 && j == 0) {
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z))).transform(startMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)Math.toDegrees(Math.asin(-end.getDir().normalize().y))).transform(startMatrix);
                } else {
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z))).transform(startMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)Math.toDegrees(Math.asin(-start.getDir().normalize().y))).transform(startMatrix);
                }
                Matrix4f endMatrix = new Matrix4f();
                endMatrix.identity();
                EntityRayTracer.MatrixTransformation.createTranslation((float)end.getPoint().x, (float)end.getPoint().y, (float)end.getPoint().z).transform(endMatrix);
                if (i == spline.getSize() - 2 && j == segments - 1) {
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)Math.toDegrees(Math.atan2(start.getDir().x, start.getDir().z))).transform(endMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)Math.toDegrees(Math.asin(-start.getDir().normalize().y))).transform(endMatrix);
                } else {
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, (float)Math.toDegrees(Math.atan2(end.getDir().x, end.getDir().z))).transform(endMatrix);
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, (float)Math.toDegrees(Math.asin(-end.getDir().normalize().y))).transform(endMatrix);
                }
                Matrix4f startTemp = new Matrix4f((Matrix4fc)startMatrix);
                Matrix4f endTemp = new Matrix4f((Matrix4fc)endMatrix);
                Matrix4f parent = matrixStack.last().pose();
                EntityRayTracer.MatrixTransformation.createTranslation(diameter / 2.0f, -diameter / 2.0f, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, diameter, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter / 2.0f, diameter / 2.0f, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -diameter, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0.0f, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, -diameter, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0.0f, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0.0f, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.0f, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, diameter, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, diameter, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0.0f, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(-diameter, 0.0f, 0.0f).transform(startTemp);
                this.createVertex(builder, parent, startTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.0f, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
                EntityRayTracer.MatrixTransformation.createTranslation(diameter, 0.0f, 0.0f).transform(endTemp);
                this.createVertex(builder, parent, endTemp, red, green, blue, light);
            }
        }
        matrixStack.popPose();
    }

    private Triple<Float, Float, Float> getHoseColour(GasPumpTileEntity gasPump) {
        float red = 0.05f;
        float green = 0.05f;
        float blue = 0.05f;
        if (gasPump.getFuelingEntity() != null) {
            red = (float)(Math.sqrt(gasPump.getFuelingEntity().distanceToSqr((double)gasPump.getBlockPos().getX() + 0.5, (double)gasPump.getBlockPos().getY() + 0.5, (double)gasPump.getBlockPos().getZ() + 0.5)) / (Double)Config.SERVER.maxHoseDistance.get());
            red = red * red * red * red * red * red;
            red = Math.max(red, 0.05f);
        }
        return Triple.of(Float.valueOf(red), Float.valueOf(green), Float.valueOf(blue));
    }

    private void createVertex(VertexConsumer buffer, Matrix4f parent, Matrix4f pos, float red, float green, float blue, int light) {
        Vector4f vec = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        pos.transform(vec);
        buffer.addVertex(parent, vec.x(), vec.y(), vec.z()).setColor(red, green, blue, 1.0f).setUv2(light & 0xFFFF, light >> 16);
    }

    private boolean isSlimModel(Player player) {
        if (player instanceof AbstractClientPlayer) {
            PlayerSkin.Model model = ((AbstractClientPlayer)player).getSkin().model();
            return model == PlayerSkin.Model.SLIM;
        }
        return false;
    }

    private float getPlayerBodyRotation(Player player, float partialTicks) {
        return player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks;
    }

    private Vec3 getNozzlePosition(Player player, BlockPos pos, float partialTicks) {
        double playerX = (double)pos.getX() - (player.xo + (player.getX() - player.xo) * (double)partialTicks);
        double playerY = (double)pos.getY() - (player.yo + (player.getY() - player.yo) * (double)partialTicks);
        double playerZ = (double)pos.getZ() - (player.zo + (player.getZ() - player.zo) * (double)partialTicks);
        Vec3 playerVec = new Vec3(-playerX, -playerY + 0.8, -playerZ);
        Minecraft minecraft = Minecraft.getInstance();
        if (player.equals((Object)minecraft.player) && minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
            return playerVec.add(new Vec3(-0.25, 0.5, -0.25).yRot(-player.getYRot() * ((float)Math.PI / 180)));
        }
        double handSide = player.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
        Vec3 nozzlePos = new Vec3(-0.35 * handSide, -0.025, -0.025);
        if (this.isSlimModel(player)) {
            nozzlePos = nozzlePos.add(0.03 * handSide, -0.03, 0.0);
        }
        float bodyRotation = this.getPlayerBodyRotation(player, partialTicks);
        nozzlePos = nozzlePos.yRot(-bodyRotation * ((float)Math.PI / 180));
        return playerVec.add(nozzlePos);
    }

    private Vec3 getLookVector(Player player, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (player.equals((Object)minecraft.player) && minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
            return Vec3.directionFromRotation((float)0.0f, (float)player.getYRot());
        }
        float bodyRotation = this.getPlayerBodyRotation(player, partialTicks);
        return Vec3.directionFromRotation((float)-20.0f, (float)bodyRotation);
    }

    private static abstract class HoseRenderType
    extends RenderType {
        public static final RenderType HOSE = HoseRenderType.create((String)"vehicle_hose", (VertexFormat)DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_LIGHTMAP_SHADER).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));

        private HoseRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }
}

