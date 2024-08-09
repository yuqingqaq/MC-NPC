package controller;

import api.OpenAIGPT;
import controller.GameController;
import interfaces.GameControllerInterface;
import system.NPCSystem;
import view.GameView;
import com.fasterxml.jackson.core.type.TypeReference;
import model.NPCModel;
import model.ItemModel;
import model.TaskModel;
import util.JsonLoader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GameController implements GameControllerInterface {
    private GameView view;
    private List<NPCModel> npcs;
    private List<ItemModel> backpackItems;
    private List<ItemModel> worldObjects;
    private List<ItemModel> gameAssets;
    private NPCSystem npcSystem;
    private OpenAIGPT gptModel;

    public GameController(GameView view) {
        this.view = view;
        this.view.setController(this);
        loadGameData();
        initializeView();
    }

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