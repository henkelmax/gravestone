package de.maxhenkel.gravestone.proxy;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.Log;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.ModBlocks;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.entity.EntityGhostPlayer;
import de.maxhenkel.gravestone.events.BlockEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.events.UpdateCheckEvents;
import de.maxhenkel.gravestone.gui.GuiHandler;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preinit(FMLPreInitializationEvent event) {
		Configuration c = null;
		try {
			c = new Configuration(event.getSuggestedConfigurationFile());
			Config config = new Config(c);
			config.setInstance();
		} catch (Exception e) {
			Log.w("Could not create config file: " + e.getMessage());
		}

		Log.setLogger(event.getModLog());

	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new UpdateCheckEvents());
		MinecraftForge.EVENT_BUS.register(new DeathEvents());
		MinecraftForge.EVENT_BUS.register(new BlockEvents());
		registerBlock(ModBlocks.GRAVESTONE);
		registerItem(ModItems.DEATH_INFO);

		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance(), new GuiHandler());

		GameRegistry.registerTileEntity(TileEntityGraveStone.class, "TileEntityGaveStone");

		GameRegistry.addRecipe(new ItemStack(ModBlocks.GRAVESTONE), new Object[] { "CXX", "CXX", "DDD",
				Character.valueOf('C'), Blocks.COBBLESTONE, Character.valueOf('D'), Blocks.DIRT });

		GameRegistry.addRecipe(new ItemStack(ModBlocks.GRAVESTONE), new Object[] { "XXC", "XXC", "DDD",
				Character.valueOf('C'), Blocks.COBBLESTONE, Character.valueOf('D'), Blocks.DIRT });

		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "player_ghost"), EntityGhostPlayer.class,
				"player_ghost", 378, Main.instance(), 32, 1, true);

	}

	public void postinit(FMLPostInitializationEvent event) {

	}

	private void registerItem(Item i) {
		GameRegistry.register(i);
	}

	private void registerBlock(Block b) {
		GameRegistry.register(b);
		GameRegistry.register(new ItemBlock(b).setRegistryName(b.getRegistryName()));
	}

}
