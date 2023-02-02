package net.sinzak.server.oauth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.user.dto.request.EmailDto;
import net.sinzak.server.user.dto.request.OauthDto;
import net.sinzak.server.user.service.UserQueryService;
import okhttp3.*;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "소셜로그인")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OauthController {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String productURL = "https://sinzak.net";
    private static final String developURL = "http://localhost:8080";
    private final UserQueryService userQueryService;
    private final SecurityService securityService;

    @ApiOperation(value = "스프링용 카카오로그인 실행",notes = "배포환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=" +
            "https://sinzak.net/api/login/oauth2/code/kakao&response_type=code\n" +
            "로컬환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=" +
            "http://localhost:8080/api/login/oauth2/code/kakao&response_type=code")
    @GetMapping("/test")
    public String kakaoLogin() throws IOException {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=3201538a34f65dfa0fb2e96b0d268ca7"
                + "&redirect_uri="+productURL+"/api/login/oauth2/code/kakao"
                + "&response_type=code";
        return url;
    }

    @ApiOperation(value = "스프링용 카카오 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/kakao")
    public String oauthKakao(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        String accessToken = getKakaoAccessToken(code);
        log.warn("액세스토큰 = {}",accessToken);
//        JSONObject info = getInfo(code);
//        OAuthAttributes OauthUser = OAuthAttributes.of("kakao", info);
//        return OauthUser.toString();
        return accessToken;
    }

    private String getKakaoAccessToken(String code) throws IOException, ParseException {
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

    @ApiOperation(value = "스프링용 구글로그인 실행",notes = "로컬환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id=725362946704-p0fr9q566ph10pl0is8dm8e3jq5klfe7.apps.googleusercontent.com" +
            "&redirect_uri=http://localhost:8080/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true"+'\n'+
            "배포환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
            "&redirect_uri=https://sinzak.net/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true")
    @GetMapping("/test2")
    public String googleLogin() throws IOException {
        String url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
                "&redirect_uri="+ productURL +"/api/login/oauth2/code/google";
        return url;
    }

    @ApiOperation(value = "스프링용 구글 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/google")
    public String oauthGoogle(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        JSONObject obj = getGoogleAccessToken(code);
        log.warn("액세스토큰 = {}",obj.get("access_token").toString());
        return obj.toJSONString();
    }

    private JSONObject getGoogleAccessToken(String code) throws IOException, ParseException {
        String url = "https://oauth2.googleapis.com/token"
                + "?client_id=725362946704-p0fr9q566ph10pl0is8dm8e3jq5klfe7.apps.googleusercontent.com"
                + "&client_secret=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"
                + "&redirect_uri="+productURL+"/api/login/oauth2/code/google"
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
        return response;
    }

    private JSONObject getGoogleInfo(OauthDto dto) throws IOException, ParseException {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token="+dto.getIdToken();
        Request.Builder builder = new Request.Builder()
                .header("Authorization","Bearer "+dto.getAccessToken())
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .url(url);
        Request request = builder.build();

        Response responseHTML = client.newCall(request).execute();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(responseHTML.body().string());
        return response;
    }

    private JSONObject getKakaoInfo(String accessToken) throws IOException, ParseException {
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

    @ApiOperation(value = "(카카오) 액세스토큰 body에 넣어주세요.  유저정보 가져오기", notes = "웹, 안드, ios 용")
    @PostMapping(value = "/oauth/get/kakao")
    public JSONObject oauthKakao(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto) throws Exception {
        JSONObject info = getKakaoInfo(tokenDto.getAccessToken());
        OAuthAttributes OauthUser = OAuthAttributes.of("kakao", info);
        TokenDto jwtToken = securityService.login(new EmailDto(OauthUser.getEmail()));

        return PropertyUtil.response(jwtToken);
    }

    @ApiOperation(value = "(구글) 액세스토큰과 idToken을 body에 넣어주세요.  유저정보 가져오기", notes = "웹, 안드, ios 용")
    @PostMapping(value = "/oauth/get/google")
    public JSONObject oauthGoogle(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto) throws Exception {
        JSONObject info = getGoogleInfo(tokenDto);
        OAuthAttributes OauthUser = OAuthAttributes.of("google", info);
        log.error(OauthUser.toString());
        TokenDto jwtToken = securityService.login(new EmailDto(OauthUser.getEmail()));

        return PropertyUtil.response(jwtToken);
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