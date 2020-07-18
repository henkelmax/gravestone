package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import de.maxhenkel.gravestone.util.NoSpaceException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GraveProcessor {

    private LivingEntity entity;
    private World world;
    private BlockPos deathPosition;
    private BlockPos gravePosition;
    private List<ItemStack> drops;
    private long time;

    public GraveProcessor(LivingEntity entity) {
        this.entity = entity;
        this.world = entity.getEntityWorld();
        this.deathPosition = entity.func_233580_cy_();
        this.gravePosition = deathPosition;
        this.drops = new ArrayList<>();
        this.time = System.currentTimeMillis();
    }

    public boolean placeGraveStone(Collection<ItemEntity> drops) {
        this.drops = drops.stream().map(itemEntity -> itemEntity.getItem()).collect(Collectors.toList());

        try {
            this.gravePosition = getGraveStoneLocation();
        } catch (NoSpaceException e) {
            this.gravePosition = deathPosition;
            Main.LOGGER.info("Grave of '{}' cant be created (No space)", entity.getName().getString());
            return false;
        }

        try {
            world.setBlockState(gravePosition, Main.GRAVESTONE.getDefaultState().with(GraveStoneBlock.FACING,
                    entity.getHorizontalFacing().getOpposite()));

            if (isReplaceable(gravePosition.down())) {
                world.setBlockState(gravePosition.down(), Blocks.DIRT.getDefaultState());
            }
        } catch (Exception e) {
            return false;
        }

        TileEntity tileentity = world.getTileEntity(gravePosition);

        if (!(tileentity instanceof GraveStoneTileEntity)) {
            return false;
        }

        try {
            GraveStoneTileEntity graveTileEntity = (GraveStoneTileEntity) tileentity;

            graveTileEntity.setPlayerName(entity.getName().getString());
            graveTileEntity.setPlayerUUID(entity.getUniqueID().toString());
            graveTileEntity.setDeathTime(time);

            graveTileEntity.setRenderHead(entity instanceof PlayerEntity);

            addItems(graveTileEntity, drops);

        } catch (Exception e) {
            Main.LOGGER.warn("Failed to fill gravestone with data");
        }

        return true;
    }

    private void addItems(GraveStoneTileEntity graveStone, Collection<ItemEntity> items) {
        try {
            int i = 0;
            for (ItemEntity item : items) {
                try {
                    ItemStack stack = item.getItem();
                    if (graveStone.getSizeInventory() > i) {
                        graveStone.setInventorySlotContents(i, stack);
                    } else {
                        InventoryHelper.spawnItemStack(world, graveStone.getPos().getX(), graveStone.getPos().getY(), graveStone.getPos().getZ(), stack);
                    }

                } catch (Exception e) {
                    Main.LOGGER.warn("Failed to add Item '{}' to gravestone", item.getItem().getItem().getTranslationKey());
                }
                i++;
            }
        } catch (Exception e) {
            Main.LOGGER.warn("Failed to add Ites to gravestone");
        }
    }

    public BlockPos getGraveStoneLocation() throws NoSpaceException {
        BlockPos location = new BlockPos(deathPosition.getX(), deathPosition.getY(), deathPosition.getZ());

        if (World.isOutsideBuildHeight(location) && location.getY() <= 0) {
            location = new BlockPos(location.getX(), 1, location.getZ());
        }

        while (!World.isOutsideBuildHeight(location)) {
            if (isReplaceable(location)) {
                return location;
            }

            location = location.add(0, 1, 0);
        }

        throw new NoSpaceException("No free Block above death Location");
    }

    private boolean isReplaceable(BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();

        if (world.isAirBlock(pos)) {
            return true;
        }

        return Main.SERVER_CONFIG.replaceableBlocks.stream().anyMatch(replaceableBlock -> b.getRegistryName().equals(replaceableBlock.getRegistryName()));
    }

    public void givePlayerNote() {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;

        List<ItemStack> deathNoteItems = drops.stream().map(itemStack -> {
            if (itemStack.equals(Main.DEATHINFO)) {
                ItemStack stack = itemStack.copy();
                stack.setTag(null);
                return stack;
            }
            return itemStack;
        }).collect(Collectors.toList());

        DeathInfo info = new DeathInfo(gravePosition, player.world.func_234923_W_().func_240901_a_().toString(), deathNoteItems, player.getName().getString(), time, player.getUniqueID());
        ItemStack stack = new ItemStack(Main.DEATHINFO);

        info.addToItemStack(stack);
        player.inventory.addItemStackToInventory(stack);
    }

    public LivingEntity getEntity() {
        return entity;
    }

}
