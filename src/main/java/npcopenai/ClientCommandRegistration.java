package npcopenai;

import controller.GameController;
import model.NPCModel;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber
public class ClientCommandRegistration {

    @SubscribeEvent
    public static void onClientCommandRegister(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("opennpcgui")
                .executes(context -> {
                    openNpcGui();
                    return 1; // 返回1表示命令执行成功
                }));
    }

    private static void openNpcGui() {
        // 从你的游戏控制器获取NPC数据，这里只是一个示例
        Minecraft.getInstance().setScreen(new NPCTaskScreen()); // 打开具有NPC信息的任务界面
    }
}