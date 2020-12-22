package de.maxhenkel.gravestone.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.util.BlockPos;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGraveStone extends BlockContainer {

	public static final String NAME = "gravestone";

	public BlockGraveStone() {
		super(new Material(MapColor.dirtColor));

		setBlockName(NAME);

		//this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setHardness(0.3F);
		this.setResistance(Float.MAX_VALUE);
		this.useNeighborBrightness = true;
		
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return ModItems.GRAVESTONE;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("minecraft:dirt");
	}

	@Override
	public void onBlockDestroyedByExplosion(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_,
			Explosion p_149723_5_) {
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {

	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_,
			int p_149747_5_) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_,
			int p_149646_5_) {
		return true;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_) {
		return super.getDamageValue(p_149643_1_, p_149643_2_, p_149643_3_, p_149643_4_);
	}

	@Override
	public void onBlockPlacedBy(World world, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase entity,
			ItemStack p_149689_6_) {
		int l = ((MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		world.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 3);
		super.onBlockPlacedBy(world, p_149689_2_, p_149689_3_, p_149689_4_, entity, p_149689_6_);
	}

	public int func_150162_k(int p_150162_1_) {
		return p_150162_1_ & 3;
	}

	@Override
	public int getRenderType() {
		return -1;// 31;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_,
			int p_149719_4_) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.11F, 1.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityGraveStone();
	}

	@Override
	public void breakBlock(World worldIn, int x, int y, int z, Block block, int damage) {
		TileEntity tileentity = worldIn.getTileEntity(x, y, z);

		if (tileentity instanceof TileEntityGraveStone) {
			Tools.dropInventoryItems(worldIn, new BlockPos(x, y, z), (TileEntityGraveStone) tileentity);
			// worldIn.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(worldIn, x, y, z, block, damage);
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return (Item) null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int damage, float hitX,
			float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		}

		TileEntity tileentity = worldIn.getTileEntity(x, y, z);

		if (!(tileentity instanceof TileEntityGraveStone)) {
			return super.onBlockActivated(worldIn, x, y, z, playerIn, damage, hitX, hitY, hitZ);
		}

		TileEntityGraveStone grave = (TileEntityGraveStone) tileentity;

		return displayGraveInfo(grave, playerIn);
	}

	private boolean displayGraveInfo(TileEntityGraveStone grave, EntityPlayer player) {
		String name = grave.getPlayerName();
		String time = grave.getTimeString();

		if (name == null || name.isEmpty()) {
			return true;
		}

		if (time == null || time.isEmpty()) {
			player.addChatMessage(new ChatComponentText(name));
		} else {
			player.addChatMessage(new ChatComponentText(Tools.translate("message.died", name, time)));
		}

		return true;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

}
