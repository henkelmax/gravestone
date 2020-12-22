package de.maxhenkel.gravestone.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.ModBlocks;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.tileentity.TileentitySpecialRendererGraveStone;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy{
 
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit(event);
	}
	
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGraveStone.class, new TileentitySpecialRendererGraveStone());
		addRenderBlock(ModBlocks.GRAVESTONE);
		addRenderItem(ModItems.DEATH_INFO);
		addRenderItem(ModItems.GRAVESTONE);
	}
	
	public void postinit(FMLPostInitializationEvent event) {
		super.postinit(event);
	}
	
	private void addRenderItem(Item i){
		String name=i.getUnlocalizedName().replace("item.", "");
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0, new ModelResourceLocation(Main.MODID +":" +name, "inventory"));
	}
	
	private void addRenderBlock(Block b){
		String name=b.getUnlocalizedName().replace("tile.", "");
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(b), 0, new ModelResourceLocation(Main.MODID +":" +name, "inventory"));
	}

}
