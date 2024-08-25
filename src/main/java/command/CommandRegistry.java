package command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import entity.NPCDataManager;
import entity.NPCEntity;
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
import npcopenai.NPCOpenAI;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CommandRegistry {

    public enum NPCType {
        NORMAL,
        PROFESSOR
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("spawnnpc")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> spawnNPC(context.getSource(), NPCType.NORMAL)));

        dispatcher.register(Commands.literal("spawnprofessor")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> spawnNPC(context.getSource(), NPCType.PROFESSOR)));

        dispatcher.register(Commands.literal("findnpc")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> findNPC(context.getSource(), NPCType.NORMAL)));

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
            case NORMAL:
                if (NPCDataManager.uniqueNpcUUID == null) {  // Check if an NPC already exists
                    npc = new NPCEntity(NPCOpenAI.NPC_ENTITY.get(), world);
                    npc.setUUID(UUID.randomUUID());
                    npc.setPos(pos.getX(), pos.getY(), pos.getZ());
                    world.addFreshEntity(npc);
                    NPCDataManager.saveNPC(npc.getUUID(), world);
                    source.sendSuccess(new TextComponent("Normal NPC spawned successfully!"), true);
                } else {
                    source.sendFailure(new TextComponent("A normal NPC already exists."));
                }
                break;
            case PROFESSOR:
                if (NPCDataManager.uniqueProfessorUUID == null) {  // Check if a Professor NPC already exists
                    npc = new ProfessorNPCEntity(NPCOpenAI.PROFESSOR_ENTITY.get(), world);
                    npc.setUUID(UUID.randomUUID());
                    npc.setPos(pos.getX(), pos.getY(), pos.getZ());
                    world.addFreshEntity(npc);
                    NPCDataManager.saveProfessor(npc.getUUID(), world);
                    source.sendSuccess(new TextComponent("Professor NPC spawned successfully!"), true);
                } else {
                    source.sendFailure(new TextComponent("A professor NPC already exists."));
                }
                break;
        }

        return 1;
    }

    private static int findNPC(CommandSourceStack source, NPCType type) throws CommandSyntaxException {
        UUID uuid = (type == NPCType.NORMAL) ? NPCDataManager.uniqueNpcUUID : NPCDataManager.uniqueProfessorUUID;
        Entity npc = NPCDataManager.findNPCByUUID(source.getLevel(), uuid);

        if (npc != null) {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos npcPos = npc.blockPosition();
            player.teleportTo(source.getLevel(), npcPos.getX(), npcPos.getY(), npcPos.getZ(), player.getYRot(), player.getXRot());
            source.sendSuccess(new TextComponent(String.format("%s NPC is located at [%d, %d, %d].",
                    type == NPCType.NORMAL ? "Normal" : "Professor", npcPos.getX(), npcPos.getY(), npcPos.getZ())), true);
        } else {
            source.sendFailure(new TextComponent(String.format("%s NPC not found.",
                    type == NPCType.NORMAL ? "Normal" : "Professor")));
        }
        return 1;
    }

    private static int clearNPCData(CommandSourceStack source) {
        ServerLevel world = source.getLevel();

        // 移除所有 NPCEntity 类型的实体
        Iterable<Entity> entities = world.getAllEntities();
        for (Entity entity : entities) {
            if (entity instanceof NPCEntity || entity instanceof ProfessorNPCEntity) {
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