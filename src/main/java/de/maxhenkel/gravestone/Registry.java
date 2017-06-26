package de.maxhenkel.gravestone;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Registry {

	public static void addRenderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		//ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void registerItem(Item i) {
		ForgeRegistries.ITEMS.register(i);
		//GameRegistry.register(i);
	}
	
	public static void addRenderBlock(Block b) {
		//ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
	}
	
	public static void registerBlock(Block b) {
		ForgeRegistries.BLOCKS.register(b);
		//GameRegistry.register(b);
	}

	public static void registerItemBlock(Block b) {
		//GameRegistry.register(b);
		ForgeRegistries.BLOCKS.register(b);
		ForgeRegistries.ITEMS.register(new ItemBlock(b).setRegistryName(b.getRegistryName()));
		//GameRegistry.register(new ItemBlock(b).setRegistryName(b.getRegistryName()));
	}
	
	public static void regiserRecipe(IRecipe recipe){
		ForgeRegistries.RECIPES.register(recipe);
	}
	
}
