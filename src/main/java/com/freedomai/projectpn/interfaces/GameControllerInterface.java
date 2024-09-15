package com.freedomai.projectpn.interfaces;

import com.freedomai.projectpn.model.NPCModel;

public interface GameControllerInterface {
    void handleUserInput(String input);
    String interactWithNPC(NPCModel npc, String input);
}