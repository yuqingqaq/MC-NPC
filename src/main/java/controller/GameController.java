package controller;

import api.OpenAIGPT;
import interfaces.GameControllerInterface;
import model.AdaptiveTaskModel;
import system.ExpertSystem;
import system.NPCSystem;
import clinic.huatuoAPI;
import system.TaskSystem;
import view.GameView;
import com.fasterxml.jackson.core.type.TypeReference;
import model.NPCModel;
import model.ItemModel;
import model.TaskModel;
import util.JsonLoader;

import java.util.List;
import java.util.Map;

public class GameController implements GameControllerInterface {
    private static GameController instance; // Singleton instance

    private GameView view;
    private List<NPCModel> npcs;
    private List<ItemModel> backpackItems;
    private List<ItemModel> worldObjects;
    private List<ItemModel> gameAssets;
    private List<AdaptiveTaskModel> adaptiveTasks;
    private NPCSystem npcSystem;
    private ExpertSystem expertSystem;
    private TaskSystem taskSystem;
    private OpenAIGPT gptModel;
    private OpenAIGPT expertModel;
    private huatuoAPI clinicModel;

    private Map<String, String> taskCoinLocations; // 任务 ID 和位置的映射
    private KnowledgeGraphManager knowledgeManager;
    private KnowledgeTaskMonitor knowledgeMonitor;

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

//        npcs = JsonLoader.loadNPCsFromJson("json/psy_sim.json");
        npcs = JsonLoader.loadNPCsFromJson("json/campus_tour.json");
        backpackItems = JsonLoader.loadObjectListFromJson(
                "json/playerBackpack.json",
                "player_backpack",
                new TypeReference<List<ItemModel>>() {}
        );
        adaptiveTasks = JsonLoader.loadObjectListFromJson(
                "json/adaptive_tasks.json",
                "adaptive_tasks",
                new TypeReference<List<AdaptiveTaskModel>>() {}
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
        gptModel = new OpenAIGPT("gpt-4o","config/gpt3keys.txt");
//        gptModel = new OpenAIGPT("bot-20241220150201-tvbhd","config/doubao.txt");  // Doubao API
        expertModel = new OpenAIGPT("gpt-3.5-turbo","config/gpt3keys.txt");
        clinicModel = new huatuoAPI("huatuogpt-lg-main");

        this.npcSystem = new NPCSystem(gptModel,clinicModel);
        this.expertSystem = new ExpertSystem(expertModel);

        this.taskSystem = new TaskSystem();
        taskCoinLocations = JsonLoader.loadTaskCoinLocations("json/task_coin_location.json");
        initializeTasksAndNPCs(this.npcs);
        knowledgeManager = KnowledgeGraphManager.getInstance();
        knowledgeMonitor = KnowledgeTaskMonitor.getInstance();
        knowledgeMonitor.startMonitoring(); // 启动监控
    }

    public void initializeTasksAndNPCs(List<NPCModel> npcs) {
        for (NPCModel npc : npcs) {
            // 获取 NPC 的位置
            String npcLocation = npc.getLocation();
            System.out.println("NPC Location: " + npcLocation);

            for (TaskModel task : npc.getTasks()) {
                String taskLocation = taskCoinLocations.get(npcLocation);
                if (taskLocation != null) {
                    task.setCoinLocation(taskLocation);
                    //System.out.println("Task ID: " + task.getTaskId() + ", Location: " + taskLocation);
                } else {
                    System.out.println("No location found for NPC Location: " + npcLocation);
                }

                this.taskSystem.addTask(task);
            }

            this.taskSystem.addNPCToStage(npcLocation, npc);
        }
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

    public List<AdaptiveTaskModel> getAdaptiveTasks() {
        return adaptiveTasks;
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


    public TaskSystem getTaskSystem() {
        return this.taskSystem;
    }

    // 可以添加一个重置方法
    public void resetKnowledgeMonitoring() {
        knowledgeMonitor.stopMonitoring();
        knowledgeMonitor.startMonitoring();
    }
    /**
     * 添加概念到知识图谱
     */
    public void addConceptToKnowledgeGraph(String conceptName) {
        knowledgeManager.addConcept(conceptName);
        // 可以在这里添加游戏效果，如声音、粒子等
    }

    /**
     * 添加概念关系到知识图谱
     */
    public void addRelationshipsToKnowledgeGraph(String relationshipCategory) {
        knowledgeManager.addRelationship(relationshipCategory);
    }

    /**
     * 验证智能体原则
     */
    public void validateAgentPrinciple(String principle) {
        knowledgeManager.addPrinciple(principle);
    }

    /**
     * 添加时间线到知识图谱
     */
    public void addTimelineToKnowledgeGraph(String timeline) {
        knowledgeManager.addTimeline(timeline);
    }

    /**
     * 获取知识图谱完成百分比
     */
    public int getKnowledgeGraphCompletion() {
        return knowledgeManager.getOverallCompletionPercentage();
    }

    /**
     * 获取指定知识领域的完成百分比
     */
    public int getKnowledgeAreaCompletion(String area) {
        return knowledgeManager.getCompletionPercentage(area);
    }
}