package de.maxhenkel.gravestone.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.util.IItemBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class BlockGraveStone extends Block implements ITileEntityProvider, IItemBlock, IBucketPickupHandler, ILiquidContainer {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockGraveStone() {
        super(Properties.create(Material.CACTUS, MaterialColor.DIRT).hardnessAndResistance(0.3F, Float.MAX_VALUE));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, false));
        this.setRegistryName(Main.MODID, "gravestone");
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(this.getRegistryName());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
        builder.add(WATERLOGGED);
    }

    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
        if (state.get(WATERLOGGED)) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, false), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public IFluidState getFluidState(IBlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
        return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
        if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
            if (!worldIn.isRemote()) {
                worldIn.setBlockState(pos, state.with(WATERLOGGED, true), 3);
                worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onBlockExploded(IBlockState state, World world, BlockPos pos, Explosion explosion) {

    }

    @Override
    public void onExplosionDestroy(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {

    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
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
    public boolean isNormalCube(IBlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canSilkHarvest(IBlockState p_canSilkHarvest_1_, IWorldReader p_canSilkHarvest_2_, BlockPos p_canSilkHarvest_3_, EntityPlayer p_canSilkHarvest_4_) {
        return true;
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState p_180643_1_) {
        return new ItemStack(this);
    }

    @Override
    public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tileentity = world.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityGraveStone)) {
            return true;
        }

        TileEntityGraveStone grave = (TileEntityGraveStone) tileentity;

        return displayGraveInfo(grave, player);
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    private boolean displayGraveInfo(TileEntityGraveStone grave, EntityPlayer player) {
        String name = grave.getPlayerName();
        String time = grave.getTimeString();

        if (name == null || name.isEmpty()) {
            return true;
        }

        if (time == null || time.isEmpty()) {
            player.sendMessage(new TextComponentString(name));
        } else {
            player.sendMessage(new TextComponentTranslation("message.died", name, time));
        }

        return true;
    }

    @Override
    public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockState state, IBlockReader reader, BlockPos pos) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getRenderShape(IBlockState state, IBlockReader reader, BlockPos pos) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader reader, BlockPos pos) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getRaytraceShape(IBlockState state, IBlockReader reader, BlockPos pos) {
        return SHAPES.get(state.get(FACING));
    }

    private static final VoxelShape BASE = Block.makeCuboidShape(0D, 0D, 0D, 16D, 2D, 16D);

    private static final Map<EnumFacing, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
            EnumFacing.NORTH,
            VoxelShapes.or(
                    BASE,
                    Block.makeCuboidShape(1D, 2D, 1D, 15D, 15D, 2D)
            ),
            EnumFacing.SOUTH,
            VoxelShapes.or(
                    BASE,
                    Block.makeCuboidShape(1D, 2D, 15D, 15D, 15D, 14D)
            ),
            EnumFacing.EAST,
            VoxelShapes.or(
                    BASE,
                    Block.makeCuboidShape(15D, 2D, 1D, 14D, 15D, 15D)
            ),
            EnumFacing.WEST,
            VoxelShapes.or(
                    BASE,
                    Block.makeCuboidShape(1D, 2D, 1D, 2D, 15D, 15D)
            )
    ));

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return createTileEntity(getDefaultState(), iBlockReader);
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        return new TileEntityGraveStone();
    }

}
