package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class Registry {

	public static void addRenderItem(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	public static void addRenderBlock(Block b) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
	}

	public static void registerItem(IForgeRegistry<Item> registry, Item i) {
		registry.register(i);
	}

	public static void registerBlock(IForgeRegistry<Block> registry, Block b) {
		registry.register(b);
	}

	public static void registerItemBlock(IForgeRegistry<Item> registry, Block b) {
		registerItem(registry, new ItemBlock(b).setRegistryName(b.getRegistryName()));
	}

	public static void regiserRecipe(IForgeRegistry<IRecipe> registry, IRecipe recipe) {
		registry.register(recipe);
	}

	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "gravestone"), null,
				new ItemStack(ModBlocks.GRAVESTONE), new Object[] { "CXX", "CXX", "DDD", Character.valueOf('C'),
						Blocks.COBBLESTONE, Character.valueOf('D'), Blocks.DIRT, Character.valueOf('X'), Blocks.AIR });
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "gravestone"), null,
				new ItemStack(ModBlocks.GRAVESTONE), new Object[] { "XXC", "XXC", "DDD", Character.valueOf('C'),
						Blocks.COBBLESTONE, Character.valueOf('D'), Blocks.DIRT, Character.valueOf('X'), Blocks.AIR });
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event.getRegistry(), ModBlocks.GRAVESTONE);
		
		GameRegistry.registerTileEntity(TileEntityGraveStone.class, "TileEntityGaveStone");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		registerItemBlock(event.getRegistry(), ModBlocks.GRAVESTONE);
		registerItem(event.getRegistry(), ModItems.DEATH_INFO);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		addRenderBlock(ModBlocks.GRAVESTONE);
		addRenderItem(ModItems.DEATH_INFO);
	}

}
