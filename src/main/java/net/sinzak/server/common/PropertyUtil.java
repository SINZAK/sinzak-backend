package net.sinzak.server.common;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.domain.User;
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
    public static final String SUCCESS_WORD ="success";

    @Override public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }
    public static JSONObject response(boolean bl){
        JSONObject obj = new JSONObject();
        if(bl){
            obj.put(SUCCESS_WORD,true);
        }
        else
            obj.put(SUCCESS_WORD,false);
        return obj;
    }

    public static JSONObject response(Object data){
        JSONObject obj = new JSONObject();
        obj.put(SUCCESS_WORD,true);
        obj.put("data",data);
        return obj;
    }

    public static JSONObject response(Object data, boolean bl){
        JSONObject obj = new JSONObject();
        obj.put(SUCCESS_WORD,bl);
        obj.put("data",data);
        return obj;
    }

    public static JSONObject response(Long id){
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put(SUCCESS_WORD,true);
        return obj;
    }

    public static JSONObject responseMessage(String message){ //그냥 json 리턴해줄때 씀
        JSONObject obj = new JSONObject();
        obj.put(SUCCESS_WORD, false);
        obj.put("message", message);
        return obj;
    }

    public static void checkHeader(User user) {
        if(user == null)
            throw new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN);
    }

}

