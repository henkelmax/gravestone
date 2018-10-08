package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

public class Config {

    public static Map<Integer, String> dimensionNames = new HashMap<Integer, String>();
    public static String dateFormat = new String();
    public static boolean renderSkull = true;
    public static boolean livingGraves = false;
    public static boolean giveDeathNotes = true;
    public static List<Block> replaceableBlocks = new ArrayList<Block>();
    public static boolean removeDeathNote = false;
    public static boolean onlyPlayersCanBreak = false;
    public static boolean spawnGhost = false;
    public static boolean friendlyGhost = true;

    public static void init(Configuration config) {
        config.load();

        dimensionNames = Collections.unmodifiableMap(getDimensionNames(config));
        dateFormat = getDateFormat(config);
        renderSkull = renderSkull(config);
        livingGraves = livingGraves(config);
        giveDeathNotes = giveDeathNotes(config);
        replaceableBlocks = Collections.unmodifiableList(getReplaceableBlocks(config));
        removeDeathNote = removeDeathNote(config);
        onlyPlayersCanBreak = onlyOwnersCanBreak(config);
        spawnGhost = spawnGhost(config);
        friendlyGhost = friendlyGhost(config);

        config.save();
    }

    /*
     * private static String[] getStringArray(Configuration config, String key,
     * String category, String[] defaultValues, String comment) { String[] s =
     * defaultValues; try { s = config.getStringList(key, category,
     * defaultValues, comment); } catch (Exception e) { } return s; }
     */

    private static Map<Integer, String> getDimensionNames(Configuration config) {
        String[] def = new String[]{"-1: Nether", "0: Overworld", "1: The End"};

        String[] dimsStr = config.getStringList("dimension_names", "gravestone", def,
                "The names of the Dimensions for the Death Note");

        Map<Integer, String> dims = new HashMap<Integer, String>();

        for (String str : dimsStr) {
            try {
                int i = str.indexOf(":");

                if (i < 0) {
                    continue;
                }

                if (str.length() - 1 < i + 1) {
                    continue;
                }

                String did = str.substring(0, i);
                String name = str.substring(i + 1).trim();
                int dimid = 0;

                try {
                    dimid = Integer.parseInt(did);
                } catch (NumberFormatException e) {
                    Log.w("Failed to parse dimension ID: " + e.getMessage());
                    continue;
                }

                if (name == null || name.isEmpty()) {
                    Log.w("Failed to load dimension name for id " + dimid);
                    continue;
                }

                dims.put(dimid, name);
            } catch (Exception e) {
                Log.w("Failed to load dimension name '" + str + "': " + e.getMessage());
            }
        }

        return dims;
    }

    private static String getDateFormat(Configuration config) {
        return config.getString("grave_date_format", "gravestone", "yyyy/MM/dd HH:mm:ss",
                "The date format outputted by clicking the gravestone or displayed in the death note");
    }

    private static boolean renderSkull(Configuration config) {
        return config.getBoolean("render_skull", "gravestone", true,
                "If this is set to true the players head will be rendered on the gravestone when there is a full block under it");
    }

    private static boolean livingGraves(Configuration config) {
        return config.getBoolean("enable_living_entity_graves", "gravestone", false,
                "If this is set to true every living entity will generate a gravestone");
    }

    private static boolean giveDeathNotes(Configuration config) {
        return config.getBoolean("enable_death_note", "gravestone", true,
                "If this is set to true you get a death note after you died");
    }

    private static final String[] DEFAULT_BLOCKS = new String[]{"minecraft:tallgrass", "minecraft:water",
            "minecraft:lava", "minecraft:yellow_flower", "minecraft:red_flower", "minecraft:double_plant",
            "minecraft:sapling", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:torch",
            "minecraft:snow_layer", "minecraft:vine", "minecraft:deadbush", "minecraft:reeds", "minecraft:fire"};

    private static List<Block> getReplaceableBlocks(Configuration config) {
        List<Block> replaceableBlocks = new ArrayList<Block>();
        try {
            String[] blocks = config.getStringList("replaceable_blocks", "gravestone", DEFAULT_BLOCKS, "The blocks that can be replaced with a grave when someone dies on it");

            replaceableBlocks = Tools.getBlocks(blocks);

            if (blocks == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            Log.w("Failed to load replaceable blocks");
            try {
                replaceableBlocks = Tools.getBlocks(DEFAULT_BLOCKS);
            } catch (Exception ex) {
                replaceableBlocks = new ArrayList<Block>();
            }
        }

        return replaceableBlocks;
    }

    private static boolean removeDeathNote(Configuration config) {
        return config.getBoolean("remove_death_note", "gravestone", false,
                "If this is set to true the death note will be taken out of your inventory when you destroyed the gravestone");
    }

    private static boolean onlyOwnersCanBreak(Configuration config) {
        return config.getBoolean("only_owners_can_break", "gravestone", false,
                "If this is set to true only the player that owns the gravestone and the admins can break the gravestone");
    }

    private static boolean spawnGhost(Configuration config) {
        return config.getBoolean("spawn_ghost", "gravestone", false,
                "If this is set to true a ghost of the dead player will be spawned when the gravestone is broken");
    }

    private static boolean friendlyGhost(Configuration config) {
        return config.getBoolean("friendly_ghost", "gravestone", true,
                "If this is set to true the ghost of the dead player will defend him");
    }

}
