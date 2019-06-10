package de.maxhenkel.gravestone.gui;

import de.maxhenkel.gravestone.DeathInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryDeathItems implements IInventory {

    private NonNullList<ItemStack> items;
    private int invSize;

    public InventoryDeathItems(DeathInfo deathInfo) {
        invSize = 54;
        items = NonNullList.withSize(invSize, ItemStack.EMPTY);

        for (int i = 0; i < deathInfo.getItems().size() && i < invSize; i++) {
            items.set(i, deathInfo.getItems().get(i));
        }
    }

    @Override
    public int getSizeInventory() {
        return invSize;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public void clear() {

    }

}