package com.cookbook.prompt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * LLM API 客户端
 * 支持 OpenAI 兼容接口，可配置 baseUrl 和 apiKey 来适配不同的 LLM 服务
 */
@Slf4j
public class LlmClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final String model;

    public LlmClient(String baseUrl, String apiKey, String model) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(120))
                .build();
    }

    /**
     * 发送聊天请求
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return LLM 返回的文本内容
     */
    public String chat(String systemPrompt, String userPrompt) throws IOException {
        ChatRequest request = ChatRequest.builder()
                .model(model)
                .messages(List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userPrompt)
                ))
                .temperature(0.7)
                .maxTokens(2048)
                .build();

        String json = objectMapper.writeValueAsString(request);
        log.debug("Request: {}", json);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request httpRequest = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("API 调用失败: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            ChatResponse chatResponse = objectMapper.readValue(responseBody, ChatResponse.class);
            return chatResponse.getChoices().get(0).getMessage().getContent();
        }
    }

    // ============ 请求/响应数据模型 ============

    @Data
    @Builder
    public static class ChatRequest {
        private String model;
        private List<Message> messages;
        private double temperature;
        @JsonProperty("max_tokens")
        private int maxTokens;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String role;
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatResponse {
        private List<Choice> choices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message;
    }
}
