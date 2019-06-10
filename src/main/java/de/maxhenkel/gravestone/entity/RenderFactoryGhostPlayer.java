package de.maxhenkel.gravestone.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryGhostPlayer implements IRenderFactory<EntityGhostPlayer> {

    @Override
    public EntityRenderer<? super EntityGhostPlayer> createRenderFor(EntityRendererManager entityRendererManager) {
        return new RenderPlayerGhost(entityRendererManager);

    }
}
