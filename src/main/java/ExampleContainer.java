import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class ExampleContainer extends Container {
    protected ExampleContainer(int id, PlayerInventory playerInventory) {
        super(ContainerType.GENERIC_9x3, id);  // 假设使用一个 9x3 的容器类型
        // 添加 slot 到 container
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;  // 确认容器仍可用于玩家
    }

    // 如果需要，添加更多方法处理物品移动等逻辑
}
