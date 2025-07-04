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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.joml.Matrix4f;

public class NoBlockDropGlueRenderer extends EntityRenderer<NoBlockDropGlueEntity> {

    // Textura del glue de Create
    private static final ResourceLocation GLUE_TEXTURE = new ResourceLocation("fragileglue", "textures/special/glue.png");
    private static final float SIZE = 0.5F;
    private static final float NORMAL_OUTLINE_WIDTH = 0.02F;
    private static final float HIGHLIGHTED_OUTLINE_WIDTH = 0.05F;

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

        // Detectar si el jugador está mirando la entidad
        boolean isLookingAt = isPlayerLookingAtEntity(player, entity);

        poseStack.pushPose();

        // Posicionar en el centro del bloque
        poseStack.translate(0.0D, 0.5D, 0.0D);

        // Renderizar el borde del bloque con diferentes efectos según si está siendo mirado
        if (isLookingAt) {
            this.renderHighlightedBlockOutline(poseStack, buffer, packedLight, entity);
            this.renderGlueTexture(poseStack, buffer, packedLight, entity);
        } else {
            this.renderBlockOutline(poseStack, buffer, packedLight, entity);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private boolean isPlayerLookingAtEntity(Player player, NoBlockDropGlueEntity entity) {
        // Obtener la entidad más cercana que está mirando el jugador
        NoBlockDropGlueEntity closestEntity = getClosestLookedAtEntity(player);

        // Solo devolver true si esta entidad es la más cercana
        return closestEntity != null && closestEntity.equals(entity);
    }

    private NoBlockDropGlueEntity getClosestLookedAtEntity(Player player) {
        Vec3 playerEyePos = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        double reachDistance = 8.0;
        Vec3 endPos = playerEyePos.add(lookVector.scale(reachDistance));

        NoBlockDropGlueEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        // Buscar todas las entidades NoBlockDropGlueEntity en el área
        for (NoBlockDropGlueEntity glueEntity : player.level().getEntitiesOfClass(
                NoBlockDropGlueEntity.class,
                new AABB(playerEyePos.subtract(reachDistance, reachDistance, reachDistance),
                        playerEyePos.add(reachDistance, reachDistance, reachDistance)))) {

            // Verificar si el rayo intersecta con el AABB de la entidad
            AABB entityBB = glueEntity.getBoundingBox();
            if (!entityBB.clip(playerEyePos, endPos).isPresent()) {
                continue;
            }

            // Verificación del ángulo de visión
            Vec3 entityCenter = glueEntity.position().add(0, 0.5, 0);
            Vec3 toEntity = entityCenter.subtract(playerEyePos).normalize();
            double dotProduct = lookVector.dot(toEntity);

            // Solo considerar entidades dentro del ángulo de visión
            if (dotProduct > 0.985) { // ~10 grados
                // Calcular la distancia al centro de la entidad
                double distance = playerEyePos.distanceTo(entityCenter);

                // Verificar si es la más cercana
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = glueEntity;
                }
            }
        }

        return closestEntity;
    }

    private void renderHighlightedBlockOutline(PoseStack poseStack, MultiBufferSource buffer,
                                               int packedLight, NoBlockDropGlueEntity entity) {
        // Renderizar múltiples capas de líneas para crear efecto de grosor
        for (int layer = 0; layer < 18; layer++) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
            PoseStack.Pose pose = poseStack.last();

            // Cada capa tiene un desplazamiento ligeramente diferente
            float offset = layer * 0.0025F;
            float minX = -0.5F - offset;
            float maxX = 0.5F + offset;
            float minY = -0.5F - offset;
            float maxY = 0.5F + offset;
            float minZ = -0.5F - offset;
            float maxZ = 0.5F + offset;

            // Color dorado con transparencia variable por capa
            int alpha = 255; // Más transparente en capas exteriores - (layer * 50)
            int red = 220, green = 50, blue = 50;

            // Renderizar las 12 aristas del cubo
            this.renderCubeOutline(vertexConsumer, pose, minX, maxX, minY, maxY, minZ, maxZ,
                    red, green, blue, alpha, packedLight);
        }
    }

    private void renderGlueTexture(PoseStack poseStack, MultiBufferSource buffer,
                                   int packedLight, NoBlockDropGlueEntity entity) {
        // Renderizar todas las 6 caras del cubo con la textura del glue
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(GLUE_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float size = 0.51F; // Ligeramente más grande que el bloque para evitar z-fighting
        float u0 = 0.0F, u1 = 1.0F;
        float v0 = 0.0F, v1 = 1.0F;
        int alpha = 60; // Transparencia

        // Forzar iluminación completa para evitar shading oscuro
        int fullBrightLight = 0xF000F0; // Luz máxima (15 para ambos valores)

        // Cara frontal (Z negativo)
        this.addQuad(vertexConsumer, matrix, pose,
                -size, -size, -size,  // bottom-left
                size, -size, -size,   // bottom-right
                size, size, -size,    // top-right
                -size, size, -size,   // top-left
                u0, v0, u1, v1, 0, 0, -1, alpha, fullBrightLight);

        // Cara trasera (Z positivo)
        this.addQuad(vertexConsumer, matrix, pose,
                size, -size, size,    // bottom-left
                -size, -size, size,   // bottom-right
                -size, size, size,    // top-right
                size, size, size,     // top-left
                u0, v0, u1, v1, 0, 0, 1, alpha, fullBrightLight);

        // Cara izquierda (X negativo)
        this.addQuad(vertexConsumer, matrix, pose,
                -size, -size, size,   // bottom-left
                -size, -size, -size,  // bottom-right
                -size, size, -size,   // top-right
                -size, size, size,    // top-left
                u0, v0, u1, v1, -1, 0, 0, alpha, fullBrightLight);

        // Cara derecha (X positivo)
        this.addQuad(vertexConsumer, matrix, pose,
                size, -size, -size,   // bottom-left
                size, -size, size,    // bottom-right
                size, size, size,     // top-right
                size, size, -size,    // top-left
                u0, v0, u1, v1, 1, 0, 0, alpha, fullBrightLight);

        // Cara inferior (Y negativo)
        this.addQuad(vertexConsumer, matrix, pose,
                -size, -size, size,   // bottom-left
                size, -size, size,    // bottom-right
                size, -size, -size,   // top-right
                -size, -size, -size,  // top-left
                u0, v0, u1, v1, 0, -1, 0, alpha, fullBrightLight);

        // Cara superior (Y positivo)
        this.addQuad(vertexConsumer, matrix, pose,
                -size, size, -size,   // bottom-left
                size, size, -size,    // bottom-right
                size, size, size,     // top-right
                -size, size, size,    // top-left
                u0, v0, u1, v1, 0, 1, 0, alpha, fullBrightLight);
    }

    private void addQuad(VertexConsumer vertexConsumer, Matrix4f matrix, PoseStack.Pose pose,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4,
                         float u0, float v0, float u1, float v1,
                         float nx, float ny, float nz,
                         int alpha, int packedLight) {

        // Usar color blanco puro y brillante para evitar shading oscuro
        int brightColor = 255;

        // Vértice 1
        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(brightColor, brightColor, brightColor, alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();

        // Vértice 2
        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(brightColor, brightColor, brightColor, alpha)
                .uv(u1, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();

        // Vértice 3
        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(brightColor, brightColor, brightColor, alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();

        // Vértice 4
        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(brightColor, brightColor, brightColor, alpha)
                .uv(u0, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderBlockOutline(PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, NoBlockDropGlueEntity entity) {

        // Usar RenderType.lines() para renderizar solo líneas
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        // Definir los límites del bloque normal
        float minX = -0.5F;
        float maxX = 0.5F;
        float minY = -0.5F;
        float maxY = 0.5F;
        float minZ = -0.5F;
        float maxZ = 0.5F;

        // Color rojo normal
        int red = 220, green = 12, blue = 12, alpha = 130;

        // Renderizar las 12 aristas del cubo
        this.renderCubeOutline(vertexConsumer, pose, minX, maxX, minY, maxY, minZ, maxZ,
                red, green, blue, alpha, packedLight);
    }

    private void renderCubeOutline(VertexConsumer vertexConsumer, PoseStack.Pose pose,
                                   float minX, float maxX, float minY, float maxY, float minZ, float maxZ,
                                   int red, int green, int blue, int alpha, int packedLight) {
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
        return GLUE_TEXTURE;
    }
}