package command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import registry.ItemRegistry;

@Mod.EventBusSubscriber(modid = "npcopenai")
public class ClearGoldCoinsCommandRegistry {

    /**
     * 注册所有命令
     */
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // 注册 /cleargoldcoins 命令
        dispatcher.register(Commands.literal("cleargoldcoins")
                .requires(source -> source.hasPermission(2)) // 需要管理员权限
                .executes(context -> clearGoldCoins(context.getSource()))
        );
    }

    /**
     * 清除当前世界中的所有金币实体
     *
     * @param source 命令来源
     * @return 被清除的金币数量
     */
    private static int clearGoldCoins(CommandSourceStack source) {
        ServerLevel level = source.getLevel(); // 获取当前世界
        int removedCount = 0; // 统计清除的金币数量

        // 遍历当前世界中的所有实体
        for (Entity entity : level.getEntities().getAll()) {
            // 判断实体是否是金币实体
            if (entity instanceof ItemEntity itemEntity &&
                    itemEntity.getItem().getItem() == ItemRegistry.GOLD_COIN.get()) {
                entity.remove(Entity.RemovalReason.DISCARDED); // 移除实体
                removedCount++;
            }
        }

        // 向命令执行者反馈结果
        System.out.println("Removed " + removedCount + " gold coins from the world.");
        return removedCount; // 返回移除的实体数量
    }
}