package controller;

import controller.GameController;
import view.GameView;
import com.fasterxml.jackson.core.type.TypeReference;
import model.NPCModel;
import model.ItemModel;
import model.TaskModel;
import util.JsonLoader;

import java.io.IOException;
import java.util.List;

public class GameController {
    private GameView view;
    private List<NPCModel> npcs;
    private List<ItemModel> backpackItems;
    private List<ItemModel> worldObjects;
    private List<ItemModel> gameAssets;

    public GameController(GameView view) {
        this.view = view;
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
    }

    private void initializeView() {
        for (NPCModel npc : npcs) {
            view.addNpcTab(npc.getNPCName(), npc.getDescription(), npc.getTasks());
        }
        view.loadItemsIntoPanel(backpackItems, worldObjects, gameAssets);
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