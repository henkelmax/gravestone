package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.config.ConfigBase;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig extends ConfigBase {

    public final ModConfigSpec.BooleanValue renderSkull;
    private final ModConfigSpec.ConfigValue<String> graveTextColorSpec;

    public int graveTextColor = 0xFFFFFF;

    public ClientConfig(ModConfigSpec.Builder builder) {
        super(builder);
        renderSkull = builder
                .comment("If this is set to true the players head will be rendered on the gravestone when there is a full block under it")
                .translation("render_skull")
                .define("render_skull", true);
        graveTextColorSpec = builder
                .comment("The color of the text at the gravestone (Hex RGB)")
                .translation("grave_text_color")
                .define("grave_text_color", "FFFFFF");
    }

    @Override
    public void onReload(ModConfigEvent event) {
        super.onReload(event);
        graveTextColor = Integer.parseInt(graveTextColorSpec.get(), 16);
    }
}
