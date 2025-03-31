package controller;

import api.OpenAIGPT;
import interfaces.GameControllerInterface;
import model.AdaptiveTaskModel;
import prompt.TaskPrompts;
import system.*;
import clinic.huatuoAPI;
import system.StageManager.StageInfo;
import system.SRLStageManager.SRLStageInfo;
import view.GameView;
import com.fasterxml.jackson.core.type.TypeReference;
import model.NPCModel;
import model.ItemModel;
import model.TaskModel;
import util.JsonLoader;

import java.util.HashMap;
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
    private List<StageManager.StageInfo> stageInfos;
    private List<SRLStageManager.SRLStageInfo> srlStageInfos;
    private NPCSystem npcSystem;
    private ExpertSystem expertSystem;
    private TaskSystem taskSystem;
    private StageManager stageManager;
    private SRLStageManager srlStageManager;

    private OpenAIGPT gptModel;
    private OpenAIGPT expertModel;
    private huatuoAPI clinicModel;

    private Map<String, String> taskCoinLocations; // 任务 ID 和位置的映射
    private KnowledgeGraphManager knowledgeManager;
    private KnowledgeTaskMonitor knowledgeMonitor;

    private boolean srlQuestAvailable = true;

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

        adaptiveTasks = JsonLoader.loadObjectListFromJson(
                "json/adaptive_tasks.json",
                "adaptive_tasks",
                new TypeReference<List<AdaptiveTaskModel>>() {}
        );

        gptModel = new OpenAIGPT("gpt-4o","config/gpt3keys.txt");
//        gptModel = new OpenAIGPT("bot-20241220150201-tvbhd","config/doubao.txt");  // Doubao API
        expertModel = new OpenAIGPT("gpt-3.5-turbo","config/gpt3keys.txt");
        clinicModel = new huatuoAPI("huatuogpt-lg-main");

        this.npcSystem = new NPCSystem(gptModel,clinicModel);
        this.expertSystem = new ExpertSystem(expertModel);

        // 获取StageManager实例
        this.stageManager = StageManager.getInstance();
        this.srlStageManager = SRLStageManager.getInstance();

        // 加载阶段信息并设置到StageManager
        stageInfos = JsonLoader.loadObjectListFromJson(
                "json/stage_config.json",
                "stages",
                new TypeReference<List<StageInfo>>() {}
        );
        srlStageInfos = JsonLoader.loadObjectListFromJson(
                "json/srl_stage_config.json",
                "stages",
                new TypeReference<List<SRLStageInfo>>() {}
        );

        // 将阶段信息设置到StageManager
        stageManager.setStages(stageInfos);
        srlStageManager.setStages(srlStageInfos);

        this.taskSystem = new TaskSystem();
        // 从配置文件加载任务金币位置信息
        //taskCoinLocations = JsonLoader.loadTaskCoinLocations("json/task_coin_location.json");

        knowledgeManager = KnowledgeGraphManager.getInstance();
        knowledgeMonitor = KnowledgeTaskMonitor.getInstance();
        knowledgeMonitor.startMonitoring(); // 启动监控
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

    /**
     * 检查SRLQuest是否可用
     * @return SRLQuest的可用状态
     */
    public boolean isSRLQuestAvailable() {
        return srlQuestAvailable;
    }

    /**
     * 设置SRLQuest的可用状态
     * @param available 是否可用
     */
    public void setSRLQuestAvailable(boolean available) {
        this.srlQuestAvailable = available;
        System.out.println("SRLQuest availability set to: " + available);
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

    // 获取StageManager的方法
    public StageManager getStageManager() {
        return stageManager;
    }
    public SRLStageManager getSRLStageManager() {
        return srlStageManager;
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