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

    // Ya no necesitamos ResourceLocation para textura personalizada
    private static final float SIZE = 0.5F;

    public NoBlockDropGlueRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NoBlockDropGlueEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof NoBlockDropGlueItem)) {
            return;
        }
        poseStack.pushPose();

        // Posicionar en el centro del bloque
        poseStack.translate(0.0D, 0.5D, 0.0D);

        // Renderizar el borde del bloque
        this.renderBlockOutline(poseStack, buffer, packedLight, entity);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void setupPositionAndRotation(PoseStack poseStack, Direction direction) {
        // Ya no necesitamos este método, pero lo dejamos por si lo usas en otro lugar
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

        poseStack.translate(-0.5D, -0.5D, -0.501D);
    }

    private void renderBlockOutline(PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, NoBlockDropGlueEntity entity) {

        // Usar RenderType.lines() para renderizar solo líneas
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        // Definir los límites del bloque (cubo de 1x1x1 centrado en el origen)
        float minX = -0.5F;
        float maxX = 0.5F;
        float minY = -0.5F;
        float maxY = 0.5F;
        float minZ = -0.5F;
        float maxZ = 0.5F;

        // Grosor de línea y color rojo
        int red = 255, green = 0, blue = 0, alpha = 255;

        // Renderizar las 12 aristas del cubo

        // Aristas inferiores (Y = minY)
        this.addLine(vertexConsumer, pose, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, minX, minY, maxZ, minX, minY, minZ, red, green, blue, alpha, packedLight);

        // Aristas superiores (Y = maxY)
        this.addLine(vertexConsumer, pose, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue, alpha, packedLight);

        // Aristas verticales
        this.addLine(vertexConsumer, pose, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha, packedLight);
        this.addLine(vertexConsumer, pose, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha, packedLight);
    }

    private void addLine(VertexConsumer consumer, PoseStack.Pose pose,
                         float x1, float y1, float z1, float x2, float y2, float z2,
                         int red, int green, int blue, int alpha, int packedLight) {

        // Calcular el vector normal para la línea
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float length = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        if (length > 0) {
            dx /= length;
            dy /= length;
            dz /= length;
        }

        // Agregar los dos vértices de la línea
        consumer.vertex(pose.pose(), x1, y1, z1)
                .color(red, green, blue, alpha)
                .normal(pose.normal(), dx, dy, dz)
                .endVertex();

        consumer.vertex(pose.pose(), x2, y2, z2)
                .color(red, green, blue, alpha)
                .normal(pose.normal(), dx, dy, dz)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(NoBlockDropGlueEntity entity) {
        // Ya no necesitamos textura, pero Minecraft requiere que devolvamos algo
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}