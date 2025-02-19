package item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import controller.GameController;
import model.NPCModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import npcopenai.NPCOpenAI;

public class CustomTaskOverviewItem extends Item {

    public CustomTaskOverviewItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("item.CustomTaskItem used on: " + context.getClickedPos());

        // 判断当前是否为客户端
        if (world.isClientSide) {
            NPCOpenAI.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CustomTaskOverviewItemClientHandler::openTaskOverviewScreen);
            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        } else {
            NPCOpenAI.getLogger().info("This action is server-side, passing");
        }
        return InteractionResult.PASS;
    }
}