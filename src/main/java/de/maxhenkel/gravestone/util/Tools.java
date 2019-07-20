package de.maxhenkel.gravestone.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import de.maxhenkel.gravestone.Config;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class Tools {

    public static String translateDimension(String dim) {
        Map<String, String> dims = Config.dimensionNames;

        String name = dims.get(dim);
        if (name == null || name.isEmpty()) {
            return dim;
        }

        return name;
    }

    public static String translateItem(ItemStack stack) {
        return new TranslationTextComponent(stack.getTranslationKey()).getUnformattedComponentText();
    }

    public static boolean isArrayEmpty(Object[] obj) {
        for (Object o : obj) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean keepInventory(PlayerEntity player) {
        try {
            return player.getEntityWorld().getWorldInfo().getGameRulesInstance().getBoolean(GameRules.KEEP_INVENTORY);
        } catch (Exception e) {
            return false;
        }
    }

    public static String timeToString(long time) {
        if (time == 0L) {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormat);
        return sdf.format(c.getTime());
    }

    public static boolean isAirBlock(Block block) {
        return block.equals(Blocks.AIR) || block.equals(Blocks.CAVE_AIR) || block.equals(Blocks.VOID_AIR);
    }

    @Nullable
    public static Block getBlock(String name) {
        try {
            String[] split = name.split(":");
            if (split.length == 2) {
                Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0], split[1]));
                if (isAirBlock(b)) {
                    return null;
                } else {
                    return b;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static VoxelShape combine(VoxelShape... shapes) {
        if (shapes.length <= 0) {
            return VoxelShapes.empty();
        }
        VoxelShape combined = shapes[0];

        for (int i = 1; i < shapes.length; i++) {
            combined = VoxelShapes.or(combined, shapes[i]);
        }

        return combined;
    }

}
