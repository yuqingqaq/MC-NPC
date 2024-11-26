package registry;

import entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static npcopenai.NPCOpenAI.MODID;

public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,  MODID);

    public static final RegistryObject<EntityType<LibrarianNPCEntity>> LIBRARIAN_ENTITY = ENTITIES.register("librarian_entity",
            () -> EntityType.Builder.of(LibrarianNPCEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(MODID, "librarian_entity").toString()));

    public static final RegistryObject<EntityType<ProfessorNPCEntity>> PROFESSOR_ENTITY = ENTITIES.register("professor_entity",
            () -> EntityType.Builder.of(ProfessorNPCEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(MODID, "professor_entity").toString()));
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


}

