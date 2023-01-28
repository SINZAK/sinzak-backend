package net.sinzak.server.oauth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = "소셜로그인")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OauthController {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String productURL = "https://sinzak.net";
    private static final String developURL = "http://localhost:8080";

    @ApiOperation(value = "스프링용 카카오로그인 실행",notes = "인가코드 받는 기능")
    @GetMapping("/oauth2/authorization/kakao")
    public  String kakaoLogin() throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=3201538a34f65dfa0fb2e96b0d268ca7"
                + "&redirect_uri="+productURL+"/api/login/oauth2/code/kakao"
                + "&response_type=code";
        return url;
    }

    @ApiOperation(value = "인가코드 전달받고 유저정보 가져오기", notes = "웹, 안드, ios 용")
    @GetMapping(value = "/login/oauth2/code/kakao")
    public String oauthKakao(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        String accessToken = getAccessToken(code);
        JSONObject info = getInfo(accessToken);
        OAuthAttributes OauthUser = OAuthAttributes.of("kakao", info);
        return OauthUser.toString();
    }

    private String getAccessToken(String code) throws IOException, ParseException {
        String url = "https://kauth.kakao.com/oauth/token"
                + "?client_id=3201538a34f65dfa0fb2e96b0d268ca7"
                + "&redirect_uri="+productURL+"/api/login/oauth2/code/kakao"
                + "&grant_type=authorization_code"
                + "&code=" + code;
        Request.Builder builder = new Request.Builder().header("Content-type", " application/x-www-form-urlencoded")
                .url(url);
        JSONObject postObj = new JSONObject();
        RequestBody requestBody = RequestBody.create(postObj.toJSONString().getBytes());
        builder.post(requestBody);
        Request request = builder.build();

        Response responseHTML = client.newCall(request).execute();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(responseHTML.body().string());
        log.warn(response.toJSONString());
        return response.get("access_token").toString();
    }

    private JSONObject getInfo(String accessToken) throws IOException, ParseException {
        String url = "https://kapi.kakao.com/v2/user/me";
        Request.Builder builder = new Request.Builder()
                .header("Authorization","Bearer "+accessToken)
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .url(url);
        Request request = builder.build();

        Response responseHTML = client.newCall(request).execute();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(responseHTML.body().string());
        return response;
    }


//    @GetMapping("/oauth2/authorization/kakao")
//    public void kakaoLogin() throws IOException {
//        String id = "80bafa3a542e6efeb296c345fa846c71";
//        String redirect_uri = "http://localhost:8080/login/oauth2/code/kakao";
//        String url = "https://kauth.kakao.com/oauth/authorize";
//        HttpUrl.Builder http = HttpUrl.get(url).newBuilder()
//                .addQueryParameter("response_type", "code")
//                .addQueryParameter("client_id", id)
//                .addQueryParameter("redirect_uri", redirect_uri);
//        Request.Builder builder = new Request.Builder().url(http.build()).get();
//        Request request = builder.build();
//        Response responseHTML = client.newCall(request).execute();
//        System.out.println(responseHTML.body().string());
//    }



}