import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

public class CustomItem extends Item {
    public CustomItem() {
        super(new Item.Properties().tab(ItemGroup.TAB_MISC));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        NPCOpenAI.getLogger().info("CustomItem used on: " + context.getClickedPos());

        if (world.isClientSide) {
            NPCOpenAI.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                // Directly set the screen without enqueue
                // Minecraft.getInstance().setScreen(new ExampleScreen());
                NPCModel npc = GameController.getInstance().getNpcs().get(0); // Fetch the first NPC for demonstration
                NPCOpenAI.getLogger().info("CustomItem used on: " + npc.getNPCName());
                Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
                return ActionResultType.SUCCESS;
            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        } else {
            NPCOpenAI.getLogger().info("This action is server-side, passing");
        }
        return ActionResultType.PASS;
    }
}