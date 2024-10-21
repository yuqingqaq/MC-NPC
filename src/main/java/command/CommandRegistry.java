package command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import entity.LibrarianNPCEntity;
import entity.NPCDataManager;
import entity.ProfessorNPCEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import npcopenai.EntityRegistry;
import npcopenai.NPCOpenAI;

import java.util.UUID;

import static npcopenai.NPCOpenAI.getLogger;

@Mod.EventBusSubscriber
public class CommandRegistry {

    public enum NPCType {
        LIBRARIAN,
        PROFESSOR
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("spawnlibrarian")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> spawnNPC(context.getSource(), NPCType.LIBRARIAN)));

        dispatcher.register(Commands.literal("spawnprofessor")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    getLogger().debug("Attempting to spawn a Professor NPC at {} by {}", source.getPosition(), source.getTextName());
                    return spawnNPC(source, NPCType.PROFESSOR);
                }));

        dispatcher.register(Commands.literal("findlibrarian")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> findNPC(context.getSource(), NPCType.LIBRARIAN)));

        dispatcher.register(Commands.literal("findprofessor")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> findNPC(context.getSource(), NPCType.PROFESSOR)));

        dispatcher.register(Commands.literal("clearnpcdata")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> clearNPCData(context.getSource())));
    }

    private static int spawnNPC(CommandSourceStack source, NPCType type) {
        ServerLevel world = source.getLevel();
        BlockPos pos = new BlockPos(source.getPosition());
        Entity npc = null;

        switch (type) {
            case LIBRARIAN:
                if (NPCDataManager.uniqueLibrarianUuid == null) {  // Check if an NPC already exists
                    npc = new LibrarianNPCEntity(EntityRegistry.LIBRAIAN_ENTITY.get(), world);
                    npc.setUUID(UUID.randomUUID());
                    npc.setPos(pos.getX(), pos.getY(), pos.getZ());
                    world.addFreshEntity(npc);
                    NPCDataManager.saveNPC(npc.getUUID(), world);
                    source.sendSuccess(new TextComponent("Librarian NPC spawned successfully!"), true);
                } else {
                    source.sendFailure(new TextComponent("A librarian NPC already exists."));
                }
                break;
            case PROFESSOR:
                getLogger().debug("Checking for existing Professor NPC...");
                if (NPCDataManager.uniqueProfessorUUID == null) {
                    getLogger().debug("Professor NPC spawning");
                    npc = new ProfessorNPCEntity(EntityRegistry.PROFESSOR_ENTITY.get(), world);
                    npc.setUUID(UUID.randomUUID());
                    npc.setPos(pos.getX(), pos.getY(), pos.getZ());
                    world.addFreshEntity(npc);
                    NPCDataManager.saveProfessor(npc.getUUID(), world);
                    getLogger().info("Professor NPC spawned successfully at {}", pos);
                    source.sendSuccess(new TextComponent("Professor NPC spawned successfully!"), true);
                } else {
                    getLogger().warn("Attempted to spawn a Professor NPC, but one already exists.");
                    source.sendFailure(new TextComponent("A professor NPC already exists."));
                }
                break;
        }

        return 1;
    }

    private static int findNPC(CommandSourceStack source, NPCType type) throws CommandSyntaxException {
        UUID uuid = (type == NPCType.LIBRARIAN) ? NPCDataManager.uniqueLibrarianUuid : NPCDataManager.uniqueProfessorUUID;
        Entity npc = NPCDataManager.findNPCByUUID(source.getLevel(), uuid);

        if (npc != null) {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos npcPos = npc.blockPosition();
            player.teleportTo(source.getLevel(), npcPos.getX(), npcPos.getY(), npcPos.getZ(), player.getYRot(), player.getXRot());
            source.sendSuccess(new TextComponent(String.format("%s NPC is located at [%d, %d, %d].",
                    type == NPCType.LIBRARIAN ? "LIBRARIAN" : "Professor", npcPos.getX(), npcPos.getY(), npcPos.getZ())), true);
        } else {
            source.sendFailure(new TextComponent(String.format("%s NPC not found.",
                    type == NPCType.LIBRARIAN ? "LIBRARIAN" : "Professor")));
        }
        return 1;
    }

    private static int clearNPCData(CommandSourceStack source) {
        ServerLevel world = source.getLevel();

        // 移除所有 NPCEntity 类型的实体
        Iterable<Entity> entities = world.getAllEntities();
        for (Entity entity : entities) {
            if (entity instanceof LibrarianNPCEntity || entity instanceof ProfessorNPCEntity) {
                entity.remove(Entity.RemovalReason.DISCARDED); // 或者使用 entity.discard() 根据你的API版本
            }
        }

        // 清空NPC数据
        NPCDataManager.deleteNPCData(world);

        // 发送成功消息
        source.sendSuccess(new TextComponent("All NPC data cleared successfully and all NPCs have been removed."), true);
        return Command.SINGLE_SUCCESS;
    }

}