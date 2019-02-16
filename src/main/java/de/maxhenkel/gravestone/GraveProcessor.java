package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import de.maxhenkel.gravestone.blocks.BlockGraveStone;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.util.NoSpaceException;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GraveProcessor {

    private EntityLivingBase entity;
    private World world;
    private BlockPos deathPosition;
    private BlockPos gravePosition;
    private List<ItemStack> drops;
    private long time;

    public GraveProcessor(EntityLivingBase entity) {
        this.entity = entity;
        this.world = entity.getEntityWorld();
        this.deathPosition = entity.getPosition();
        this.gravePosition = deathPosition;
        this.drops = new ArrayList<ItemStack>();
        this.time = System.currentTimeMillis();
    }

    public boolean placeGraveStone(Collection<EntityItem> drops) {
        for (EntityItem ei : drops) {
            this.drops.add(ei.getItem());
        }

        try {
            this.gravePosition = getGraveStoneLocation();
        } catch (NoSpaceException e) {
            this.gravePosition = deathPosition;
            Log.i("Grave of '" + entity.getName().getUnformattedComponentText() + "' cant be created (No space)");
            return false;
        }

        try {
            world.setBlockState(gravePosition, Main.graveStone.getDefaultState().with(BlockGraveStone.FACING,
                    entity.getHorizontalFacing().getOpposite()));

            if (isReplaceable(gravePosition.down())) {
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

            graveTileEntity.setPlayerName(entity.getName().getFormattedText());
            graveTileEntity.setPlayerUUID(entity.getUniqueID().toString());
            graveTileEntity.setDeathTime(time);

            graveTileEntity.setRenderHead(entity instanceof EntityPlayer);

            addItems(graveTileEntity, drops);

        } catch (Exception e) {
            Log.w("Failed to fill gravestone with data");
        }

        return true;
    }

    private void addItems(TileEntityGraveStone graveStone, Collection<EntityItem> items) {
        try {
            int i = 0;
            for (EntityItem item : items) {
                try {
                    ItemStack stack = item.getItem();
                    if (graveStone.getSizeInventory() > i) {
                        graveStone.setInventorySlotContents(i, stack);
                    } else {
                        InventoryHelper.spawnItemStack(world, graveStone.getPos().getX(), graveStone.getPos().getY(), graveStone.getPos().getZ(), stack);
                    }

                } catch (Exception e) {
                    Log.w("Failed to add Item '" + item.getItem().getItem().getTranslationKey() + "' to gravestone");
                }
                i++;
            }
        } catch (Exception e) {
            Log.w("Failed to add Ites to gravestone");
        }
    }

    public BlockPos getGraveStoneLocation() throws NoSpaceException {
        BlockPos location = new BlockPos(deathPosition.getX(), deathPosition.getY(), deathPosition.getZ());

        if (world.isOutsideBuildHeight(location) && location.getY() < world.getHeight()) {
            location = new BlockPos(location.getX(), 1, location.getZ());
        }

        while (location.getY() < world.getHeight()) {
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

        for (Block replaceableBlock : Config.replaceableBlocks) {
            if (b.getRegistryName().toString().equals(replaceableBlock.getRegistryName().toString())) {
                return true;
            }
        }
        return false;
    }

    public void givePlayerNote() {

        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;

        DeathInfo info = new DeathInfo(gravePosition, player.dimension.toString(), drops.stream().collect(Collectors.toList()), player.getName().getUnformattedComponentText(), time, player.getUniqueID());
        ItemStack stack = new ItemStack(Main.deathInfo);

        info.addToItemStack(stack);
        player.inventory.addItemStackToInventory(stack);
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

}
