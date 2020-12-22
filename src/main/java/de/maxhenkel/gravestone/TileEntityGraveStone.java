package de.maxhenkel.gravestone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;

public class TileEntityGraveStone extends TileEntity implements IInventory{

	private InventoryBasic inventory;
	private String playerName;
	private long deathTime;
	private static final String TAG_NAME="ItemStacks";
	private static final String INV_NAME="GraveInventory";
	private static final String PLAYER_NAME="PlayerName";
	private static final String DEATH_TIME="DeathTime";
	
	public TileEntityGraveStone() {
		this.inventory =new InventoryBasic(INV_NAME, false, 127);
		this.playerName="";
		this.deathTime=0L;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setString(PLAYER_NAME, playerName);
		compound.setLong(DEATH_TIME, deathTime);
		
		NBTTagList list=new NBTTagList();
		
		for(int i=0; i<inventory.getSizeInventory(); i++){
			if(inventory.getStackInSlot(i)!=null){
				NBTTagCompound tag=new NBTTagCompound();
				inventory.getStackInSlot(i).writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		
		compound.setTag(TAG_NAME, list);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		NBTTagList list= compound.getTagList(TAG_NAME, 10);
		this.inventory=new InventoryBasic(INV_NAME, false, list.tagCount());

		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound tag=list.getCompoundTagAt(i);
			inventory.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(tag));
		}
		
		this.playerName=compound.getString(PLAYER_NAME);
		this.deathTime=compound.getLong(DEATH_TIME);
		
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		syncData.setString(PLAYER_NAME, this.playerName);
	 	return new S35PacketUpdateTileEntity(this.pos, 1, syncData);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.playerName=pkt.getNbtCompound().getString(PLAYER_NAME);
		super.onDataPacket(net, pkt);
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public IChatComponent getDisplayName() {
		return inventory.getDisplayName();
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
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
	
	public String getPlayerName(){
		return this.playerName;
	}
	
	public void setPlayerName(String playerName){
		this.playerName=playerName;
	}
	
	public long getDeathTime(){
		return this.deathTime;
	}
	
	public void setDeathTime(){
		this.deathTime=System.currentTimeMillis();
	}
	
	public void setPlayerName(long time){
		this.deathTime=time;
	}
	
	public String getTimeString(){
		if(deathTime==0L){
			return "";
		}
		
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(this.deathTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(c.getTime());
	}

	@Override
	public String getCommandSenderName() {
		return inventory.getCommandSenderName();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return inventory.getStackInSlotOnClosing(index);
	}

}
