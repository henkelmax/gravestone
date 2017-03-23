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
import net.minecraft.util.text.TextComponentTranslation;

public class Tools {

	public static String translate(String str, Object... args) {
		return new TextComponentTranslation(str, args).getFormattedText();
	}

	public static String dimIDToString(int id) {
		Map<Integer, String> dims = Config.instance().dimensionNames;

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

		return item.getRegistryName().getResourceDomain() + ":" + item.getRegistryName().getResourcePath();
	}

	public static String translateItem(String str, int meta) {
		if (str == null) {
			return null;
		}

		Item i = Item.REGISTRY.getObject(new ResourceLocation(str));

		if (i == null) {
			return null;
		}

		return i.getItemStackDisplayName(new ItemStack(i, 1, meta));
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
		SimpleDateFormat sdf = new SimpleDateFormat(Config.instance().dateFormat);
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
				Block b = Block.REGISTRY.getObject(new ResourceLocation(split[0], split[1]));
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
