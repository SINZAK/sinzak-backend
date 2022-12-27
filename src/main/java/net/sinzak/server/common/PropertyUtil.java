package net.sinzak.server.common;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:/application-real.properties")
public class PropertyUtil implements EnvironmentAware {
    private static Environment environment;
    public static String SUCCESS_WORD ="success";

    @Override public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }
    public static JSONObject response(boolean bl){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        if(bl){
            obj.put(SUCCESS_WORD,true);
        }
        else
            obj.put(SUCCESS_WORD,false);
        return obj;
    }
    public static JSONObject responseMessage(HttpStatus status, String message){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        obj.put(SUCCESS_WORD, false);
        obj.put("code",status.value());
        obj.put("message", message);
        return obj;
    }

    public static JSONObject responseMessage(String message){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        obj.put(SUCCESS_WORD, false);
        obj.put("message", message);
        return obj;
    }

}

