package com.freedomai.projectpn.controller;

import com.freedomai.projectpn.api.OpenAIGPT;
import com.freedomai.projectpn.interfaces.GameControllerInterface;
import com.freedomai.projectpn.system.NPCSystem;
import com.freedomai.projectpn.view.GameView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.freedomai.projectpn.model.NPCModel;
import com.freedomai.projectpn.model.ItemModel;
import com.freedomai.projectpn.util.JsonLoader;

import java.util.List;

public class GameController implements GameControllerInterface {
    private static GameController instance; // Singleton instance

    private GameView view;
    private List<NPCModel> npcs;
    private List<ItemModel> backpackItems;
    private List<ItemModel> worldObjects;
    private List<ItemModel> gameAssets;
    private NPCSystem npcSystem;
    private OpenAIGPT gptModel;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }
    private GameController() {
        loadGameData();
    }

//    public GameController(GameView view) {
//        this.view = view;
//        this.view.setController(this);
//        loadGameData();
//        initializeView();
//    }

    private void loadGameData() {

        npcs = JsonLoader.loadNPCsFromJson("json/World.json");
        backpackItems = JsonLoader.loadObjectListFromJson(
                "json/playerBackpack.json",
                "player_backpack",
                new TypeReference<List<ItemModel>>() {}
        );
        worldObjects = JsonLoader.loadObjectListFromJson(
                "json/world_objects.json",
                "world_objects",
                new TypeReference<List<ItemModel>>() {}
        );
        gameAssets = JsonLoader.loadObjectListFromJson(
                "json/game_assets.json",
                "game_assets",
                new TypeReference<List<ItemModel>>() {}
        );
        gptModel = new OpenAIGPT("gpt-3.5-turbo","config/gpt3keys.txt");

        this.npcSystem = new NPCSystem(gptModel);

    }

    private void initializeView() {
        for (NPCModel npc : npcs) {
            view.addNpcTab(npc);
        }
        view.loadItemsIntoPanel(backpackItems, worldObjects, gameAssets);
    }

    @Override
    public void handleUserInput(String input) {
        // 处理用户输入
    }

    @Override
    public String interactWithNPC(NPCModel npc, String userInput) {
        if (userInput != null && !userInput.trim().isEmpty()) {
            String response = npcSystem.interact(npc, userInput);
            // 可以在这里添加更多逻辑，如更新模型等
            return response;
        }
        return "";
    }

    public List<NPCModel> getNpcs() {
        return npcs;
    }

    public List<ItemModel> getBackpackItems() {
        return backpackItems;
    }

    public List<ItemModel> getWorldObjects() {
        return worldObjects;
    }

    public List<ItemModel> getGameAssets() {
        return gameAssets;
    }
}