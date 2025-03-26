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
import item.knowledge.*;
import item.poster.AgentBasicPosterItem;
import item.poster.AgentEvolutionPosterItem;
import item.poster.AgentLearningPosterItem;
import item.poster.AgentPrinciplesPosterItem;
import item.paper.ExcelPaperItem;
import item.paper.AgentPromptPaperItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NPCOpenAI.MODID);

    // 论文物品
    public static final RegistryObject<Item> EXCEL_PAPER = ITEMS.register("paper",
            ExcelPaperItem::new);

    public static final RegistryObject<Item> AGENT_PROMPT_PAPER = ITEMS.register("paper2",
            AgentPromptPaperItem::new);
    public static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("task_book", CustomItem::new);
    public static final RegistryObject<Item> CUSTOM_Overview_ITEM = ITEMS.register("task_overview_book", CustomTaskOverviewItem::new);
    public static final RegistryObject<Item> CUSTOM_ITEM_TIMED = ITEMS.register("timed_book", TimedCustomItem::new);
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("phoenix", GoldCoinItem::new);
    // 知识学习主物品
    public static final RegistryObject<Item> KNOWLEDGE_JOURNAL = ITEMS.register("knowledge_journal", KnowledgeJournalItem::new);

    // 知识问题物品 - 使用QuestionItem并指定对应的问题ID
    public static final RegistryObject<Item> AGENT_CONCEPT = ITEMS.register("agent_concept",
            () -> new QuestionItem("mc_agent_concept_1"));

    public static final RegistryObject<Item> AGENT_RELATIONSHIP = ITEMS.register("agent_relationship",
            () -> new QuestionItem("match_learning_methods_1"));

    public static final RegistryObject<Item> AGENT_PRINCIPLE = ITEMS.register("agent_principle",
            () -> new QuestionItem("tf_learning_principle_1"));

    public static final RegistryObject<Item> AGENT_TIMELINE = ITEMS.register("agent_timeline",
            () -> new QuestionItem("order_tech_evolution"));

    // 海报物品
    public static final RegistryObject<Item> AGENT_BASIC_POSTER = ITEMS.register("agent_basic",
            AgentBasicPosterItem::new);

    public static final RegistryObject<Item> AGENT_EVOLUTION_POSTER = ITEMS.register("agent_evolution",
            AgentEvolutionPosterItem::new);

    public static final RegistryObject<Item> AGENT_LEARNING_POSTER = ITEMS.register("agent_learning",
            AgentLearningPosterItem::new);

    public static final RegistryObject<Item> AGENT_PRINCIPLES_POSTER = ITEMS.register("agent_principles",
            AgentPrinciplesPosterItem::new);


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
            itemRegistryEvent.getRegistry().registerAll(
                    EXCEL_PAPER .get()
            );
            LOGGER.info("Paper items registered.");

            itemRegistryEvent.getRegistry().registerAll(
                    AGENT_PROMPT_PAPER.get()
            );
            LOGGER.info("Paper items registered.");

            // 注册知识学习物品
            itemRegistryEvent.getRegistry().registerAll(
                    KNOWLEDGE_JOURNAL.get(),
                    AGENT_CONCEPT.get(),
                    AGENT_RELATIONSHIP.get(),
                    AGENT_PRINCIPLE.get(),
                    AGENT_TIMELINE.get()
            );
            LOGGER.info("Knowledge learning items registered.");

            // 注册海报物品
            itemRegistryEvent.getRegistry().registerAll(
                    AGENT_BASIC_POSTER.get(),
                    AGENT_EVOLUTION_POSTER.get(),
                    AGENT_LEARNING_POSTER.get(),
                    AGENT_PRINCIPLES_POSTER.get()
            );
            LOGGER.info("Academic poster items registered.");
        }
    }
}