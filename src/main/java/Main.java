import controller.GameController;
import view.GameView;

import javax.swing.*;
import java.io.IOException;

// Main.java
public class Main {
    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView();
            GameController controller = new GameController(view);
            view.setVisible(true);
        });

    }
}