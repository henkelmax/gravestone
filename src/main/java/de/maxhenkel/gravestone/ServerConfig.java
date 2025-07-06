package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.config.ConfigBase;
import de.maxhenkel.corelib.tag.Tag;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfig extends ConfigBase {

    public final ModConfigSpec.BooleanValue giveObituaries;
    public final ModConfigSpec.ConfigValue<List<? extends String>> replaceableBlocksSpec;
    public final ModConfigSpec.BooleanValue removeObituary;
    public final ModConfigSpec.BooleanValue onlyOwnersCanBreak;
    public final ModConfigSpec.BooleanValue spawnGhost;
    public final ModConfigSpec.BooleanValue friendlyGhost;
    public final ModConfigSpec.BooleanValue sneakPickup;
    public final ModConfigSpec.BooleanValue breakPickup;
    public final ModConfigSpec.BooleanValue strictPlacement;

    public List<Tag<Block>> replaceableBlocks = new ArrayList<>();

    public ServerConfig(ModConfigSpec.Builder builder) {
        super(builder);
        giveObituaries = builder
                .comment("If this is set to true you get an obituary after you died")
                .define("enable_obituary", true);
        replaceableBlocksSpec = builder
                .comment("The blocks that can be replaced with a grave", "If it starts with '#' it is a tag")
                .defineList("replaceable_blocks", Collections.singletonList("#gravestone:grave_replaceable"), Objects::nonNull);
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
        strictPlacement = builder
                .comment(
                        "If this is set to true the grave will replace other blocks if there is no free space above",
                        "Note that this might cause issues with other mods or multiblock structures - This option is not recommended and subject to change"
                )
                .define("strict_placement", false);
    }

    @Override
    public void onReload(ModConfigEvent.Reloading event) {
        super.onReload(event);
        onConfigChange();
    }

    @Override
    public void onLoad(ModConfigEvent.Loading event) {
        super.onLoad(event);
        onConfigChange();
    }

    private void onConfigChange() {
        replaceableBlocks = replaceableBlocksSpec.get().stream().map(TagUtils::getBlock).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
