package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.List;

import de.maxhenkel.gravestone.DeathInfo.ItemInfo;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import scala.actors.threadpool.Arrays;

public class DeathPosition {

	public static final String REPLACEABLE_BLOCKS="replaceable_blocks";
	public static final String[] DEFAULT_BLOCKS = new String[] { "minecraft:tallgrass", "minecraft:water",
			"minecraft:lava", "minecraft:yellow_flower", "minecraft:red_flower", "minecraft:double_plant",
			"minecraft:sapling", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:torch",
			"minecraft:snow_layer", "minecraft:vine", "minecraft:deadbush", "minecraft:reeds", "minecraft:fire" };
	
	private EntityPlayer player;
	private World world;
	private BlockPos deathPosition;
	private BlockPos gravePosition;
	private static ArrayList<Block> replaceableBlocks;
	private List<ItemStack> drops;
	private long time;
	
	public DeathPosition(EntityPlayer player) {
		this.player = player;
		this.world = player.worldObj;
		this.deathPosition = player.getPosition();
		this.gravePosition=deathPosition;
		this.drops=new ArrayList<ItemStack>();
		this.time=System.currentTimeMillis();
		
		if (replaceableBlocks == null) {
			try {
				String[] blocks = Main.getInstance().getConfig().getStringArray(REPLACEABLE_BLOCKS, DEFAULT_BLOCKS);//c.get("Gravestone", "Replaceable blocks", DEFAULT_BLOCKS).getStringList();

				replaceableBlocks = getBlocks(blocks);

				if (blocks == null) {
					replaceableBlocks = getBlocks(DEFAULT_BLOCKS);
				}
			} catch (Exception e) {
				replaceableBlocks = new ArrayList<Block>();
			}
		}
	}

	public boolean placeGraveStone(List<EntityItem> drops) {

		try {
			this.gravePosition = getGraveStoneLocation();
		} catch (BlockNotFoundException e) {
			this.gravePosition=deathPosition;
			return false;
		}

		try {
			world.setBlockState(gravePosition, MBlocks.GRAVESTONE.getDefaultState().withProperty(BlockGraveStone.FACING,
					player.getHorizontalFacing().getOpposite()));
			
			if(isReplaceable(gravePosition.down())){
				world.setBlockState(gravePosition.down(), Blocks.DIRT.getDefaultState());
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

			graveTileEntity.setPlayerName(player.getDisplayNameString());
			graveTileEntity.setPlayerUUID(player.getUniqueID().toString());
			graveTileEntity.setDeathTime(time);

			addItems(graveTileEntity, drops);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private void addItems(TileEntityGraveStone graveStone, List<EntityItem> items) {
		try {
			for (int i=0; i<items.size(); i++) {
				try {
					EntityItem item=items.get(i);
					ItemStack stack=item.getEntityItem();
					drops.add(stack);
					graveStone.setInventorySlotContents(i, stack);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BlockPos getGraveStoneLocation() throws BlockNotFoundException {
		BlockPos location = new BlockPos(deathPosition.getX(), deathPosition.getY(), deathPosition.getZ());

		if (location.getY() < 1) {
			location = new BlockPos(location.getX(), 1, location.getZ());
		}

		for (int i = 1; i < player.worldObj.getHeight(); i++) {
			if (isReplaceable(location)) {
				return location;
			}

			if (location.getY() >= player.worldObj.getHeight()) {
				break;
			}

			location = location.add(0, 1, 0);
		}

		throw new BlockNotFoundException("No free Block above death Location");
	}

	private boolean isReplaceable(BlockPos pos) {
		Block b = world.getBlockState(pos).getBlock();

		if (b.getUnlocalizedName().equals(Blocks.AIR.getUnlocalizedName())) {
			return true;
		}

		for (Block replaceableBlock : replaceableBlocks) {
			if (b.getUnlocalizedName().equals(replaceableBlock.getUnlocalizedName())) {
				return true;
			}
		}
		return false;
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
	
	public void givePlayerNote(){
		ItemInfo[] items=new ItemInfo[drops.size()];
		for(int i=0; i<drops.size(); i++){
			ItemStack stack=drops.get(i);
			if(stack!=null){
				items[i]=new ItemInfo(Tools.getStringFromItem(stack.getItem()), stack.stackSize);
			}
		}
		
		DeathInfo info=new DeathInfo(gravePosition, player.dimension, items, player.getDisplayNameString(), time);
		ItemStack stack=new ItemStack(MItems.DEATH_INFO);
		
		info.addToItemStack(stack);
		player.inventory.addItemStackToInventory(stack);
	}
	
	public static void givePlayerNote(EntityPlayer player){
		DeathInfo info=new DeathInfo(player.getPosition(), player.dimension, new ItemInfo[0], player.getDisplayNameString(), System.currentTimeMillis());
		ItemStack stack=new ItemStack(MItems.DEATH_INFO);
		
		info.addToItemStack(stack);
		player.inventory.addItemStackToInventory(stack);
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getDeathPosition() {
		return deathPosition;
	}

	public static ArrayList<Block> getReplaceableBlocks() {
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
