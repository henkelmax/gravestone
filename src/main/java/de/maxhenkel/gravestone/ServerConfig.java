package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfig extends ConfigBase {

    public final ForgeConfigSpec.BooleanValue giveObituaries;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> replaceableBlocksSpec;
    public final ForgeConfigSpec.BooleanValue removeObituary;
    public final ForgeConfigSpec.BooleanValue onlyOwnersCanBreak;
    public final ForgeConfigSpec.BooleanValue spawnGhost;
    public final ForgeConfigSpec.BooleanValue friendlyGhost;
    public final ForgeConfigSpec.BooleanValue sneakPickup;
    public final ForgeConfigSpec.BooleanValue breakPickup;

    public List<Block> replaceableBlocks = new ArrayList<>();

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        giveObituaries = builder
                .comment("If this is set to true you get an obituary after you died")
                .define("enable_obituary", true);
        replaceableBlocksSpec = builder
                .comment("The blocks that can be replaced with a grave")
                .defineList("replaceable_blocks",
                        Arrays.asList(
                                "minecraft:tall_grass",
                                "minecraft:grass",
                                "minecraft:water",
                                "minecraft:lava",
                                "minecraft:dandelion",
                                "minecraft:lilac",
                                "minecraft:rose_bush",
                                "minecraft:peony",
                                "minecraft:sunflower",
                                "minecraft:poppy",
                                "minecraft:blue_orchid",
                                "minecraft:azure_bluet",
                                "minecraft:oxeye_daisy",
                                "minecraft:orange_tulip",
                                "minecraft:pink_tulip",
                                "minecraft:red_tulip",
                                "minecraft:white_tulip",
                                "minecraft:allium",
                                "minecraft:fern",
                                "minecraft:large_fern",
                                "minecraft:spruce_sapling",
                                "minecraft:acacia_sapling",
                                "minecraft:birch_sapling",
                                "minecraft:dark_oak_sapling",
                                "minecraft:jungle_sapling",
                                "minecraft:oak_sapling",
                                "minecraft:brown_mushroom",
                                "minecraft:red_mushroom",
                                "minecraft:snow",
                                "minecraft:vine",
                                "minecraft:dead_bush",
                                "minecraft:fire"),
                        Objects::nonNull
                );

        removeObituary = builder
                .comment("If this is set to true the obituary will be taken out of your inventory when you break the grave")
                .define("remove_obituary", false);
        onlyOwnersCanBreak = builder
                .comment("If this is set to true only the player that owns the grave and admins can break the grave")
                .define("only_owners_can_break", false);
        spawnGhost = builder
                .comment("If this is set to true the ghost of the dead player will be spawned when the grave is broken")
                .define("spawn_ghost", false);
        friendlyGhost = builder
                .comment("If this is set to true the ghost player will defend the player")
                .define("friendly_ghost", true);
        sneakPickup = builder
                .comment("If this is set to true you get your items back into your inventory by sneaking on the grave")
                .define("sneak_pickup", false);
        breakPickup = builder
                .comment("If this is set to true you get your items sorted back into your inventory by breaking the grave")
                .define("break_pickup", true);
    }

    @Override
    public void onReload(ModConfig.ModConfigEvent event) {
        super.onReload(event);
        replaceableBlocks = replaceableBlocksSpec.get().stream().map(s -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
