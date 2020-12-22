package de.maxhenkel.gravestone.proxy;

import de.maxhenkel.gravestone.DeathPosition;
import de.maxhenkel.gravestone.Events;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.MBlocks;
import de.maxhenkel.gravestone.MItems;
import de.maxhenkel.gravestone.TileEntityGraveStone;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preinit(FMLPreInitializationEvent event) {
		registerBlock(MBlocks.GRAVESTONE);
		registerItem(MItems.DEATH_LOCATION_FINDER);
		
		
		GameRegistry.registerTileEntity(TileEntityGraveStone.class, "TileEntityGaveStone");

		GameRegistry.addRecipe(new ItemStack(MBlocks.GRAVESTONE), new Object[] { "CXX", "CXX", "DDD", Character.valueOf('C'), Blocks.cobblestone, Character.valueOf('D'), Blocks.dirt });

		GameRegistry.addRecipe(new ItemStack(MBlocks.GRAVESTONE), new Object[] { "XXC", "XXC", "DDD", Character.valueOf('C'), Blocks.cobblestone, Character.valueOf('D'), Blocks.dirt });
	
		boolean addLocationFinderReciepe=false;
		try {
			Main.getInstance().getConfig().load();
			addLocationFinderReciepe = Main.getInstance().getConfig().get(Main.MODID, "enable_death_location_finder_reciepe", false).getBoolean();
			Main.getInstance().getConfig().save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(addLocationFinderReciepe){
			GameRegistry.addRecipe(new ItemStack(MItems.DEATH_LOCATION_FINDER), new Object[] { "XRR", "XSR", "SXX", Character.valueOf('R'), Items.redstone, Character.valueOf('S'), Items.stick });
		}
		
	}

	public void init(FMLInitializationEvent event) {
		
	}

	public void postinit(FMLPostInitializationEvent event) {

	}

	private void registerItem(Item i) {
		GameRegistry.registerItem(i, i.getUnlocalizedName().replace("item.", ""));
	}

	private void registerBlock(Block b) {
		GameRegistry.registerBlock(b, b.getUnlocalizedName().replace("tile.", ""));
	}

}
