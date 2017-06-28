package de.maxhenkel.gravestone.proxy;

import de.maxhenkel.gravestone.ModBlocks;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.Registry;
import de.maxhenkel.gravestone.entity.EntityGhostPlayer;
import de.maxhenkel.gravestone.entity.RenderFactoryGhostPlayer;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.tileentity.TileentitySpecialRendererGraveStone;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{
 
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit(event);

		RenderingRegistry.registerEntityRenderingHandler(EntityGhostPlayer.class, new RenderFactoryGhostPlayer());
	}
	
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGraveStone.class, new TileentitySpecialRendererGraveStone());
		Registry.addRenderBlock(ModBlocks.GRAVESTONE);
		Registry.addRenderItem(ModItems.DEATH_INFO);

	}
	
	public void postinit(FMLPostInitializationEvent event) {
		super.postinit(event);
	}

}
