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
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

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

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Main.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DeathInfo>> DEATH_DATA_COMPONENT = DATA_COMPONENT_TYPE_REGISTER.register("death", () -> DataComponentType.<DeathInfo>builder().persistent(DeathInfo.CODEC).networkSynchronized(DeathInfo.STREAM_CODEC).build());

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public Main(IEventBus eventBus) {
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::registerAttributes);
        eventBus.addListener(this::onRegisterPayloadHandler);
        eventBus.addListener(CreativeTabEvents::onCreativeModeTabBuildContents);

        SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.CLIENT, ClientConfig.class, true);

        if (FMLEnvironment.dist.isClient()) {
            eventBus.addListener(Main.this::clientSetup);
        }

        BLOCK_REGISTER.register(eventBus);
        ITEM_REGISTER.register(eventBus);
        BLOCK_ENTITY_REGISTER.register(eventBus);
        ENTITY_REGISTER.register(eventBus);
        DATA_COMPONENT_TYPE_REGISTER.register(eventBus);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new DeathEvents());
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

    public void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("0");
        CommonRegistry.registerMessage(registrar, MessageOpenObituary.class);
    }

    public void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(Main.GHOST.get(), GhostPlayerEntity.getGhostAttributes());
    }

}
