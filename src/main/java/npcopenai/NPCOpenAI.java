package npcopenai;

import controller.GameController;
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
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.stream.Collectors;

@Mod(NPCOpenAI.MODID)
public class NPCOpenAI {
    public static final String MODID = "npcopenai";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("custom_item", CustomItem::new);

    public NPCOpenAI()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        System.out.println("Attempting to load Jackson classes...");
        try {
            Class.forName("com.fasterxml.jackson.core.type.TypeReference");
            System.out.println("Jackson TypeReference class loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load Jackson TypeReference class.");
            e.printStackTrace();
        }
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
        LOGGER.info("HELLO from server starting");
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
    }
}