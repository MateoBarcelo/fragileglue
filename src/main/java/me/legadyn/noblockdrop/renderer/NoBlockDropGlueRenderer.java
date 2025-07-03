package me.legadyn.noblockdrop.renderer;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NoBlockDropGlueRenderer extends SuperGlueRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("create", "textures/special/glue.png");
    public NoBlockDropGlueRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(SuperGlueEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(SuperGlueEntity entity) {
        return TEXTURE;
    }
}

