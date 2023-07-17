package net.sinzak.server.common;

import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;


public class SinzakResponse {
    private boolean success;
    private Object data;
    private String message = "";

    public SinzakResponse(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static JSONObject success() {
        return makeResponseDto(new SinzakResponse(true, null, null));
    }

    public static JSONObject success(Object data) {
        return makeResponseDto(new SinzakResponse(true, data, null));
    }

    public static JSONObject success(Long id) {
        JSONObject obj = makeResponseDto(new SinzakResponse(true, null, null));
        obj.put("id",id);
        return obj;
    }

    public static JSONObject error(String message) {
        return makeResponseDto(new SinzakResponse(false, null, message));
    }

    private static JSONObject makeResponseDto(SinzakResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("success", response.success);
        obj.put("data", response.data);
        obj.put("message", response.message);
        return obj;
    }

    public static void checkHeader(User user) {
        if(user == null)
            throw new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN);
    }

}
