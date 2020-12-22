package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.items.ItemDeathInfo;
import de.maxhenkel.gravestone.items.ItemGraveStone;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModItems {
	public static final ItemDeathInfo DEATH_INFO=new ItemDeathInfo();
	public static final ItemGraveStone GRAVESTONE=new ItemGraveStone(ModBlocks.GRAVESTONE);
}
