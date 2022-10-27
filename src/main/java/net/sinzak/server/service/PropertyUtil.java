package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:/application-real.properties")
public class PropertyUtil implements EnvironmentAware {
    private static Environment environment;

    @Override public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }
    public static JSONObject response(boolean bl){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        if(bl)
            obj.put("success",true);
        else
            obj.put("success",false);
        return obj;
    }

    public static JSONObject responseMessage(String message){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        obj.put("success", false);
        obj.put("message", message);
        return obj;
    }
}

