package com.cookbook.prompt;

import java.io.IOException;

/**
 * Prompt 工程对比 —— 同一个任务，有无 Prompt 约束，两次真实 API 调用，直观对比差异。
 *
 * <pre>
 *   mvn compile exec:java -Dexec.mainClass="com.cookbook.prompt.PromptEngineeringDemo"
 * </pre>
 */
public class PromptEngineeringDemo {

    // ======================== 任务场景 ========================
    // 让 LLM 写一个 Java 函数，判断给定字符串是否为回文

    // ======================== 糟糕的 Prompt ========================
    static final String BAD_SYSTEM_PROMPT = "You are a helpful assistant.";

    static final String BAD_USER_PROMPT = """
            写一个判断回文的函数。
            """;

    // ======================== 良好的 Prompt ========================
    static final String GOOD_SYSTEM_PROMPT = """
            你是一名资深 Java 后端工程师，拥有 10 年以上经验，擅长编写清晰、健壮、可维护的代码。
            你的代码风格遵循《Effective Java》和《Clean Code》的最佳实践。

            回答要求：
            - 代码必须能直接编译运行
            - 考虑边界条件（null、空字符串、特殊字符）
            - 包含完整的 Javadoc 注释
            - 附带单元测试示例
            - 说明时间复杂度和空间复杂度
            """;

    static final String GOOD_USER_PROMPT = """
            请实现一个 Java 方法来判断字符串是否为回文。

            ## 需求规格
            - 方法名：isPalindrome
            - 回文定义：正读和反读都一样的字符串，忽略大小写，只考虑字母和数字字符
            - 输入示例 -> 期望输出：
              - "A man, a plan, a canal: Panama" -> true
              - "race a car" -> false
              - "  " -> true（空字符串或仅空白视为回文）
              - null -> false
              - "a." -> true
            - 性能要求：O(n) 时间复杂度，O(1) 额外空间
            - 必须处理 Unicode 字符

            ## 输出格式
            1. 先给出思路分析（2-3句话）
            2. 完整可运行的 Java 代码
            3. 3 个测试用例
            4. 复杂度分析
            """;

    // ======================== API 配置 ========================
    static final String API_BASE_URL = "https://api.siliconflow.cn";//这里用的硅基流动平台
    static final String API_KEY = "你的API密钥";
    static final String API_MODEL = "deepseek-ai/DeepSeek-V4-Pro";

    public static void main(String[] args) {
        LlmClient client = new LlmClient(API_BASE_URL, API_KEY, API_MODEL);

        // ========== 第一轮：无 Prompt 约束 ==========
        printSeparator("第 1 轮：未加 Prompt 约束");
        System.out.println(">>> System Prompt:");
        System.out.println(BAD_SYSTEM_PROMPT);
        System.out.println("\n>>> User Prompt:");
        System.out.println(BAD_USER_PROMPT);
        System.out.println("\n... 调用 API 中 ...\n");

        String badResult = callApi(client, BAD_SYSTEM_PROMPT, BAD_USER_PROMPT);
        System.out.println("<<< API 返回：");
        System.out.println(badResult);

        // ========== 第二轮：加了 Prompt 约束 ==========
        printSeparator("第 2 轮：加入 Prompt 约束");
        System.out.println(">>> System Prompt:");
        System.out.println(GOOD_SYSTEM_PROMPT);
        System.out.println("\n>>> User Prompt:");
        System.out.println(GOOD_USER_PROMPT);
        System.out.println("\n... 调用 API 中 ...\n");

        String goodResult = callApi(client, GOOD_SYSTEM_PROMPT, GOOD_USER_PROMPT);
        System.out.println("<<< API 返回：");
        System.out.println(goodResult);

        printSeparator("");
        System.out.println("以上两次 API 调用完成。同一个任务，Prompt 不同，结果差异一目了然。");
    }

    private static String callApi(LlmClient client, String system, String user) {
        try {
            return client.chat(system, user);
        } catch (IOException e) {
            return "API 调用失败: " + e.getMessage();
        }
    }

    // ======================== 工具方法 ========================

    private static void printSeparator(String title) {
        if (title.isEmpty()) {
            System.out.println("\n" + "-".repeat(70) + "\n");
        } else {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("  " + title);
            System.out.println("=".repeat(70));
        }
    }

}
