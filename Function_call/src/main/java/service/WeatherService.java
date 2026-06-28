package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WeatherService {
//    查询天气的demo
    private final Gson gson=new Gson();

    /**
     * 模拟查询天气的行为
     * @param city
     * @return
     */
    public  String  getWeather(String city){
        return city+":晴，，22°C，微风";
    }

    /**
     * 解析调用API之后返回的参数
     * @param argumentsGson
     * @return
     */
    public String execute(String argumentsGson){
        JsonObject args=gson.fromJson(argumentsGson,JsonObject.class);
        String city=args.get("city").getAsString();
        return getWeather(city);
    }
}
