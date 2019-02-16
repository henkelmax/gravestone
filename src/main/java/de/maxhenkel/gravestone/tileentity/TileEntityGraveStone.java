package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import javax.annotation.Nullable;

public class TileEntityGraveStone extends TileEntity implements IInventory {

    public static final int INVENTORY_SIZE = 127;

    private InventoryBasic inventory;
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
        super(Main.graveTileEntity);
        this.inventory = new InventoryBasic(new TextComponentString(INV_NAME), INVENTORY_SIZE);
        this.playerName = "";
        this.deathTime = 0L;
        this.playerUUID = "";
        this.renderHead = true;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setString(PLAYER_NAME, playerName);
        compound.setLong(DEATH_TIME, deathTime);
        compound.setString(PLAYER_UUID, playerUUID);
        compound.setBoolean(RENDER_HEAD, renderHead);


        NBTTagList list = new NBTTagList();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
                NBTTagCompound tag = inventory.getStackInSlot(i).serializeNBT();
                list.add(tag);
            }
        }

        compound.setTag(TAG_NAME, list);

        return super.write(compound);
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        NBTTagList list = compound.getList(TAG_NAME, 10);
        this.inventory = new InventoryBasic(new TextComponentString(INV_NAME), list.size());

        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound tag = list.getCompound(i);
            ItemStack stack = ItemStack.read(tag);
            inventory.setInventorySlotContents(i, stack);
        }

        this.playerName = compound.getString(PLAYER_NAME);
        this.playerUUID = compound.getString(PLAYER_UUID);
        this.deathTime = compound.getLong(DEATH_TIME);
        this.renderHead = compound.getBoolean(RENDER_HEAD);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.write(new NBTTagCompound());
    }

    @Override
    public ITextComponent getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return inventory.getName();
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
    public void openInventory(EntityPlayer player) {
        this.inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        this.inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return inventory.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
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
    public boolean isUsableByPlayer(EntityPlayer player) {
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
