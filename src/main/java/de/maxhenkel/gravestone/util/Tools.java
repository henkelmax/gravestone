package de.maxhenkel.gravestone.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.maxhenkel.gravestone.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class Tools {

	public static String translate(String str, Object... args) {
		return new ChatComponentTranslation(str, args).getFormattedText();
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

		return item.itemRegistry.getNameForObject(item).toString();// RegistryName();//Check
	}

	public static String translateItem(String str, int meta) {
		if (str == null) {
			return null;
		}

		Item i = (Item) Item.itemRegistry.getObject(str);

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
			return player.worldObj.getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("keepInventory");
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
				Block b = (Block) Block.blockRegistry.getObject(new ResourceLocation(split[0], split[1]));
				if (b.equals(Blocks.air)) {
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

	public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inv) {
		drop(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), inv);
	}

	private static void drop(World worldIn, double p_180174_1_, double p_180174_3_, double p_180174_5_,
			IInventory p_180174_7_) {
		for (int i = 0; i < p_180174_7_.getSizeInventory(); ++i) {
			ItemStack itemstack = p_180174_7_.getStackInSlot(i);

			if (itemstack != null) {
				spawnItemStack(worldIn, p_180174_1_, p_180174_3_, p_180174_5_, itemstack);
			}
		}
	}

	private static void spawnItemStack(World worldIn, double p_180173_1_, double p_180173_3_, double p_180173_5_,
			ItemStack p_180173_7_) {
		Random random = new Random();
		float f = random.nextFloat() * 0.8F + 0.1F;
		float f1 = random.nextFloat() * 0.8F + 0.1F;
		float f2 = random.nextFloat() * 0.8F + 0.1F;

		while (p_180173_7_.stackSize > 0) {
			int i = random.nextInt(21) + 10;

			if (i > p_180173_7_.stackSize) {
				i = p_180173_7_.stackSize;
			}

			p_180173_7_.stackSize -= i;
			EntityItem entityitem = new EntityItem(worldIn, p_180173_1_ + (double) f, p_180173_3_ + (double) f1,
					p_180173_5_ + (double) f2, new ItemStack(p_180173_7_.getItem(), i, p_180173_7_.getItemDamage()));

			if (p_180173_7_.hasTagCompound()) {
				entityitem.getEntityItem().setTagCompound((NBTTagCompound) p_180173_7_.getTagCompound().copy());
			}

			float f3 = 0.05F;
			entityitem.motionX = random.nextGaussian() * (double) f3;
			entityitem.motionY = random.nextGaussian() * (double) f3 + 0.20000000298023224D;
			entityitem.motionZ = random.nextGaussian() * (double) f3;
			worldIn.spawnEntityInWorld(entityitem);
		}
	}
	
	public static int oppositeSite(EntityLivingBase entity){
		int l = ((MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		if(l==0){
			l=2;
		}else if(l==1){
			l=3;
		}else if(l==2){
			l=0;
		}else if(l==3){
			l=1;
		}
		
		return l;
	}

}
