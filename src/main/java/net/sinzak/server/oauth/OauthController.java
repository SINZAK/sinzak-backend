package net.sinzak.server.oauth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.config.auth.SecurityService;
import net.sinzak.server.config.auth.jwt.TokenDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.OauthDto;
import net.sinzak.server.user.service.UserCommandService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    private final SecurityService securityService;
    private final UserCommandService userService;
// TODO 리팩토링 예정.

    @ApiOperation(value = "액세스토큰 body에 넣어주세요.  유저정보 가져오기", notes = "구글은 idToken 값까지 같이주기. 나머지 origin들은 생략\n" +
            "data 안에는 공통적으로 토큰이, success = true -> 홈으로 보내면되고, success = false 면 /join api 처리할 수 있게 회원가입 창으로 보내주시면 될 것 같아여")
    @PostMapping(value = "/oauth/get")
    public JSONObject getOauthToken(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto) throws Exception {
        JSONObject OauthInfo = getInfo(tokenDto);
        OAuthAttributes OauthUser = OAuthAttributes.of(tokenDto.getOrigin(), OauthInfo);
        if(OauthUser.getEmail() == null || OauthUser.getEmail().isBlank())
            return PropertyUtil.responseMessage("액세스 토큰으로 가져올 정보가 없습니다. (소셜로그인 실패)");
        TokenDto jwtToken;
        try{
            jwtToken = securityService.login(OauthUser.getEmail());
        }
        catch(UserNotFoundException e){
            User savedUser = userService.saveTempUser(new User(OauthUser.getEmail(), OauthUser.getName(), OauthUser.getPicture(), OauthUser.getOrigin()));
            jwtToken = securityService.login(savedUser);
        }
        return PropertyUtil.response(jwtToken, jwtToken.isJoined());
    }

    private JSONObject getInfo(OauthDto tokenDto) throws IOException, ParseException {
        JSONObject OauthInfo;
        if(tokenDto.getOrigin().equals("kakao"))
            OauthInfo = getKakaoInfo(tokenDto.getAccessToken());
        else if(tokenDto.getOrigin().equals("naver"))
            OauthInfo = getNaverInfo(tokenDto.getAccessToken());
        else
            OauthInfo = getGoogleInfo(tokenDto);

        return OauthInfo;
    }


    @ApiOperation(value = "스프링용 카카오로그인 실행",notes = "로컬환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=" +
            "http://localhost:8080/api/login/oauth2/code/kakao&response_type=code" +
            "배포환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=\" +\n" +
            "\"https://sinzak.net/api/login/oauth2/code/kakao&response_type=code\\n")
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

    @ApiOperation(value = "스프링용 구글로그인 실행",notes = "로컬환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
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
                + "?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com"
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


    @ApiOperation(value = "스프링용 네이버로그인 실행",notes =
            "로컬환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
            "&redirect_uri="+developURL+"/login/oauth2/code/naver"+'\n'+
            "배포환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
            "&redirect_uri="+productURL+"/login/oauth2/code/naver&response_type=code&scope=profile%20email&include_granted_scopes=true")
    @GetMapping("/test3")
    public void naverLogin() throws IOException {
    }

    @ApiOperation(value = "스프링용 구글 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/naver")
    public String oauthNaver(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        JSONObject obj = getNaverAccessToken(code);
        log.warn("액세스토큰 = {}",obj.get("access_token").toString());
        return obj.toJSONString();
    }

    private JSONObject getNaverAccessToken(String code) throws IOException, ParseException {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=DwXMEfKZq0tmkrsn6kLk&client_secret=2CAzvT18ok&code="+code+"&state=9kgsGTfH4j7IyAkg";
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

    private JSONObject getNaverInfo(String accessToken) throws IOException, ParseException {
        String url = "https://openapi.naver.com/v1/nid/me";
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


}