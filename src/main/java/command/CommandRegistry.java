package command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import entity.NPCEntity;
import entity.ProfessorNPCEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import npcopenai.NPCOpenAI;

@Mod.EventBusSubscriber
public class CommandRegistry {

    public static NPCEntity uniqueNpc = null;
    public static ProfessorNPCEntity uniqueProfessor = null;

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // 注册 spawnnpc 命令
        dispatcher.register(Commands.literal("spawnnpc")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> spawnNPC(context.getSource())));

        // 注册 spawnprofessor 命令
        dispatcher.register(Commands.literal("spawnprofessor")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> spawnProfessorNPC(context.getSource())));

        // 注册 findnpc 命令
        dispatcher.register(Commands.literal("findnpc")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> findNPC(context.getSource())));

        // 注册 findprofessor 命令
        dispatcher.register(Commands.literal("findprofessor")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> findProfessorNPC(context.getSource())));
    }

    private static int spawnNPC(CommandSourceStack source) {
        if (uniqueNpc == null) {
            ServerLevel world = source.getLevel();
            BlockPos pos = new BlockPos(source.getPosition());
            uniqueNpc = new NPCEntity(NPCOpenAI.NPC_ENTITY.get(), world);
            uniqueNpc.setPos(pos.getX(), pos.getY(), pos.getZ());
            world.addFreshEntity(uniqueNpc);
            source.sendSuccess(new TextComponent("Unique NPC spawned successfully!"), true);
        } else {
            source.sendFailure(new TextComponent("A unique NPC already exists."));
        }
        return 1;
    }

    private static int spawnProfessorNPC(CommandSourceStack source) {
        if (uniqueProfessor == null) {
            ServerLevel world = source.getLevel();
            BlockPos pos = new BlockPos(source.getPosition());
            uniqueProfessor = new ProfessorNPCEntity(NPCOpenAI.PROFESSOR_ENTITY.get(), world);
            uniqueProfessor.setPos(pos.getX(), pos.getY(), pos.getZ());
            world.addFreshEntity(uniqueProfessor);
            source.sendSuccess(new TextComponent("Professor NPC spawned successfully!"), true);
        } else {
            source.sendFailure(new TextComponent("Professor NPC already exists."));
        }
        return 1;
    }

    private static int findNPC(CommandSourceStack source) throws CommandSyntaxException {
        if (uniqueNpc != null) {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos npcPos = uniqueNpc.blockPosition();
            player.teleportTo(source.getLevel(), npcPos.getX(), npcPos.getY(), npcPos.getZ(), player.getYRot(), player.getXRot());
            String message = String.format("NPC is located at [%d, %d, %d].", npcPos.getX(), npcPos.getY(), npcPos.getZ());
            source.sendSuccess(new TextComponent(message), true);
        } else {
            source.sendFailure(new TextComponent("No unique NPC has been spawned."));
        }
        return 1;
    }

    private static int findProfessorNPC(CommandSourceStack source) throws CommandSyntaxException {
        if (uniqueProfessor != null) {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos npcPos = uniqueProfessor.blockPosition();
            player.teleportTo(source.getLevel(), npcPos.getX(), npcPos.getY(), npcPos.getZ(), player.getYRot(), player.getXRot());
            String message = String.format("Professor Wang is located at [%d, %d, %d].", npcPos.getX(), npcPos.getY(), npcPos.getZ());
            source.sendSuccess(new TextComponent(message), true);
        } else {
            source.sendFailure(new TextComponent("No unique Professor NPC has been spawned."));
        }
        return 1;
    }
}