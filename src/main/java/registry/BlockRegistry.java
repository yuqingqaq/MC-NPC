package registry;

import block.poster.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import npcopenai.NPCOpenAI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockRegistry {
    private static final Logger LOGGER = LogManager.getLogger();

    // 方块注册器
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NPCOpenAI.MODID);

    // 方块实体注册器
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, NPCOpenAI.MODID);

    // 基础概念海报方块
    public static final RegistryObject<Block> AGENT_BASIC_POSTER =
            BLOCKS.register("agent_basic_poster", AgentBasicPosterBlock::new);

    // 基础概念海报方块实体
    public static final RegistryObject<BlockEntityType<AgentBasicPosterBlockEntity>> AGENT_BASIC_POSTER_ENTITY =
            BLOCK_ENTITIES.register("agent_basic_poster",
                    () -> BlockEntityType.Builder.of(
                            AgentBasicPosterBlockEntity::new,
                            AGENT_BASIC_POSTER.get()
                    ).build(null));

    // 技术演化海报方块
    public static final RegistryObject<Block> AGENT_EVOLUTION_POSTER =
            BLOCKS.register("agent_evolution_poster", AgentEvolutionPosterBlock::new);

    // 技术演化海报方块实体
    public static final RegistryObject<BlockEntityType<AgentEvolutionPosterBlockEntity>> AGENT_EVOLUTION_POSTER_ENTITY =
            BLOCK_ENTITIES.register("agent_evolution_poster",
                    () -> BlockEntityType.Builder.of(
                            AgentEvolutionPosterBlockEntity::new,
                            AGENT_EVOLUTION_POSTER.get()
                    ).build(null));

    // 学习方法海报方块
    public static final RegistryObject<Block> AGENT_LEARNING_POSTER =
            BLOCKS.register("agent_learning_poster", AgentLearningPosterBlock::new);

    // 学习方法海报方块实体
    public static final RegistryObject<BlockEntityType<AgentLearningPosterBlockEntity>> AGENT_LEARNING_POSTER_ENTITY =
            BLOCK_ENTITIES.register("agent_learning_poster",
                    () -> BlockEntityType.Builder.of(
                            AgentLearningPosterBlockEntity::new,
                            AGENT_LEARNING_POSTER.get()
                    ).build(null));

    // 设计原则海报方块
    public static final RegistryObject<Block> AGENT_PRINCIPLES_POSTER =
            BLOCKS.register("agent_principles_poster", AgentPrinciplesPosterBlock::new);

    // 设计原则海报方块实体
    public static final RegistryObject<BlockEntityType<AgentPrinciplesPosterBlockEntity>> AGENT_PRINCIPLES_POSTER_ENTITY =
            BLOCK_ENTITIES.register("agent_principles_poster",
                    () -> BlockEntityType.Builder.of(
                            AgentPrinciplesPosterBlockEntity::new,
                            AGENT_PRINCIPLES_POSTER.get()
                    ).build(null));

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            LOGGER.info("Registering blocks");
            // 不需要手动注册，DeferredRegister会处理
            LOGGER.info("Blocks registered.");
        }

        @SubscribeEvent
        public static void onBlockEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> blockEntityRegistryEvent) {
            LOGGER.info("Registering block entities");
            // 不需要手动注册，DeferredRegister会处理
            LOGGER.info("Block entities registered.");
        }
    }
}