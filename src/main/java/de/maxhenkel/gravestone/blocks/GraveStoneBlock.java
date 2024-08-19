package de.maxhenkel.gravestone.blocks;

import com.google.common.collect.ImmutableList;
import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GraveStoneBlock extends Block implements EntityBlock, IItemBlock, SimpleWaterloggedBlock {

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
                    Block.box(1D, 2D, 14D, 15D, 12D, 15D),
                    Block.box(2D, 12D, 14D, 14D, 14D, 15D),
                    Block.box(3D, 14D, 14D, 13D, 15D, 15D)
            ).direction(Direction.EAST,
                    BASE1,
                    BASE2,
                    Block.box(14D, 2D, 1D, 15D, 12D, 15D),
                    Block.box(14D, 12D, 2D, 15D, 14D, 14D),
                    Block.box(14D, 14D, 3D, 15D, 15D, 13D)
            ).direction(Direction.WEST,
                    BASE1,
                    BASE2,
                    Block.box(1D, 2D, 1D, 2D, 12D, 15D),
                    Block.box(1D, 12D, 2D, 2D, 14D, 14D),
                    Block.box(1D, 14D, 3D, 2D, 15D, 13D)
            ).build();

    public GraveStoneBlock() {
        super(Properties.of().mapColor(MapColor.DIRT).strength(0.3F, Float.MAX_VALUE));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof GraveStoneTileEntity) {
                GraveStoneTileEntity grave = (GraveStoneTileEntity) tileentity;
                grave.setCustomName(stack.getHoverName());
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {

    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        BlockEntity tileentity = level.getBlockEntity(pos);

        if (!(tileentity instanceof GraveStoneTileEntity)) {
            return InteractionResult.FAIL;
        }

        GraveStoneTileEntity grave = (GraveStoneTileEntity) tileentity;

        Component name = grave.getGraveName();

        if (name == null) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            Component time = GraveUtils.getDate(grave.getDeath().getTimestamp());
            if (time == null) {
                player.sendSystemMessage(name);
            } else {
                player.sendSystemMessage(Component.translatable("message.gravestone.died", name, time));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof GraveStoneTileEntity) {
                dropItems(world, pos, ((GraveStoneTileEntity) tileentity).getDeath().getAllItems());
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public void dropItems(Level world, BlockPos pos, NonNullList<ItemStack> items) {
        for (ItemStack item : items) {
            Containers.dropItemStack(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!GraveUtils.canBreakGrave(world, player, pos)) {
            return false;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (!world.isClientSide && te instanceof GraveStoneTileEntity grave) {
            removeObituary(player, grave);
            spawnGhost(world, pos, grave);

            if (!grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && Main.SERVER_CONFIG.breakPickup.get()) {
                sortItems(world, pos, player, grave);
            }
        }
        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    protected void spawnGhost(Level world, BlockPos pos, GraveStoneTileEntity grave) {
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

        GhostPlayerEntity ghost = new GhostPlayerEntity(world, uuid, Component.literal(grave.getDeath().getPlayerName()), grave.getDeath().getEquipment(), grave.getDeath().getModel());
        ghost.setPos(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D);
        world.addFreshEntity(ghost);
    }

    protected void removeObituary(Player p, GraveStoneTileEntity grave) {
        if (!Main.SERVER_CONFIG.removeObituary.get()) {
            return;
        }
        if (!(p instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) p;

        Inventory inv = player.getInventory();

        List<NonNullList<ItemStack>> invs = ImmutableList.of(player.getInventory().items, player.getInventory().armor, player.getInventory().offhand);

        for (NonNullList<ItemStack> i : invs) {
            for (ItemStack stack : i) {
                if (stack.getItem().equals(Main.OBITUARY.get())) {
                    Death death = Main.OBITUARY.get().fromStack(player, stack);
                    if (death != null && !grave.getDeath().getId().equals(GraveUtils.EMPTY_UUID) && grave.getDeath().getId().equals(death.getId())) {
                        inv.removeItem(stack);
                    }
                }
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);
        if (!(entity instanceof ServerPlayer) || !entity.isAlive() || !Main.SERVER_CONFIG.sneakPickup.get()) {
            return;
        }
        ServerPlayer player = (ServerPlayer) entity;
        if (!player.isShiftKeyDown() || player.getAbilities().instabuild || !GraveUtils.canBreakGrave(world, player, pos)) {
            return;
        }
        BlockEntity te = world.getBlockEntity(pos);
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

    protected void sortItems(Level world, BlockPos pos, Player player, GraveStoneTileEntity grave) {
        Death death = grave.getDeath();
        dropItems(world, pos, fillPlayerInventory(player, death));
        world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1F, 1F);
        grave.setChanged();
    }

    public NonNullList<ItemStack> fillPlayerInventory(Player player, Death death) {
        NonNullList<ItemStack> additionalItems = NonNullList.create();
        fillInventory(additionalItems, death.getMainInventory(), player.getInventory().items);
        fillInventory(additionalItems, death.getArmorInventory(), player.getInventory().armor);
        fillInventory(additionalItems, death.getOffHandInventory(), player.getInventory().offhand);

        additionalItems.addAll(death.getAdditionalItems());
        NonNullList<ItemStack> restItems = NonNullList.create();
        for (ItemStack stack : additionalItems) {
            if (!player.getInventory().add(stack)) {
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
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveStoneTileEntity(pos, state);
    }

}
