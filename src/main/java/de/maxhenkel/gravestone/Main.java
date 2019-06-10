package de.maxhenkel.gravestone;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import de.maxhenkel.gravestone.blocks.BlockGraveStone;
import de.maxhenkel.gravestone.entity.EntityGhostPlayer;
import de.maxhenkel.gravestone.entity.RenderFactoryGhostPlayer;
import de.maxhenkel.gravestone.events.BlockEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.gui.ContainerDeathItems;
import de.maxhenkel.gravestone.gui.GuiDeathItems;
import de.maxhenkel.gravestone.items.ItemDeathInfo;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.tileentity.TileEntitySpecialRendererGraveStone;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    private static Main instance;

    @ObjectHolder(MODID + ":gravestone")
    public static BlockGraveStone graveStone;

    @ObjectHolder(MODID + ":gravestone")
    public static Item graveStoneItem;

    @ObjectHolder(MODID + ":gravestone")
    public static TileEntityType<TileEntityGraveStone> graveTileEntity;

    @ObjectHolder(MODID + ":death_info")
    public static ItemDeathInfo deathInfo;

    @ObjectHolder(MODID + ":player_ghost")
    public static EntityType<EntityGhostPlayer> ghost;

    public static ContainerType DEATH_INFO_INVENTORY_CONTAINER = registerContainer("death_items", ContainerDeathItems::new);

    public static ResourceLocation ghostLootTable;

    public Main() {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::configEvent);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
        });
    }

    @SubscribeEvent
    public void configEvent(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            Config.loadServer();
        } else if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            Config.loadClient();
        }
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new DeathEvents());
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(this);

        Set<ResourceLocation> lootTables = ObfuscationReflectionHelper.getPrivateValue(LootTables.class, null, "LOOT_TABLES");
        lootTables.add(new ResourceLocation(Main.MODID, "entities/player_ghost"));
        //ghostLootTable = LootTables.register(new ResourceLocation(Main.MODID, "entities/player_ghost"));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGraveStone.class, new TileEntitySpecialRendererGraveStone());
        RenderingRegistry.registerEntityRenderingHandler(EntityGhostPlayer.class, new RenderFactoryGhostPlayer());

        ScreenManager.IScreenFactory factory = (ScreenManager.IScreenFactory<ContainerDeathItems, GuiDeathItems>) (container, playerInventory, name) -> new GuiDeathItems(playerInventory, container, name);
        ScreenManager.registerFactory(Main.DEATH_INFO_INVENTORY_CONTAINER, factory);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                graveStone = new BlockGraveStone()
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                graveStoneItem = graveStone.toItem(),
                deathInfo = new ItemDeathInfo()
        );

        Item.BLOCK_TO_ITEM.put(graveStone, graveStoneItem);
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        graveTileEntity = /*TileEntityType.register*/registerTileEntityType(graveStone.getRegistryName().toString(), TileEntityType.Builder.func_223042_a(TileEntityGraveStone::new));
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntityType(final String id, final TileEntityType.Builder<T> builder) {
        Type<?> type = null;

        try {
            type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        } catch (final IllegalArgumentException illegalstateexception) {
            if (SharedConstants.developmentMode) {
                throw illegalstateexception;
            }

            LogManager.getLogger(Main.MODID).warn("No data fixer registered for block entity {}", id);
        }

        if (getBlocksFromBuilder(builder).isEmpty()) {
            LogManager.getLogger(Main.MODID).warn("Block entity type {} requires at least one valid block to be defined!", id);
        }

        return Registry.register(Registry.field_212626_o, id, builder.build(type));
    }

    private static <T extends TileEntity> Set<Block> getBlocksFromBuilder(final TileEntityType.Builder<T> builder) {
        return ObfuscationReflectionHelper.getPrivateValue(TileEntityType.Builder.class, builder, "field_223044_b");
    }


    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        ghost = /*EntityType.register*/registerEntity(Main.MODID + ":player_ghost", EntityType.Builder.<EntityGhostPlayer>create(EntityGhostPlayer::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
    }

    private static <T extends Entity> EntityType<T> registerEntity(String id, EntityType.Builder<T> builder) {
        return Registry.register(Registry.field_212629_r, id, builder.build(id));
    }

    private static <T extends Container> ContainerType<T> registerContainer(String name, ContainerType.IFactory<T> factory) {
        return Registry.register(Registry.field_218366_G, new ResourceLocation(Main.MODID, name), new ContainerType<>(factory));
    }

}
