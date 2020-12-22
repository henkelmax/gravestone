package de.maxhenkel.gravestone.proxy;

import de.maxhenkel.gravestone.DeathPosition;
import de.maxhenkel.gravestone.Events;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.MBlocks;
import de.maxhenkel.gravestone.MItems;
import de.maxhenkel.gravestone.TileEntityGraveStone;
import de.maxhenkel.gravestone.gui.GuiHandler;
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
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class CommonProxy {

	public void preinit(FMLPreInitializationEvent event) {
		registerBlock(MBlocks.GRAVESTONE);
		registerItem(MItems.DEATH_INFO);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.getInstance(), new GuiHandler());
		
		GameRegistry.registerTileEntity(TileEntityGraveStone.class, "TileEntityGaveStone");

		GameRegistry.addRecipe(new ItemStack(MBlocks.GRAVESTONE), new Object[] { "CXX", "CXX", "DDD", Character.valueOf('C'), Blocks.cobblestone, Character.valueOf('D'), Blocks.dirt });

		GameRegistry.addRecipe(new ItemStack(MBlocks.GRAVESTONE), new Object[] { "XXC", "XXC", "DDD", Character.valueOf('C'), Blocks.cobblestone, Character.valueOf('D'), Blocks.dirt });

	}

	public void init(FMLInitializationEvent event) {
		
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
