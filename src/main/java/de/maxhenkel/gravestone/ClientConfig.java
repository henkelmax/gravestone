package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientConfig extends ConfigBase {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionNamesSpec;
    private final ForgeConfigSpec.ConfigValue<String> dateFormatSpec;
    public final ForgeConfigSpec.BooleanValue renderSkull;
    private final ForgeConfigSpec.ConfigValue<String> graveTextColorSpec;

    public Map<String, String> dimensionNames = new HashMap<>();
    public int graveTextColor = 0xFFFFFF;
    public SimpleDateFormat dateFormat;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        dimensionNamesSpec = builder
                .comment("The names of the Dimensions for the Death Note")
                .translation("dimension_names")
                .defineList("dimension_names", Arrays.asList("minecraft:overworld=Overworld", "minecraft:the_nether=Nether", "minecraft:the_end=The End"), e -> e instanceof String);
        dateFormatSpec = builder
                .comment("The date format outputted by clicking the gravestone or displayed in the death note")
                .translation("grave_date_format")
                .define("grave_date_format", "yyyy/MM/dd HH:mm:ss");
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
    public void onReload(ModConfig.ModConfigEvent event) {
        super.onReload(event);
        dimensionNames = dimensionNamesSpec.get().stream().collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        graveTextColor = Integer.parseInt(graveTextColorSpec.get(), 16);
        dateFormat = new SimpleDateFormat(dateFormatSpec.get());
    }
}
