package de.maxhenkel.gravestone.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class GhostPlayerRenderFactory implements IRenderFactory<GhostPlayerEntity> {

    @Override
    public EntityRenderer<? super GhostPlayerEntity> createRenderFor(EntityRendererManager entityRendererManager) {
        return new PlayerGhostRenderer(entityRendererManager);

    }
}
