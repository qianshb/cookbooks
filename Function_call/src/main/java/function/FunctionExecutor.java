package function;

import service.WeatherService;

/**
 * 函数执行器 —— 统一调度中心
 * 模型说"我要调 xxx 函数"，由这里找到对应的服务并执行
 */
public class FunctionExecutor {

    private final WeatherService weatherService = new WeatherService();

    /**
     * 根据函数名执行对应的服务
     *
     * @param functionName 模型要调用的函数名
     * @param arguments    模型生成的参数（JSON 字符串）
     * @return 函数执行结果
     */
    public String execute(String functionName, String arguments) {
        switch (functionName) {
            case "get_weather":
                return weatherService.execute(arguments);
            default:
                return "未知函数：" + functionName;
        }
    }
}