package client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

/**
 * OpenAI 客户端封装
 * 用 OkHttp 直接调 OpenAI HTTP API
 */
public class OpenAiClient {

    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String MODEL = "deepseek-ai/DeepSeek-V3";

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;

    public OpenAiClient(String apiKey) {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
        this.apiKey = apiKey;
    }

    /**
     * 第一轮：发送用户消息，让模型决定是否调用函数
     *
     * @param userMessage 用户输入
     * @param functionsJson 函数定义（JSON 字符串）
     * @return 模型返回的完整响应 JSON
     */
    public JsonObject sendFirstRound(String userMessage, String functionsJson) throws IOException {
        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL);

        // messages 数组
        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        requestBody.add("messages", messages);

        // tools 数组
        JsonArray tools = gson.fromJson(functionsJson, JsonArray.class);
        requestBody.add("tools", tools);

        // tool_choice = auto（让模型自己决定是否调用）
        requestBody.addProperty("tool_choice", "auto");

        // 发送 HTTP 请求
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            ))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonObject.class);
        }
    }

    /**
     * 第二轮：把函数执行结果发给模型，让它生成最终回答
     *
     * @param conversationHistory 完整对话历史（3 条消息）
     * @return 模型生成的最终回答文本
     */
    public String sendSecondRound(JsonArray conversationHistory) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL);
        requestBody.add("messages", conversationHistory);

        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            ))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            return json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        }
    }
}