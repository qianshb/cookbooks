import client.OpenAiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import function.FunctionExecutor;
import function.functionDefinitionRegistry;

import java.io.IOException;

/**
 * 程序入口 —— 串联整个 Function Call 流程
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // 从环境变量读取 API Key
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("请先设置环境变量 OPENAI_API_KEY");
            return;
        }

        // 初始化组件
        OpenAiClient client = new OpenAiClient(apiKey);
        FunctionExecutor executor = new FunctionExecutor();
        Gson gson = new Gson();

        // 用户提问
        String userQuestion = "北京今天天气怎么样？";
        System.out.println("用户：" + userQuestion);

        // ===== 第一轮：发给模型，看它要不要调函数 =====
        JsonObject firstResponse = client.sendFirstRound(
            userQuestion,
            functionDefinitionRegistry.toToolsJson()
        );

        // 解析模型返回的消息
        JsonObject message = firstResponse.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .getAsJsonObject("message");

        // 检查是否有 tool_calls
        if (message.has("tool_calls")) {
            JsonObject toolCall = message.getAsJsonArray("tool_calls")
                .get(0).getAsJsonObject();
            String toolCallId = toolCall.get("id").getAsString();
            JsonObject function = toolCall.getAsJsonObject("function");
            String functionName = function.get("name").getAsString();
            String arguments = function.get("arguments").getAsString();

            System.out.println("模型要调函数：" + functionName);
            System.out.println("参数：" + arguments);

            // 执行函数
            String functionResult = executor.execute(functionName, arguments);
            System.out.println("函数返回：" + functionResult);

            // ===== 组装第二轮对话历史 =====
            JsonArray messages = new JsonArray();

            // 第 1 条：用户原始提问
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", userQuestion);
            messages.add(userMsg);

            // 第 2 条：模型的 tool_calls 消息
            messages.add(message);

            // 第 3 条：函数执行结果（tool 角色）
            JsonObject functionMsg = new JsonObject();
            functionMsg.addProperty("role", "tool");
            functionMsg.addProperty("tool_call_id", toolCallId);
            functionMsg.addProperty("name", functionName);
            functionMsg.addProperty("content", functionResult);
            messages.add(functionMsg);

            // ===== 第二轮：让模型生成最终回答 =====
            String finalAnswer = client.sendSecondRound(messages);
            System.out.println("AI：" + finalAnswer);

        } else {
            // 模型没调函数，直接回答了
            String directAnswer = message.has("content") && !message.get("content").isJsonNull()
                ? message.get("content").getAsString() : "";
            System.out.println("AI（未调函数）：" + directAnswer);
        }
    }
}