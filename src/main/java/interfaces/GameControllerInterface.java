package interfaces;

import model.NPCModel;

public interface GameControllerInterface {
    void handleUserInput(String input);
    String interactWithNPC(NPCModel npc, String input);
}