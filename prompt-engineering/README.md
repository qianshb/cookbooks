# Prompt Engineering Cookbook

一个简洁的 Java 工程，用于直观对比 **「糟糕的 Prompt」** 与 **「良好的 Prompt」** 对 LLM 输出质量的巨大影响。

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+

### 方式一：仅查看 Prompt 对比（无需 API Key）

```bash
cd prompt-engineering
mvn compile exec:java -Dexec.mainClass="com.cookbook.prompt.PromptEngineeringDemo"
```

程序会以**演示模式**运行，展示两种 Prompt 的模板和差异分析。

### 方式二：调用真实 LLM 进行对比

```bash
# 设置 API 密钥
export LLM_API_KEY=sk-your-key-here
export LLM_BASE_URL=https://api.openai.com   # 可选，默认 OpenAI
export LLM_MODEL=gpt-4o-mini                  # 可选，默认 gpt-4o-mini

# 运行
mvn compile exec:java -Dexec.mainClass="com.cookbook.prompt.PromptEngineeringDemo"
```

也支持任何 OpenAI 兼容接口（如 Azure OpenAI、DeepSeek、通义千问等）：

```bash
export LLM_BASE_URL=https://api.deepseek.com
export LLM_API_KEY=sk-xxx
export LLM_MODEL=deepseek-chat
```

## 项目结构

```
prompt-engineering/
├── pom.xml
├── README.md
└── src/main/java/com/cookbook/prompt/
    ├── PromptEngineeringDemo.java   # 主程序：定义并对比好/差 Prompt
    └── LlmClient.java               # LLM API 客户端（OpenAI 兼容）
```

## 核心对比

| 维度 | 糟糕的 Prompt | 良好的 Prompt |
|------|-------------|-------------|
| **角色设定** | 无，通用助手 | 资深 Java 工程师 + 代码规范要求 |
| **需求明确度** | "写一个判断回文的函数" | 完整方法签名 + 5 个输入输出示例 |
| **边界条件** | 未提及 | null、空串、特殊字符、Unicode |
| **输出格式** | 无约束 | 思路 -> 代码 -> 测试 -> 复杂度 |
| **性能约束** | 无 | O(n) 时间 / O(1) 空间 |
| **示例驱动** | 无 | 5 个具体示例 |

## 五种核心 Prompt 技巧

1. **角色扮演（Role Prompting）** — 让 LLM 扮演特定角色，提升专业度
2. **结构化输出（Structured Output）** — 明确要求的输出格式
3. **少样本提示（Few-shot Prompting）** — 用具体示例锚定期望行为
4. **约束规范（Constraints）** — 限定性能、风格、边界
5. **思维链（Chain of Thought）** — 引导 LLM 先思考再输出

## 打包运行

```bash
mvn clean package
java -jar target/prompt-engineering-0.0.1-SNAPSHOT.jar
```
