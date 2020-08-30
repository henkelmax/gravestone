package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;

public class DeathInfo {
    public static final String KEY_INFO = "info";
    public static final String KEY_POS_X = "pos_x";
    public static final String KEY_POS_Y = "pos_y";
    public static final String KEY_POS_Z = "pos_z";
    public static final String KEY_DIM = "dim";
    public static final String KEY_TIME = "time";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_NAME = "mod.name"; //TODO fix name
    public static final String KEY_UUID = "uuid";

    private BlockPos deathLocation;
    private String dimension;
    private List<ItemStack> items;
    private String name;
    private UUID uuid;
    private long time;

    public DeathInfo(BlockPos deathLocation, String dimension, List<ItemStack> items, String name, long time, UUID uuid) {
        this.deathLocation = deathLocation;
        this.dimension = dimension;
        this.items = items;
        this.name = name;
        this.time = time;
        this.uuid = uuid;
    }

    public BlockPos getDeathLocation() {
        return deathLocation;
    }

    public String getDimension() {
        return dimension;
    }

    public List<ItemStack> getItems() {
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

    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();

        compound.putInt(KEY_POS_X, deathLocation.getX());
        compound.putInt(KEY_POS_Y, deathLocation.getY());
        compound.putInt(KEY_POS_Z, deathLocation.getZ());

        compound.putString(KEY_DIM, dimension);
        compound.putString(KEY_NAME, name);
        compound.putString(KEY_UUID, uuid.toString());
        compound.putLong(KEY_TIME, time);

        ListNBT itemList = new ListNBT();

        for (ItemStack s : items) {
            itemList.add(s.serializeNBT());
        }

        compound.put(KEY_ITEMS, itemList);

        return compound;
    }

    public void addToItemStack(ItemStack stack) {
        CompoundNBT compound = new CompoundNBT();
        compound.put(KEY_INFO, toNBT());
        stack.setTag(compound);
    }

    public static DeathInfo fromNBT(CompoundNBT compound) {
        try {
            int x = compound.getInt(KEY_POS_X);
            int y = compound.getInt(KEY_POS_Y);
            int z = compound.getInt(KEY_POS_Z);

            BlockPos deathLocation = new BlockPos(x, y, z);

            String dimension = compound.getString(KEY_DIM);
            String name = compound.getString(KEY_NAME);
            String uuid = "";

            if (compound.contains(KEY_UUID)) {
                uuid = compound.getString(KEY_UUID);
            }

            long time = compound.getLong(KEY_TIME);

            ListNBT itemList = (ListNBT) compound.get(KEY_ITEMS);
            List<ItemStack> items = new ArrayList<>();

            for (int i = 0; i < itemList.size(); i++) {
                CompoundNBT comp = itemList.getCompound(i);
                items.add(ItemStack.read(comp));
            }

            return new DeathInfo(deathLocation, dimension, items, name, time, UUID.fromString(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DeathInfo getDeathInfoFromPlayerHand(PlayerEntity player) {
        ItemStack stack;

        if (isDeathInfoItem(player.getHeldItemMainhand().getItem())) {
            stack = player.getHeldItemMainhand();
        } else if (isDeathInfoItem(player.getHeldItemOffhand().getItem())) {
            stack = player.getHeldItemOffhand();
        } else {
            return null;
        }

        if (!stack.hasTag()) {
            return null;
        }

        CompoundNBT compound = stack.getTag();

        if (compound == null || !compound.contains(KEY_INFO)) {
            return null;
        }

        CompoundNBT info = compound.getCompound(KEY_INFO);

        return fromNBT(info);
    }

    public static boolean isDeathInfoItem(Item item) {
        if (item == null) {
            return false;
        } else {
            return Main.DEATHINFO.equals(item);
        }
    }

    public static boolean isDeathInfoItem(ItemStack item) {
        if (item == null) {
            return false;
        } else {
            return isDeathInfoItem(item.getItem());
        }
    }
}
