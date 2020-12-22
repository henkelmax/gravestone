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
import net.minecraft.block.state.BlockState;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGraveStone extends BlockContainer{

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockGraveStone() {
		super(new Material(MapColor.dirtColor));
		String name="gravestone";
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setUnlocalizedName(name);
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
	public boolean isOpaqueCube(){
        return false;
    }

	@Override
    public boolean isFullCube(){
        return false;
    }

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
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
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{FACING});
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos){
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.11F, 1.0F);
    }
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
    	return new TileEntityGraveStone();
    }

    @Override
    public int getRenderType() {
    	return 3;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
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
    			playerIn.addChatMessage(new ChatComponentTranslation(name));
    		}else{
    			String msg=name +" " +new ChatComponentTranslation("message.died", new Object[0]).getFormattedText() +" " +time;
        		playerIn.addChatMessage(new ChatComponentTranslation(msg));
    		}

    		return true;
    	}
    	return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }
    
    @Override
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
    	return false;
    }
    
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
    	return false;
    }
    
    @Override
    public boolean isReplaceable(World worldIn, BlockPos pos) {
    	return false;
    }
    
}
