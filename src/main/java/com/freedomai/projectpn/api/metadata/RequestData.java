package com.freedomai.projectpn.api.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestData {
    @JsonProperty("com")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages; // 确保列表元素类型正确

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("top_p")
    private double topP;

    @JsonProperty("frequency_penalty")
    private double frequencyPenalty;

    @JsonProperty("presence_penalty")
    private double presencePenalty;

    @JsonProperty("n")
    private int n;

    public RequestData(String model, List<Message> messages, double temperature, double topP, double frequencyPenalty, double presencePenalty, int n) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.topP = topP;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
        this.n = n;
    }

    // Getters and setters
}