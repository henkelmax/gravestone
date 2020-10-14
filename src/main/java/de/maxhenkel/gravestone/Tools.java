package de.maxhenkel.gravestone;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

public class Tools {

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
