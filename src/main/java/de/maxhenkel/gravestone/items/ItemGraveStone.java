package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemReed;

public class ItemGraveStone extends ItemReed {
	
	public ItemGraveStone(Block block) {
		super(block);
		setUnlocalizedName(ModBlocks.GRAVESTONE.NAME);
		setCreativeTab(CreativeTabs.tabDecorations);
		setTextureName(Main.MODID +":" +ModBlocks.GRAVESTONE.NAME);
		
	}
}
