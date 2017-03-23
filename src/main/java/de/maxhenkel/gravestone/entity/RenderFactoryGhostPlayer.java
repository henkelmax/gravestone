package de.maxhenkel.gravestone.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryGhostPlayer implements IRenderFactory<EntityGhostPlayer>{

	@Override
	public Render<EntityGhostPlayer> createRenderFor(RenderManager manager) {
		return new RenderPlayerGhost(manager);
	}

}
