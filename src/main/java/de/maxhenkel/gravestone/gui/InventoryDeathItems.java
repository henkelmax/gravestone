package de.maxhenkel.gravestone.gui;

import de.maxhenkel.gravestone.DeathInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import javax.annotation.Nullable;

public class InventoryDeathItems implements IInventory {

    private NonNullList<ItemStack> items;
    private int invSize;

    public InventoryDeathItems(DeathInfo deathInfo) {
        invSize=54;
        items = NonNullList.withSize(invSize, ItemStack.EMPTY);

        for(int i=0; i<deathInfo.getItems().size()&&i<invSize; i++){
            items.set(i, deathInfo.getItems().get(i));
        }
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentTranslation("gui.deathitems");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
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
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer entityPlayer) {

    }

    @Override
    public void closeInventory(EntityPlayer entityPlayer) {

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
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

}