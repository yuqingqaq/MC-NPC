//import com.mojang.blaze3d.matrix.MatrixStack;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.widget.list.ExtendedList;
//
//public class CustomTaskList<E extends ExtendedList.AbstractListEntry<E>> extends ExtendedList<E> {
//    public CustomTaskList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
//        super(mc, width, height, top, bottom, itemHeight);
//        this.setRenderBackground(false);
//        this.setRenderTopAndBottom(false);
//    }
//
//    @Override
//    protected int getScrollbarPosition() {
//        return this.width - 6;
//    }
//
//    // Add a public method to add entries
//    public void addPublicEntry(E entry) {
//        this.addEntry(entry);
//    }
//}