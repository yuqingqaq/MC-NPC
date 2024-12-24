package npcopenai;

import controller.GameController;
import entity.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import registry.EntityRegistry;
import registry.ItemRegistry;

import java.util.stream.Collectors;

import static registry.EntityRegistry.LIBRARIAN_ENTITY;
import static registry.EntityRegistry.PROFESSOR_ENTITY;

@Mod(NPCOpenAI.MODID)
public class NPCOpenAI {
    public static final String MODID = "npcopenai";
    private static final Logger LOGGER = LogManager.getLogger();


    public NPCOpenAI()
    {
        // 初始化逻辑
        GameController.getInstance();
        LOGGER.info(" GameController: {}", GameController.getInstance().getNpcs());
        //ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ItemRegistry.init();
        LOGGER.info(" ItemRegistry Initialized" );

        EntityRegistry.init();
        LOGGER.info(" EntityRegistry Initialized");

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info(" MinecraftForge.EVENT_BUS Registered");

    }

    public static Logger getLogger() {
        return LOGGER;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

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
    public void onServerStarting(ServerStartingEvent event) {
        ServerLevel world = event.getServer().overworld();  // 获取主世界
        NPCData data = NPCData.forLevel(world);
        NPCDataManager.loadAllNPCs(world, data);  // 检查 NPC 是否存在
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ServerLevel world = event.getServer().overworld();  // 获取主世界
        NPCDataManager.saveAllNPCs(world);  // 保存所有 NPC 数据
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(LIBRARIAN_ENTITY.get(), LibrarianNPCEntity.createAttributes().build());
            event.put(PROFESSOR_ENTITY.get(), ProfessorNPCEntity.createAttributes().build());

        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ClientProxy {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                EntityRenderers.register(LIBRARIAN_ENTITY.get(), LibrarianNPCEntityRenderer::new);
                EntityRenderers.register(PROFESSOR_ENTITY.get(), ProfessorNPCEntityRenderer::new); // 添加此行

            });
        }
    }
}