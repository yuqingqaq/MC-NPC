package item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import npcopenai.NPCOpenAI;
import gui.screen.NPCTimeSortedScreen;

public class TimedCustomItem extends Item {
    public TimedCustomItem() {
        super(new Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("item.TimedCustomItem used on: " + context.getClickedPos());

        if (world.isClientSide) {
            NPCOpenAI.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                //NPCModel npc = GameController.getInstance().getNpcs().get(0); // Fetch the first NPC for demonstration
                //NPCOpenAI.getLogger().info("item.TimedCustomItem used on: " + npc.getNPCName());
                //Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
                Minecraft.getInstance().setScreen(new NPCTimeSortedScreen());
                return InteractionResult.SUCCESS;
            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        } else {
            NPCOpenAI.getLogger().info("This action is server-side, passing");
        }
        return InteractionResult.PASS;
    }
}