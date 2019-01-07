package de.maxhenkel.gravestone.blocks;

import java.util.Random;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockGraveStone extends BlockContainer {

    public static final String NAME = "gravestone";

    public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockGraveStone() {
        super(Builder.create(Material.CACTUS, MapColor.DIRT).hardnessAndResistance(0.3F, Float.MAX_VALUE));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
        this.setRegistryName(Main.MODID, NAME);
        //this.setCreativeTab(CreativeTabs.DECORATIONS);
        //this.setHardness(0.3F);
        //this.setResistance(Float.MAX_VALUE);
        //this.useNeighborBrightness = true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
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
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    /*

                @Override
                public IBlockState getStateFromMeta(int meta) {
                    EnumFacing enumFacing = EnumFacing.getFront(meta);

                    if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
                        enumFacing = EnumFacing.NORTH;
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
                public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                                        float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
                    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
                }

                @Override
                public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                    return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.11F, 1.0F);
                }

                @Override
                public boolean isFullBlock(IBlockState state) {
                    return false;
                }
            */
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
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public boolean canSilkHarvest(IBlockState p_canSilkHarvest_1_, IWorldReader p_canSilkHarvest_2_, BlockPos p_canSilkHarvest_3_, EntityPlayer p_canSilkHarvest_4_) {
        return true;
    }

    @Override
    public Item asItem() {
        return Items.AIR;
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tileentity = world.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityGraveStone)) {
            return super.onBlockActivated(state, world, pos, player, hand, facing, x, y, z);
        }

        TileEntityGraveStone grave = (TileEntityGraveStone) tileentity;

        return displayGraveInfo(grave, player);
    }

    /*
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityGraveStone) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityGraveStone) tileentity);
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
        return (Item) null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityGraveStone)) {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY);
        }

        TileEntityGraveStone grave = (TileEntityGraveStone) tileentity;

        return displayGraveInfo(grave, playerIn);

    }*/

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    /*@Override
    public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
        return super.getCollisionShape(p_196268_1_, p_196268_2_, p_196268_3_);
    }

    @Override
    public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        return super.getRenderShape(p_196247_1_, p_196247_2_, p_196247_3_);
    }*/

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return new TileEntityGraveStone();
    }
}
