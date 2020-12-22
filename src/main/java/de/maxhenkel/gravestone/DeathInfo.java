package de.maxhenkel.gravestone;

import java.util.Arrays;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import scala.reflect.internal.Trees.This;

public class DeathInfo {
	public static final String KEY_INFO = "info";
	public static final String KEY_POS_X = "pos_x";
	public static final String KEY_POS_Y = "pos_y";
	public static final String KEY_POS_Z = "pos_z";
	public static final String KEY_DIM = "dim";
	public static final String KEY_TIME = "time";
	public static final String KEY_ITEMS = "items";
	public static final String KEY_NAME = "name";
	public static final String KEY_UUID = "uuid";

	private BlockPos deathLocation;
	private int dimension;
	private ItemInfo[] items;
	private String name;
	private UUID uuid;
	private long time;

	public DeathInfo(BlockPos deathLocation, int dimension, ItemInfo[] items, String name, long time, UUID uuid) {
		this.deathLocation = deathLocation;
		this.dimension = dimension;
		this.items = items;
		this.name = name;
		this.time = time;
		this.uuid=uuid;
	}

	public BlockPos getDeathLocation() {
		return deathLocation;
	}

	public int getDimension() {
		return dimension;
	}

	public ItemInfo[] getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

	public long getTime() {
		return time;
	}

	public UUID getUuid() {
		return uuid;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setInteger(KEY_POS_X, deathLocation.getX());
		compound.setInteger(KEY_POS_Y, deathLocation.getY());
		compound.setInteger(KEY_POS_Z, deathLocation.getZ());

		compound.setInteger(KEY_DIM, dimension);
		compound.setString(KEY_NAME, name);
		compound.setString(KEY_UUID, uuid.toString());
		compound.setLong(KEY_TIME, time);

		NBTTagList itemList = new NBTTagList();

		for (ItemInfo s : items) {
			itemList.appendTag(s.toNBT());
		}

		compound.setTag(KEY_ITEMS, itemList);

		return compound;
	}

	public void addToItemStack(ItemStack stack) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag(KEY_INFO, toNBT());
		stack.setTagCompound(compound);
	}

	public static DeathInfo fromNBT(NBTTagCompound compound) {
		try{
			int x = compound.getInteger(KEY_POS_X);
			int y = compound.getInteger(KEY_POS_Y);
			int z = compound.getInteger(KEY_POS_Z);

			BlockPos deathLocation = new BlockPos(x, y, z);

			int dimension = compound.getInteger(KEY_DIM);
			String name = compound.getString(KEY_NAME);
			String uuid ="";
			
			if(compound.hasKey(KEY_UUID)){
				uuid=compound.getString(KEY_UUID);
			}
			
			long time = compound.getLong(KEY_TIME);

			NBTTagList itemList = (NBTTagList) compound.getTag(KEY_ITEMS);
			ItemInfo[] items = new ItemInfo[itemList.tagCount()];

			for (int i = 0; i < itemList.tagCount(); i++) {
				NBTTagCompound s = itemList.getCompoundTagAt(i);
				items[i] = ItemInfo.fromNBT(s);
			}

			return new DeathInfo(deathLocation, dimension, items, name, time, UUID.fromString(uuid));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static DeathInfo getDeathInfoFromPlayerHand(EntityPlayer player) {
		ItemStack stack = null;

		if (player.getHeldItemMainhand() != null && isDeathInfoItem(player.getHeldItemMainhand().getItem())) {
			stack = player.getHeldItemMainhand();
		} else if (player.getHeldItemOffhand() != null && isDeathInfoItem(player.getHeldItemOffhand().getItem())) {
			stack = player.getHeldItemOffhand();
		} else {
			return null;
		}

		if (!stack.hasTagCompound()) {
			return null;
		}

		NBTTagCompound compound = stack.getTagCompound();

		if (!compound.hasKey(KEY_INFO)) {
			return null;
		}

		NBTTagCompound info = compound.getCompoundTag(KEY_INFO);

		if (info == null) {
			return null;
		}

		return fromNBT(info);
	}

	public static boolean isDeathInfoItem(Item item) {
		if (item == null) {
			return false;
		} else {
			return ModItems.DEATH_INFO.equals(item);
		}
	}

	public static boolean isDeathInfoItem(ItemStack item) {
		if (item == null) {
			return false;
		} else {
			return isDeathInfoItem(item.getItem());
		}
	}

	public static class ItemInfo {
		public static final String KEY_NAME = "name";
		public static final String KEY_STACK_SIZE = "stacksize";
		public static final String KEY_META = "meta";

		private String name;
		private int stackSize;
		private int meta;

		public ItemInfo(String name, int stackSize, int meta) {
			this.name = name;
			this.stackSize = stackSize;
			this.meta=meta;
		}

		public String getName() {
			return name;
		}

		public int getStackSize() {
			return stackSize;
		}
		
		public int getMeta() {
			return meta;
		}

		public NBTTagCompound toNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString(KEY_NAME, name);
			compound.setInteger(KEY_STACK_SIZE, stackSize);
			compound.setInteger(KEY_META, meta);
			return compound;
		}

		public static ItemInfo fromNBT(NBTTagCompound compound) {
			int meta=0;
			try{
				meta=compound.getInteger(KEY_META);
			}catch(Exception e){}
			
			
			return new ItemInfo(compound.getString(KEY_NAME), compound.getInteger(KEY_STACK_SIZE), meta);
		}

	}

}
