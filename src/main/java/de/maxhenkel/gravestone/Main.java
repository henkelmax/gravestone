package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.commands.RestoreCommand;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.entity.PlayerGhostRenderer;
import de.maxhenkel.gravestone.events.CreativeTabEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import de.maxhenkel.gravestone.tileentity.render.GravestoneRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

    public static SimpleChannel SIMPLE_CHANNEL;

    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, Main.MODID);
    public static final DeferredHolder<Block, GraveStoneBlock> GRAVESTONE = BLOCK_REGISTER.register("gravestone", GraveStoneBlock::new);

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Main.MODID);
    public static final DeferredHolder<Item, Item> GRAVESTONE_ITEM = ITEM_REGISTER.register("gravestone", () -> GRAVESTONE.get().toItem());
    public static final DeferredHolder<Item, ObituaryItem> OBITUARY = ITEM_REGISTER.register("obituary", ObituaryItem::new);

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Main.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraveStoneTileEntity>> GRAVESTONE_TILEENTITY = BLOCK_ENTITY_REGISTER.register("gravestone", () -> BlockEntityType.Builder.of(GraveStoneTileEntity::new, GRAVESTONE.get()).build(null));

    private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Main.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<GhostPlayerEntity>> GHOST = ENTITY_REGISTER.register("player_ghost", () ->
            CommonRegistry.registerEntity(Main.MODID, "player_ghost", MobCategory.MONSTER, GhostPlayerEntity.class, builder -> builder.sized(0.6F, 1.95F))
    );

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeTabEvents::onCreativeModeTabBuildContents);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class, true);

        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
        }

        BLOCK_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITY_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITY_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new DeathEvents());

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageOpenObituary.class);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(GRAVESTONE_TILEENTITY.get(), GravestoneRenderer::new);

        // TODO
        // RenderingRegistry.registerEntityRenderingHandler(GHOST, PlayerGhostRenderer::new);
        EntityRenderers.register(GHOST.get(), PlayerGhostRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        RestoreCommand.register(event.getDispatcher());
    }

    public void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(Main.GHOST.get(), GhostPlayerEntity.getGhostAttributes());
    }

}
