package de.maxhenkel.gravestone.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import de.maxhenkel.gravestone.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Tools {

	public static String dimIDToString(int id) {
		Map<Integer, String> dims = Config.dimensionNames;

		String name = dims.get(id);
		if (name == null || name.isEmpty()) {
			return String.valueOf(id);
		}
		
		return name;
	}

	public static String getStringFromItem(Item item) {
		if (item == null) {
			return null;
		}

		return item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath();
	}

	public static String translateItem(String str) {
		if (str == null) {
			return null;
		}

		Item i = Item.REGISTRY.get(new ResourceLocation(str));

		if (i == null) {
			return null;
		}

		return i.getDisplayName(new ItemStack(i)).getUnformattedComponentText();
	}

	public static boolean isArrayEmpty(Object[] obj) {
		for (Object o : obj) {
			if (o != null) {
				return false;
			}
		}
		return true;
	}

	public static boolean keepInventory(EntityPlayer player) {
		try {
			return player.getEntityWorld().getWorldInfo().getGameRulesInstance().getBoolean("keepInventory");
		} catch (Exception e) {
			return false;
		}
	}

	public static String timeToString(long time) {
		if (time == 0L) {
			return "";
		}

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormat);
		return sdf.format(c.getTime());
	}

	public static ArrayList<Block> getBlocks(String[] names) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (String s : names) {
			Block b = getBlock(s);
			if (b != null) {
				blocks.add(b);
			}
		}
		return blocks;
	}
	
	public static Block getBlock(String name) {
		try {
			String[] split = name.split(":");
			if (split.length == 2) {
				Block b = Block.REGISTRY.get(new ResourceLocation(split[0], split[1]));
				if (b.equals(Blocks.AIR)) {
					return null;
				} else {
					return b;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
