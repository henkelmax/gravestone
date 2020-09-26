package de.maxhenkel.gravestone.util;

import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

public class Tools {

    public static String translateDimension(String dim) {
        return Main.CLIENT_CONFIG.dimensionNames.getOrDefault(dim, dim);
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

    @OnlyIn(Dist.CLIENT)
    public static String timeToString(long time) {
        if (time == 0L) {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return Main.CLIENT_CONFIG.dateFormat.format(c.getTime());
    }

}
