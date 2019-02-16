package de.maxhenkel.gravestone;

import java.util.*;
import java.util.stream.Collectors;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPairServer.getRight();
        SERVER = specPairServer.getLeft();

        Pair<ClientConfig, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPairClient.getRight();
        CLIENT = specPairClient.getLeft();
    }

    public static Map<String, String> dimensionNames = new HashMap<>();
    public static String dateFormat = "yyyy/MM/dd HH:mm:ss";
    public static boolean renderSkull = true;
    public static boolean livingGraves = false;
    public static boolean giveDeathNotes = true;
    public static List<Block> replaceableBlocks = new ArrayList<>();
    public static boolean removeDeathNote = false;
    public static boolean onlyOwnersCanBreak = false;
    public static boolean spawnGhost = false;
    public static boolean friendlyGhost = true;

    public static void loadServer() {
        livingGraves = SERVER.livingGraves.get();
        giveDeathNotes = SERVER.giveDeathNotes.get();
        replaceableBlocks = SERVER.replaceableBlocks.get().stream().map(s -> Tools.getBlock(s)).collect(Collectors.toList());
        removeDeathNote = SERVER.removeDeathNote.get();
        onlyOwnersCanBreak = SERVER.onlyOwnersCanBreak.get();
        spawnGhost = SERVER.spawnGhost.get();
        friendlyGhost = SERVER.friendlyGhost.get();
    }

    public static void loadClient() {
        dimensionNames = CLIENT.dimensionNames.get().stream().collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        dateFormat = CLIENT.dateFormat.get();
        renderSkull = CLIENT.renderSkull.get();
    }

    public static class ServerConfig {
        public ForgeConfigSpec.BooleanValue livingGraves;
        public ForgeConfigSpec.BooleanValue giveDeathNotes;
        public ForgeConfigSpec.ConfigValue<List<String>> replaceableBlocks;
        public ForgeConfigSpec.BooleanValue removeDeathNote;
        public ForgeConfigSpec.BooleanValue onlyOwnersCanBreak;
        public ForgeConfigSpec.BooleanValue spawnGhost;
        public ForgeConfigSpec.BooleanValue friendlyGhost;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            livingGraves = builder
                    .comment("If this is set to true every living entity will generate a gravestone")
                    .translation("enable_living_entity_graves")
                    .define("enable_living_entity_graves", false);
            giveDeathNotes = builder
                    .comment("If this is set to true you get a death note after you died")
                    .translation("enable_death_note")
                    .define("enable_death_note", true);
            replaceableBlocks = builder
                    .comment("The blocks that can be replaced with a grave when someone dies there")
                    .translation("replaceable_blocks")
                    .define("replaceable_blocks", Arrays.asList(
                            Blocks.TALL_GRASS, Blocks.GRASS, Blocks.WATER, Blocks.LAVA, Blocks.DANDELION,
                            Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY, Blocks.SUNFLOWER,
                            Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.AZURE_BLUET, Blocks.OXEYE_DAISY,
                            Blocks.ORANGE_TULIP, Blocks.PINK_TULIP, Blocks.RED_TULIP, Blocks.WHITE_TULIP,
                            Blocks.ALLIUM, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SPRUCE_SAPLING,
                            Blocks.ACACIA_SAPLING, Blocks.BIRCH_SAPLING, Blocks.DARK_OAK_SAPLING,
                            Blocks.JUNGLE_SAPLING, Blocks.OAK_SAPLING, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM,
                            Blocks.SNOW, Blocks.VINE, Blocks.DEAD_BUSH, Blocks.FIRE
                    ).stream().map(b -> b.getRegistryName().toString()).collect(Collectors.toList()));//TODO add all
            removeDeathNote = builder
                    .comment("If this is set to true the death note will be taken out of your inventory when you destroyed the gravestone")
                    .translation("remove_death_note")
                    .define("remove_death_note", false);
            onlyOwnersCanBreak = builder
                    .comment("If this is set to true only the player that owns the gravestone and the admins can break the gravestone")
                    .translation("only_owners_can_break")
                    .define("only_owners_can_break", false);
            spawnGhost = builder
                    .comment("If this is set to true a ghost of the dead player will be spawned when the gravestone is broken")
                    .translation("spawn_ghost")
                    .define("spawn_ghost", false);
            friendlyGhost = builder
                    .comment("If this is set to true the ghost of the dead player will defend him")
                    .translation("friendly_ghost")
                    .define("friendly_ghost", true);
        }
    }

    public static class ClientConfig {
        public ForgeConfigSpec.ConfigValue<List<String>> dimensionNames;
        public ForgeConfigSpec.ConfigValue<String> dateFormat;
        public ForgeConfigSpec.BooleanValue renderSkull;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            dimensionNames = builder
                    .comment("The names of the Dimensions for the Death Note")
                    .translation("dimension_names")
                    .define("dimension_names", Arrays.asList("minecraft:overworld=Overworld", "minecraft:nether=Nether", "minecraft:the_end=The End"));
            dateFormat = builder
                    .comment("The date format outputted by clicking the gravestone or displayed in the death note")
                    .translation("enable_death_note")
                    .define("enable_death_note", "yyyy/MM/dd HH:mm:ss");
            renderSkull = builder
                    .comment("If this is set to true the players head will be rendered on the gravestone when there is a full block under it")
                    .translation("render_skull")
                    .define("render_skull", true);
        }
    }

}
