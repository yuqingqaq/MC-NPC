package controller;

import api.OpenAIGPT;
import controller.GameController;
import interfaces.GameControllerInterface;
import system.ExpertSystem;
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
    private static GameController instance; // Singleton instance

    private GameView view;
    private List<NPCModel> npcs;
    private List<ItemModel> backpackItems;
    private List<ItemModel> worldObjects;
    private List<ItemModel> gameAssets;
    private NPCSystem npcSystem;
    private ExpertSystem expertSystem;
    private OpenAIGPT gptModel;
    private OpenAIGPT expertModel;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }
    private GameController() {
        loadGameData();
    }


    private void loadGameData() {

        npcs = JsonLoader.loadNPCsFromJson("json/psy_sim.json");
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
        expertModel = new OpenAIGPT("gpt-3.5-turbo","config/gpt3keys.txt");

        this.npcSystem = new NPCSystem(gptModel);
        this.expertSystem = new ExpertSystem(expertModel);

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
        if (userInput == null || userInput.trim().isEmpty()) {
            return "";
        }

        // 判断输入是主要为中文还是英文
        String language = isMostlyChinese(userInput) ? "zh" : "en";

        String response = npcSystem.interact(npc, userInput, language);

        return response;
    }

    /**
     * 判断字符串是否主要包含中文字符
     * @param text 输入文本
     * @return 如果主要是中文返回true，否则返回false
     */
    private boolean isMostlyChinese(String text) {
        int chineseCount = 0;
        int otherCount = 0;

        for (char c : text.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fff') {
                chineseCount++;
            } else {
                otherCount++;
            }
        }

        return chineseCount > otherCount;
    }

    public String interactWithExpert(NPCModel npc, String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "";
        }
        String language = isMostlyChinese(userInput) ? "zh" : "en";
        String advice = expertSystem.interact(npc, userInput, language);
        return advice;
    }
    
    public NPCModel getNPC(int index) {
        if (index >= 0 && index < npcs.size()) {
            return npcs.get(index);
        }
        return null;
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