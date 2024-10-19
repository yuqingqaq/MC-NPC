package npcopenai;

import command.CommandRegistry;
import controller.GameController;
import entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.stream.Collectors;

@Mod(NPCOpenAI.MODID)
public class NPCOpenAI {
    public static final String MODID = "npcopenai";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("task_book", CustomItem::new);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,  MODID);

    public static final RegistryObject<EntityType<LibrarianNPCEntity>> LIBRAIAN_ENTITY = ENTITIES.register("librarian_entity",
            () -> EntityType.Builder.of(LibrarianNPCEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(MODID, "librarian_entity").toString()));

    public static final RegistryObject<EntityType<ProfessorNPCEntity>> PROFESSOR_ENTITY = ENTITIES.register("professor_entity",
            () -> EntityType.Builder.of(ProfessorNPCEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(MODID, "professor_entity").toString()));

    public NPCOpenAI()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void logDebugInfo(String message) {
        LOGGER.debug(message);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        // 初始化逻辑
        GameController.getInstance();
        LOGGER.info(" GameController: {}", GameController.getInstance().getNpcs());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("npcopenai", "helloworld", () -> {
            LOGGER.info("Hello world from NPCOpenAI");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.info("Got IMC {}", event.getIMCStream()
                .map(m -> m.messageSupplier().get())
                .collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        ServerLevel world = event.getServer().overworld();  // 获取主世界
        NPCData data = NPCData.forLevel(world);
        NPCDataManager.checkNPCExists(world, data);  // 检查 NPC 是否存在
        LOGGER.info("HELLO from server starting");
    }
    public class ServerEvents {
        @SubscribeEvent
        public void onServerStopping(ServerStoppingEvent event) {
            ServerLevel world = event.getServer().overworld();  // 获取主世界

            if (NPCDataManager.uniqueLibrarianUuid != null) {
                Entity npc = NPCDataManager.findNPCByUUID(world, NPCDataManager.uniqueLibrarianUuid);
                if (npc != null) {
                    NPCDataManager.saveNPC(npc.getUUID(), world);  // 保存 NPC 数据
                }
            }
            if (NPCDataManager.uniqueProfessorUUID != null) {
                Entity professor = NPCDataManager.findNPCByUUID(world, NPCDataManager.uniqueProfessorUUID);
                if (professor != null) {
                    NPCDataManager.saveProfessor(professor.getUUID(), world);  // 保存 NPC 数据
                }
            }
        }
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            LOGGER.info("Registering custom items");
            itemRegistryEvent.getRegistry().registerAll(
                    CUSTOM_ITEM.get()
            );
            LOGGER.info("Custom items registered.");
        }
        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(LIBRAIAN_ENTITY.get(), LibrarianNPCEntity.createAttributes().build());
            event.put(PROFESSOR_ENTITY.get(), ProfessorNPCEntity.createAttributes().build());

        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ClientProxy {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                EntityRenderers.register(LIBRAIAN_ENTITY.get(), LibrarianNPCEntityRenderer::new);
                EntityRenderers.register(PROFESSOR_ENTITY.get(), ProfessorNPCEntityRenderer::new); // 添加此行

            });
        }
    }
}