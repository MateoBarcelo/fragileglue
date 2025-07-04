package me.legadyn.noblockdrop.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import me.legadyn.noblockdrop.item.NoBlockDropGlueItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class NoBlockDropGlueRenderer extends EntityRenderer<NoBlockDropGlueEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("fragileglue", "textures/item/fragile_glue.png");
    private static final float SIZE = 0.5F;

    public NoBlockDropGlueRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NoBlockDropGlueEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // Obtener la dirección de la cara donde está pegado el superglue
        Direction attachmentDirection = entity.getAttachmentDirection();

        // Posicionar y orientar según la cara
        this.setupPositionAndRotation(poseStack, attachmentDirection);

        // Renderizar el quad del superglue
        this.renderSuperGlueQuad(poseStack, buffer, packedLight, entity);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void setupPositionAndRotation(PoseStack poseStack, Direction direction) {
        poseStack.translate(0.5D, 0.5D, 0.5D);

        switch (direction) {
            case DOWN:
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                break;
            case UP:
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                break;
            case NORTH:
                // No rotation needed
                break;
            case SOUTH:
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                break;
            case WEST:
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                break;
            case EAST:
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                break;
        }

        poseStack.translate(-0.5D, -0.5D, -0.501D); // Ligeramente hacia afuera para evitar z-fighting
    }

    private void renderSuperGlueQuad(PoseStack poseStack, MultiBufferSource buffer,
                                     int packedLight, NoBlockDropGlueEntity entity) {

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        PoseStack.Pose pose = poseStack.last();

        // Definir los vértices del quad
        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        float size = SIZE;
        float centerX = 0.5F;
        float centerY = 0.5F;
        float z = 0.0F;

        // Calcular posiciones de los vértices
        float minX = centerX - size;
        float maxX = centerX + size;
        float minY = centerY - size;
        float maxY = centerY + size;

        // Renderizar el quad
        this.addVertex(vertexConsumer, pose, minX, minY, z, minU, maxV, packedLight);
        this.addVertex(vertexConsumer, pose, minX, maxY, z, minU, minV, packedLight);
        this.addVertex(vertexConsumer, pose, maxX, maxY, z, maxU, minV, packedLight);
        this.addVertex(vertexConsumer, pose, maxX, minY, z, maxU, maxV, packedLight);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose,
                           float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(NoBlockDropGlueEntity entity) {
        System.out.println(TEXTURE.getPath());
        return TEXTURE;
    }
}