package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GraveUtils {

    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    @Nullable
    public static BlockPos getGraveStoneLocation(World world, BlockPos pos) {
        BlockPos.Mutable location = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());

        if (World.isOutsideBuildHeight(location) && location.getY() <= 0) {
            location.set(location.getX(), 1, location.getZ());
        }

        while (!World.isOutsideBuildHeight(location)) {
            if (isReplaceable(world, location)) {
                return location;
            }

            location.move(0, 1, 0);
        }

        return null;
    }

    public static boolean isReplaceable(World world, BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();

        if (world.isEmptyBlock(pos)) {
            return true;
        }

        return Main.SERVER_CONFIG.replaceableBlocks.stream().anyMatch(b::is);
    }

    @Nullable
    public static IFormattableTextComponent getDate(long timestamp) {
        if (timestamp <= 0L) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(new TranslationTextComponent("gui.gravestone.date_format").getString());
        return new StringTextComponent(dateFormat.format(new Date(timestamp)));
    }

    public static boolean canBreakGrave(IWorld world, PlayerEntity player, BlockPos pos) {
        if (!Main.SERVER_CONFIG.onlyOwnersCanBreak.get()) {
            return true;
        }

        TileEntity te = world.getBlockEntity(pos);

        if (!(te instanceof GraveStoneTileEntity)) {
            return true;
        }

        GraveStoneTileEntity grave = (GraveStoneTileEntity) te;

        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity p = (ServerPlayerEntity) player;
            if (p.hasPermissions(p.server.getOperatorUserPermissionLevel())) {
                return true;
            }
        }
        UUID uuid = grave.getDeath().getPlayerUUID();
        if (uuid.equals(GraveUtils.EMPTY_UUID)) {
            return true;
        }

        return player.getUUID().equals(uuid);
    }

}
