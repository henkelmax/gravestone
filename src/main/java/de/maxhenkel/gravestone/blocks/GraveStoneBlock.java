package de.maxhenkel.gravestone.blocks;

import com.google.common.collect.ImmutableList;
import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GraveStoneBlock extends Block implements ITileEntityProvider, IItemBlock, IBucketPickupHandler, ILiquidContainer {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape BASE1 = Block.box(0D, 0D, 0D, 16D, 1D, 16D);
    private static final VoxelShape BASE2 = Block.box(1D, 1D, 1D, 15D, 2D, 15D);

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder()
            .direction(Direction.NORTH,
                    BASE1,
                    BASE2,
                    Block.box(1D, 2D, 1D, 15D, 12D, 2D),
                    Block.box(2D, 12D, 1D, 14D, 14D, 2D),
                    Block.box(3D, 14D, 1D, 13D, 15D, 2D))
            .direction(Direction.SOUTH,
                    BASE1,
                    BASE2,
                    Block.box(1D, 2D, 15D, 15D, 12D, 14D),
                    Block.box(2D, 12D, 15D, 14D, 14D, 14D),
                    Block.box(3D, 14D, 15D, 13D, 15D, 14D)
            ).direction(Direction.EAST,
                    BASE1,
                    BASE2,
                    Block.box(15D, 2D, 1D, 14D, 12D, 15D),
                    Block.box(15D, 12D, 2D, 14D, 14D, 14D),
                    Block.box(15D, 14D, 3D, 14D, 15D, 13D)
            ).direction(Direction.WEST,
                    BASE1,
                    BASE2,
                    Block.box(1D, 2D, 1D, 2D, 12D, 15D),
                    Block.box(1D, 12D, 2D, 2D, 14D, 14D),
                    Block.box(1D, 14D, 3D, 2D, 15D, 13D)
            ).build();

    public static final Material GRAVESTONE_MATERIAL = new Material(MaterialColor.DIRT, false, true, true, false, false, false, PushReaction.BLOCK);

    public GraveStoneBlock() {
        super(Properties.of(GRAVESTONE_MATERIAL, MaterialColor.DIRT).strength(0.3F, Float.MAX_VALUE));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
        setRegistryName(Main.MODID, "gravestone");
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)).setRegistryName(getRegistryName());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(WATERLOGGED);
    }

    @Override
    public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            world.setBlock(pos, state.setValue(WATERLOGGED, false), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
                world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof GraveStoneTileEntity) {
                GraveStoneTileEntity grave = (GraveStoneTileEntity) tileentity;
                grave.setCustomName(stack.getDisplayName());
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void wasExploded(World world, BlockPos pos, Explosion explosion) {

    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity tileentity = world.getBlockEntity(pos);

        if (!(tileentity instanceof GraveStoneTileEntity)) {
            return ActionResultType.FAIL;
        }

        GraveStoneTileEntity grave = (GraveStoneTileEntity) tileentity;

        ITextComponent name = grave.getGraveName();

        if (name == null) {
            return ActionResultType.FAIL;
        }

        if (world.isClientSide) {
            ITextComponent time = GraveUtils.getDate(grave.getDeath().getTimestamp());
            if (time == null) {
                player.sendMessage(name, Util.NIL_UUID);
            } else {
                player.sendMessage(new TranslationTextComponent("message.gravestone.died", name, time), Util.NIL_UUID);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof GraveStoneTileEntity) {
                dropItems(world, pos, ((GraveStoneTileEntity) tileentity).getDeath().getAllItems());
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public void dropItems(World world, BlockPos pos, NonNullList<ItemStack> items) {
        for (ItemStack item : items) {
            InventoryHelper.dropItemStack(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (!GraveUtils.canBreakGrave(world, player, pos)) {
            return false;
        }
        TileEntity te = world.getBlockEntity(pos);
        if (!world.isClientSide && te instanceof GraveStoneTileEntity) {
            GraveStoneTileEntity grave = (GraveStoneTileEntity) te;

            removeObituary(player, grave);
            spawnGhost(world, pos, grave);

            if (!grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && Main.SERVER_CONFIG.breakPickup.get()) {
                sortItems(world, pos, player, grave);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    protected void spawnGhost(World world, BlockPos pos, GraveStoneTileEntity grave) {
        if (!Main.SERVER_CONFIG.spawnGhost.get()) {
            return;
        }
        if (!world.isEmptyBlock(pos.above())) {
            return;
        }

        UUID uuid = grave.getDeath().getPlayerUUID();

        if (uuid.equals(GraveUtils.EMPTY_UUID)) {
            return;
        }

        GhostPlayerEntity ghost = new GhostPlayerEntity(world, uuid, new StringTextComponent(grave.getDeath().getPlayerName()), grave.getDeath().getEquipment(), grave.getDeath().getModel());
        ghost.setPos(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D);
        world.addFreshEntity(ghost);
    }

    protected void removeObituary(PlayerEntity p, GraveStoneTileEntity grave) {
        if (!Main.SERVER_CONFIG.removeObituary.get()) {
            return;
        }
        if (!(p instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        PlayerInventory inv = player.inventory;

        List<NonNullList<ItemStack>> invs = ImmutableList.of(player.inventory.items, player.inventory.armor, player.inventory.offhand);

        for (NonNullList<ItemStack> i : invs) {
            for (ItemStack stack : i) {
                if (stack.getItem().equals(Main.OBITUARY)) {
                    Death death = Main.OBITUARY.fromStack(player, stack);
                    if (death != null && !grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && grave.getDeath().getId().equals(death.getId())) {
                        inv.removeItem(stack);
                    }
                }
            }
        }
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);
        if (!(entity instanceof ServerPlayerEntity) || !entity.isAlive() || !Main.SERVER_CONFIG.sneakPickup.get()) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!player.isShiftKeyDown() || player.abilities.instabuild || !GraveUtils.canBreakGrave(world, player, pos)) {
            return;
        }
        TileEntity te = world.getBlockEntity(pos);
        if (!(te instanceof GraveStoneTileEntity)) {
            return;
        }
        GraveStoneTileEntity grave = (GraveStoneTileEntity) te;
        if (grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID)) {
            return;
        }

        removeObituary(player, grave);
        spawnGhost(world, pos, grave);

        sortItems(world, pos, player, grave);
        world.destroyBlock(pos, true);
    }

    protected void sortItems(World world, BlockPos pos, PlayerEntity player, GraveStoneTileEntity grave) {
        Death death = grave.getDeath();
        dropItems(world, pos, fillPlayerInventory(player, death));
        world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1F, 1F);
        grave.setChanged();
    }

    public NonNullList<ItemStack> fillPlayerInventory(PlayerEntity player, Death death) {
        NonNullList<ItemStack> additionalItems = NonNullList.create();
        fillInventory(additionalItems, death.getMainInventory(), player.inventory.items);
        fillInventory(additionalItems, death.getArmorInventory(), player.inventory.armor);
        fillInventory(additionalItems, death.getOffHandInventory(), player.inventory.offhand);

        additionalItems.addAll(death.getAdditionalItems());
        NonNullList<ItemStack> restItems = NonNullList.create();
        for (ItemStack stack : additionalItems) {
            if (!player.inventory.add(stack)) {
                restItems.add(stack);
            }
        }

        death.getAdditionalItems().clear();
        return restItems;
    }

    public void fillInventory(List<ItemStack> additionalItems, NonNullList<ItemStack> inventory, NonNullList<ItemStack> playerInv) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack playerStack = playerInv.get(i);
            if (!playerStack.isEmpty()) {
                additionalItems.add(playerStack);
            }
            inventory.set(i, ItemStack.EMPTY);
            playerInv.set(i, stack);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new GraveStoneTileEntity();
    }

}
