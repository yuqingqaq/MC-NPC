package registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import npcopenai.NPCOpenAI;
import item.CustomTaskOverviewItem;
import item.CustomItem;
import item.TimedCustomItem;
import item.goldcoin.GoldCoinItem;
import item.PaperItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NPCOpenAI.MODID);

    public static final RegistryObject<Item> PAPER_ITEM = ITEMS.register("paper", PaperItem::new);
    public static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("task_book", CustomItem::new);
    public static final RegistryObject<Item> CUSTOM_Overview_ITEM = ITEMS.register("task_overview_book", CustomTaskOverviewItem::new);
    public static final RegistryObject<Item> CUSTOM_ITEM_TIMED = ITEMS.register("timed_book", TimedCustomItem::new);
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("phoenix", GoldCoinItem::new);
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
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

            itemRegistryEvent.getRegistry().registerAll(
                    CUSTOM_ITEM_TIMED.get()
            );
            LOGGER.info("Timed Custom items registered.");

            itemRegistryEvent.getRegistry().registerAll(
                    CUSTOM_Overview_ITEM.get()
            );
            LOGGER.info("Paper items registered.");
            itemRegistryEvent.getRegistry().registerAll(
                    PAPER_ITEM.get()
            );
            LOGGER.info("Paper items registered.");
        }
    }



}