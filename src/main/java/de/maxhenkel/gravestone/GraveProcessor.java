package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.List;
import de.maxhenkel.gravestone.DeathInfo.ItemInfo;
import de.maxhenkel.gravestone.blocks.BlockGraveStone;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.util.NoSpaceException;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import scala.actors.threadpool.Arrays;

public class GraveProcessor {

	private EntityLivingBase entity;
	private World world;
	private BlockPos deathPosition;
	private BlockPos gravePosition;
	private static List<Block> replaceableBlocks;
	private List<ItemStack> drops;
	private long time;
	
	public GraveProcessor(EntityLivingBase entity) {
		this.entity = entity;
		this.world = entity.worldObj;
		this.deathPosition = entity.getPosition();
		this.gravePosition=deathPosition;
		this.drops=new ArrayList<ItemStack>();
		this.time=System.currentTimeMillis();
		
		if (replaceableBlocks == null) {
			replaceableBlocks=Config.instance().replaceableBlocks;
		}
	}

	public boolean placeGraveStone(List<EntityItem> drops) {
		
		for(EntityItem ei:drops){
			this.drops.add(ei.getEntityItem());
		}
		
		try {
			this.gravePosition = getGraveStoneLocation();
		} catch (NoSpaceException e) {
			this.gravePosition=deathPosition;
			Log.i("Grave from '" +entity.getName() +"' cant be created (No space)");
			return false;
		}

		try {
			world.setBlockState(gravePosition, ModBlocks.GRAVESTONE.getDefaultState().withProperty(BlockGraveStone.FACING,
					entity.getHorizontalFacing().getOpposite()));
			
			if(isReplaceable(gravePosition.down())){
				world.setBlockState(gravePosition.down(), Blocks.dirt.getDefaultState());
			}
			
			
		} catch (Exception e) {
			return false;
		}

		TileEntity tileentity = world.getTileEntity(gravePosition);

		if (tileentity == null || !(tileentity instanceof TileEntityGraveStone)) {
			return false;
		}

		try {
			TileEntityGraveStone graveTileEntity = (TileEntityGraveStone) tileentity;

			graveTileEntity.setPlayerName(entity.getName());
			graveTileEntity.setPlayerUUID(entity.getUniqueID().toString());
			graveTileEntity.setDeathTime(time);
			
			graveTileEntity.setRenderHead(entity instanceof EntityPlayer);

			addItems(graveTileEntity, drops);
			
		} catch (Exception e) {
			Log.w("Failed to fill gravestone with data");
		}

		return true;
	}

	private void addItems(TileEntityGraveStone graveStone, List<EntityItem> items) {
		try {
			for (int i=0; i<items.size(); i++) {
				EntityItem item=items.get(i);
				try {
					ItemStack stack=item.getEntityItem();
					graveStone.setInventorySlotContents(i, stack);
				} catch (Exception e) {
					Log.w("Failed to add Item '" +item.getEntityItem().getUnlocalizedName() +"' to gravestone");
				}
			}
		} catch (Exception e) {
			Log.w("Failed to add Ites to gravestone");
		}
	}

	public BlockPos getGraveStoneLocation() throws NoSpaceException {
		BlockPos location = new BlockPos(deathPosition.getX(), deathPosition.getY(), deathPosition.getZ());
		
		if (location.getY() < 1) {
			location = new BlockPos(location.getX(), 1, location.getZ());
		}

		while (location.getY()<entity.worldObj.getHeight()) {
			if (isReplaceable(location)) {
				return location;
			}

			location = location.add(0, 1, 0);
		}
		
		throw new NoSpaceException("No free Block above death Location");
	}

	private boolean isReplaceable(BlockPos pos) {
		Block b = world.getBlockState(pos).getBlock();

		if (b.getUnlocalizedName().equals(Blocks.air.getUnlocalizedName())) {
			return true;
		}

		for (Block replaceableBlock : replaceableBlocks) {
			if (b.getUnlocalizedName().equals(replaceableBlock.getUnlocalizedName())) {
				return true;
			}
		}
		return false;
	}
	
	public void givePlayerNote(){
		
		if(!(entity instanceof EntityPlayer)){
			return;
		}
		
		EntityPlayer player=(EntityPlayer) entity;
		
		ItemInfo[] items=new ItemInfo[drops.size()];
		for(int i=0; i<drops.size(); i++){
			ItemStack stack=drops.get(i);
			if(stack!=null){
				items[i]=new ItemInfo(Tools.getStringFromItem(stack.getItem()), stack.stackSize, stack.getMetadata());
			}
		}
		
		DeathInfo info=new DeathInfo(gravePosition, player.dimension, items, player.getName(), time, player.getUniqueID());
		ItemStack stack=new ItemStack(ModItems.DEATH_INFO);
		
		info.addToItemStack(stack);
		player.inventory.addItemStackToInventory(stack);
	}

	public EntityLivingBase getEntity() {
		return entity;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getDeathPosition() {
		return deathPosition;
	}

	public static List<Block> getReplaceableBlocks() {
		return replaceableBlocks;
	}

	public List<ItemStack> getDrops() {
		return drops;
	}

	public long getTime() {
		return time;
	}

	public BlockPos getGravePosition() {
		return gravePosition;
	}

}
