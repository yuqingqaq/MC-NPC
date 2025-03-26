package registry;

import block.poster.AcademicPosterBlock;
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
import block.poster.AcademicPosterBlockEntity;

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

    // 注册学术海报方块
    public static final RegistryObject<Block> ACADEMIC_POSTER =
        BLOCKS.register("academic_poster", AcademicPosterBlock::new);

    // 注册学术海报方块实体
    public static final RegistryObject<BlockEntityType<AcademicPosterBlockEntity>> ACADEMIC_POSTER_ENTITY =
        BLOCK_ENTITIES.register("academic_poster",
            () -> BlockEntityType.Builder.of(
                AcademicPosterBlockEntity::new,
                ACADEMIC_POSTER.get()
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
            blockRegistryEvent.getRegistry().register(ACADEMIC_POSTER.get());
            LOGGER.info("Blocks registered.");
        }

        @SubscribeEvent
        public static void onBlockEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> blockEntityRegistryEvent) {
            LOGGER.info("Registering block entities");
            blockEntityRegistryEvent.getRegistry().register(ACADEMIC_POSTER_ENTITY.get());
            LOGGER.info("Block entities registered.");
        }
    }
}