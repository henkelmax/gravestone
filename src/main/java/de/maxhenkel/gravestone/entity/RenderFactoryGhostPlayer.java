package de.maxhenkel.gravestone.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBlaze;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryGhostPlayer implements IRenderFactory{

	@Override
	public Render createRenderFor(RenderManager manager) {
		return new RenderPlayerGhost(manager);
	}

}
