package function;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class functionDefinitionRegistry {
    //现在需要根据open AI的function call的协议格式完成属性注入
    public static String toToolsJson() {
        JsonArray tools = new JsonArray();

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");

        JsonObject function = new JsonObject();
        function.addProperty("name", "get_weather");
        function.addProperty("description", "根据城市查询当前天气情况");

        JsonObject parameters = new JsonObject();
        parameters.addProperty("type", "object");

        JsonObject properties = new JsonObject();
        JsonObject city = new JsonObject();
        city.addProperty("type", "string");
        city.addProperty("description", "例如北京，上海，杭州之类的城市名");
        properties.add("city", city);

        parameters.add("properties", properties);

        JsonArray required = new JsonArray();
        required.add("city");
        parameters.add("required", required);

        function.add("parameters", parameters);
        tool.add("function", function);
        tools.add(tool);

        return tools.toString();
    }

}
