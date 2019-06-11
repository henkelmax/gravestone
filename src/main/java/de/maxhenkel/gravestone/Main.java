package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.entity.GhostPlayerRenderFactory;
import de.maxhenkel.gravestone.events.BlockEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.gui.DeathItemsContainer;
import de.maxhenkel.gravestone.gui.DeathItemsScreen;
import de.maxhenkel.gravestone.items.DeathInfoItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import de.maxhenkel.gravestone.tileentity.GravestoneRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    @ObjectHolder(MODID + ":gravestone")
    public static GraveStoneBlock GRAVESTONE;

    @ObjectHolder(MODID + ":gravestone")
    public static Item GRAVESTONE_ITEM;

    @ObjectHolder(MODID + ":gravestone")
    public static TileEntityType<GraveStoneTileEntity> GRAVESTONE_TILEENTITY;

    @ObjectHolder(MODID + ":death_info")
    public static DeathInfoItem DEATHINFO;

    @ObjectHolder(MODID + ":player_ghost")
    public static EntityType<GhostPlayerEntity> GHOST;

    @ObjectHolder(MODID + ":death_items")
    public static ContainerType DEATH_INFO_INVENTORY_CONTAINER;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainers);
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
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(GraveStoneTileEntity.class, new GravestoneRenderer());
        RenderingRegistry.registerEntityRenderingHandler(GhostPlayerEntity.class, new GhostPlayerRenderFactory());

        ScreenManager.IScreenFactory factory = (ScreenManager.IScreenFactory<DeathItemsContainer, DeathItemsScreen>) (container, playerInventory, name) -> new DeathItemsScreen(playerInventory, container, name);
        ScreenManager.registerFactory(Main.DEATH_INFO_INVENTORY_CONTAINER, factory);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                GRAVESTONE = new GraveStoneBlock()
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                GRAVESTONE_ITEM = GRAVESTONE.toItem(),
                DEATHINFO = new DeathInfoItem()
        );
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        GRAVESTONE_TILEENTITY = TileEntityType.Builder.func_223042_a(GraveStoneTileEntity::new, GRAVESTONE).build(null);
        GRAVESTONE_TILEENTITY.setRegistryName(new ResourceLocation(MODID, "gravestone"));
        event.getRegistry().register(GRAVESTONE_TILEENTITY);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        GHOST = EntityType.Builder.<GhostPlayerEntity>create(GhostPlayerEntity::new, EntityClassification.MONSTER)
                .size(0.6F, 1.95F)
                .build(Main.MODID + ":player_ghost");
        GHOST.setRegistryName(new ResourceLocation(Main.MODID, "player_ghost"));
        event.getRegistry().register(GHOST);
    }

    @SubscribeEvent
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        DEATH_INFO_INVENTORY_CONTAINER = new ContainerType<>(DeathItemsContainer::new);
        DEATH_INFO_INVENTORY_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "death_items"));
        event.getRegistry().register(DEATH_INFO_INVENTORY_CONTAINER);
    }
}
