package com.freedomai.projectpn.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.freedomai.projectpn.model.NPCModel;
import com.freedomai.projectpn.model.TaskModel;

public class JsonLoader {

    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> List<T> loadObjectListFromJson(String filePath, String jsonNodeName, TypeReference<List<T>> typeReference) {
        try (InputStream is = ResourcePathUtil.getResourceAsStream(filePath)) {
            if (is == null) {
                LOGGER.log(Level.SEVERE, "Resource not found: " + filePath);
                return new ArrayList<>();
            }
            JsonNode root = mapper.readTree(is);
            if (root.has(jsonNodeName)) {
                return mapper.convertValue(root.get(jsonNodeName), typeReference);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load data from JSON: " + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public static List<NPCModel> loadNPCsFromJson(String filePath) {
        List<NPCModel> npcs = new ArrayList<>();
        try (InputStream is = ResourcePathUtil.getResourceAsStream(filePath)) {
            if (is == null) {
                LOGGER.log(Level.SEVERE, "Resource not found: " + filePath);
                return npcs;
            }
            JsonNode root = mapper.readTree(is);
            if (root.has("npcs") && root.get("npcs").isArray()) {
                ArrayNode npcsNode = (ArrayNode) root.get("npcs");
                for (JsonNode npcNode : npcsNode) {
                    try {
                        NPCModel npc = parseNPC(npcNode);
                        npcs.add(npc);
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.WARNING, "NPC data error: " + e.getMessage());
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Expected 'npcs' node to be an array.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load NPCs from JSON: " + e.getMessage(), e);
        }
        return npcs;
    }

    private static NPCModel parseNPC(JsonNode npcNode) {
        String name = npcNode.path("name").asText();
        String description = npcNode.path("description").asText();
        String role = npcNode.path("role").asText();
        List<String> dialogues = new ArrayList<>();
        npcNode.path("dialogues").forEach(d -> dialogues.add(d.asText()));

        List<TaskModel> tasks = new ArrayList<>();
        if (npcNode.has("tasks") && npcNode.get("tasks").isArray()) {
            for (JsonNode taskNode : npcNode.get("tasks")) {
                String taskId = taskNode.path("task_id").asText();
                String taskDescription = taskNode.path("description").asText();
                String completionCriteria = taskNode.path("completion_criteria").asText();
                boolean completed = taskNode.path("completed").asBoolean();
                String objectName = taskNode.has("object") ? taskNode.path("object").asText() : "无需物品"; // 使用默认值或标记
                List<String> reward = new ArrayList<>();
                if (taskNode.has("reward")) {
                    taskNode.path("reward").forEach(r -> reward.add(r.asText()));
                } else {
                    reward.add("无奖励"); // 使用默认值或标记
                }
                String plotSignificance = taskNode.path("plot_significance").asText();
                tasks.add(new TaskModel(taskId, taskDescription, completionCriteria, objectName, reward, plotSignificance, completed));
            }
        }

        return new NPCModel(name, description, role, dialogues, tasks);
    }
}