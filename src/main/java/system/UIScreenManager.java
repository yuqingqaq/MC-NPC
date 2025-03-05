package system;

import net.minecraft.client.Minecraft;
import gui.academic.TaskOverviewScreen;

public class UIScreenManager {
    private static UIScreenManager instance;
    private boolean isHUDVisible = true;
    private ScreenState currentScreenState = ScreenState.DEFAULT;

    public enum ScreenState {
        DEFAULT,  // HUD 可见
        NO_HUD    // HUD 不可见
    }

    private UIScreenManager() {}

    public static UIScreenManager getInstance() {
        if (instance == null) {
            instance = new UIScreenManager();
            instance.updateHUDVisibility();
        }
        return instance;
    }

    public boolean isHUDVisible() {
        return isHUDVisible;
    }

    private void updateHUDVisibility() {
        isHUDVisible = (currentScreenState == ScreenState.DEFAULT);
    }

    public void setCurrentScreenState(ScreenState screenState) {
        this.currentScreenState = screenState;
        updateHUDVisibility();
    }

    public void switchToTaskOverviewScreen() {
        Minecraft.getInstance().setScreen(new TaskOverviewScreen());
    }

} 