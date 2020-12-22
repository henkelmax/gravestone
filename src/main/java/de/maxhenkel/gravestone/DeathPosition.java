package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeathPosition {

	private EntityPlayer player;
	private World world;
	private boolean keepInventory;
	private BlockPos deathPosition;
	private static ArrayList<Block> replaceableBlocks;
	public static final String[] DEFAULT_BLOCKS = new String[] { "minecraft:tallgrass", "minecraft:water",
			"minecraft:lava", "minecraft:yellow_flower", "minecraft:red_flower", "minecraft:double_plant",
			"minecraft:sapling", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:torch",
			"minecraft:snow_layer", "minecraft:vine", "minecraft:deadbush", "minecraft:reeds" };

	public DeathPosition(EntityPlayer player) {
		this.player = player;
		this.world = player.worldObj;
		this.deathPosition = player.getPosition();
		this.keepInventory = player.worldObj.getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("keepInventory");

		if (replaceableBlocks == null) {
			try {
				Configuration c = Main.getInstance().getConfig();
				c.load();

				String[] blocks = c.get("Gravestone", "Replaceable blocks", DEFAULT_BLOCKS).getStringList();

				replaceableBlocks = getBlocks(blocks);

				if (blocks == null) {
					replaceableBlocks = getBlocks(DEFAULT_BLOCKS);
				}

				c.save();
			} catch (Exception e) {
				replaceableBlocks = new ArrayList<Block>();

			}
		}
	}

	public boolean placeGraveStone(List<EntityItem> drops) {

		if (keepInventory) {
			return true;
		}

		BlockPos gravePos;

		try {
			gravePos = getGraveStoneLocation();
		} catch (BlockNotFoundException e) {
			return false;
		}

		try {
			world.setBlockState(gravePos, MBlocks.GRAVESTONE.getDefaultState().withProperty(BlockGraveStone.FACING,
					player.getHorizontalFacing().getOpposite()));
			
			if(isReplaceable(gravePos.down())){
				world.setBlockState(gravePos.down(), Blocks.dirt.getDefaultState());
			}
			
		} catch (Exception e) {
			return false;
		}

		TileEntity tileentity = world.getTileEntity(gravePos);

		if (tileentity == null || !(tileentity instanceof TileEntityGraveStone)) {
			return false;
		}

		try {
			TileEntityGraveStone graveTileEntity = (TileEntityGraveStone) tileentity;

			graveTileEntity.setPlayerName(player.getDisplayNameString());
			graveTileEntity.setDeathTime();

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

	public static Block getBlock(String name) {
		try {
			String[] split = name.split(":");
			if (split.length == 2) {
				Block b = Block.blockRegistry.getObject(new ResourceLocation(split[0], split[1]));
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

	public EntityPlayer getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public boolean isKeepInventory() {
		return keepInventory;
	}

	public BlockPos getDeathPosition() {
		return deathPosition;
	}

	public static ArrayList<Block> getReplaceableBlocks() {
		return replaceableBlocks;
	}

}
