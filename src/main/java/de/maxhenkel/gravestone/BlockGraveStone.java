package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGraveStone extends BlockContainer{

	public static final String NAME="gravestone";
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockGraveStone() {
		super(new Material(MapColor.dirtColor));

		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setUnlocalizedName(NAME);
		this.setRegistryName(Main.MODID, NAME);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setHardness(0.3F);
		this.setResistance(Float.MAX_VALUE);
		this.useNeighborBrightness=true;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {

	}
	
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumFacing = EnumFacing.getFront(meta);
		
		if(enumFacing.getAxis()==EnumFacing.Axis.Y){
			enumFacing=EnumFacing.NORTH;
		}
		
		return this.getDefaultState().withProperty(FACING, enumFacing);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.11F, 1.0F);
	}
	
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
    	return new TileEntityGraveStone();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityGraveStone){
        	InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityGraveStone)tileentity);
        	worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
     }
    
    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    	return true;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    	return (Item)null;
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if(worldIn.isRemote){
    		return true;
    	}
    	
    	TileEntity tileentity = worldIn.getTileEntity(pos);
    	if(tileentity instanceof TileEntityGraveStone){
    		TileEntityGraveStone grave=(TileEntityGraveStone) tileentity;
    		String name=grave.getPlayerName();
    		String time=grave.getTimeString();
    		
    		if(name==null||name.isEmpty()){
    			return true;
    		}
    		
    		if(time==null||time.isEmpty()){
    			playerIn.addChatMessage(new TextComponentString(name));
    		}else{
    			String msg=name +" " +new TextComponentTranslation("message.died", new Object[0]).getFormattedText() +" " +time;
        		playerIn.addChatMessage(new TextComponentString(msg));
    		}

    		return true;
    	}
    	return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }
    
    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
    	return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
    	return false;
    }
    
    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
    	return false;
    }
    
}
