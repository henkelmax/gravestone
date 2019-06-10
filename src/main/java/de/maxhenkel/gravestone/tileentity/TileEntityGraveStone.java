package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGraveStone extends TileEntity implements IInventory {

    public static final int INVENTORY_SIZE = 127;
    private Inventory inventory;
    private String playerName;
    private String playerUUID;
    private long deathTime;
    private boolean renderHead;
    private static final String TAG_NAME = "ItemStacks";
    private static final String INV_NAME = "GraveInventory";
    private static final String PLAYER_NAME = "PlayerName";
    private static final String PLAYER_UUID = "PlayerUUID";
    private static final String DEATH_TIME = "DeathTime";
    private static final String RENDER_HEAD = "RenderHead";

    public TileEntityGraveStone() {
        super(Main.GRAVESTONE_TILEENTITY);
        this.inventory = new Inventory(INVENTORY_SIZE);
        this.playerName = "";
        this.deathTime = 0L;
        this.playerUUID = "";
        this.renderHead = true;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putString(PLAYER_NAME, playerName);
        compound.putLong(DEATH_TIME, deathTime);
        compound.putString(PLAYER_UUID, playerUUID);
        compound.putBoolean(RENDER_HEAD, renderHead);


        ListNBT list = new ListNBT();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
                CompoundNBT tag = inventory.getStackInSlot(i).serializeNBT();
                list.add(tag);
            }
        }

        compound.put(TAG_NAME, list);

        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        ListNBT list = compound.getList(TAG_NAME, 10);
        this.inventory = new Inventory(list.size());

        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            ItemStack stack = ItemStack.read(tag);
            inventory.setInventorySlotContents(i, stack);
        }

        this.playerName = compound.getString(PLAYER_NAME);
        this.playerUUID = compound.getString(PLAYER_UUID);
        this.deathTime = compound.getLong(DEATH_TIME);
        this.renderHead = compound.getBoolean(RENDER_HEAD);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.inventory.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        this.inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return inventory.isItemValidForSlot(index, stack);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return this.inventory.removeStackFromSlot(index);
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return inventory.isUsableByPlayer(player);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        markDirty();
    }

    public long getDeathTime() {
        return this.deathTime;
    }

    public void setDeathTime(long time) {
        this.deathTime = time;
        markDirty();
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
        markDirty();
    }

    public boolean renderHead() {
        return renderHead;
    }

    public void setRenderHead(boolean renderHead) {
        this.renderHead = renderHead;
        markDirty();
    }

    public String getTimeString() {
        return Tools.timeToString(deathTime);
    }
}
