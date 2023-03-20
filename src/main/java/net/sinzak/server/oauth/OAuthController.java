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
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "소셜로그인")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
    private final SecurityService securityService;
    private final UserCommandService userService;
    private final OAuthService oAuthService;
    private final AppleService appleService;

    private static final String productURL = "https://sinzak.net/api/login/oauth2/code";
    private static final String developURL = "http://localhost:8080";

    @Value("${google.client-id}")
    private String GOOGLE_ID;
    @Value("${kakao.client-id}")
    private String KAKAO_ID;
    @Value("${naver.client-id}")
    private String NAVER_ID;

    private final static String TEAM_ID = "F7B353LPM3";
    private final static String CLIENT_ID = "net.sinzak.sinzak";
    private final static String KEY_ID = "37QTX3F226";
    private final static String AUTH_URL = "https://appleid.apple.com";
    private final static String KEY_PATH = "static/apple/AuthKey_37QTX3F226.p8";

    @ApiOperation(value = "액세스토큰 body에 넣어주세요.", notes = "구글은 액세스토큰, idToken, origin까지 총 3개. " +
            "\n네이버, 카카오는 액세스토큰, origin 총 2개. " +
            "\n애플로그인은 idToken, origin 총 2개\n" +
            "data 안에는 공통적으로 토큰과 회원가입여부(joined)가 있습니다., \njoined = true -> 홈으로 보내면되고,  " +
            "joined = false 면 /join api 처리할 수 있게 회원가입 창으로 보내주시면 될 것 같아여")
    @PostMapping(value = "/oauth/get")
    public JSONObject getOauthToken(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto) throws Exception {
        JSONObject OauthInfo = oAuthService.getOauthInfo(tokenDto);
        OAuthAttributes OauthUser = OAuthAttributes.of(tokenDto.getOrigin(), OauthInfo);
        if(OauthUser.getEmail() == null || OauthUser.getEmail().isBlank())
            return PropertyUtil.responseMessage("회원가입 불가능(소셜로그인 실패)");
        TokenDto jwtToken;
        try{
            jwtToken = securityService.login(OauthUser.getEmail());
        }
        catch(UserNotFoundException e){
            User savedUser = userService.saveTempUser(new User(OauthUser.getEmail(), OauthUser.getName(), OauthUser.getPicture(), OauthUser.getOrigin()));
            jwtToken = securityService.login(savedUser);
        }
        return PropertyUtil.response(jwtToken);
    }

    @ApiOperation(value = "스프링용 카카오로그인 실행(인가코드)",notes = "하단 참고")
    @GetMapping("/test1")
    public String kakaoLogin() {
        return "로컬환경 : https://kauth.kakao.com/oauth/authorize?client_id="+KAKAO_ID +
                "&redirect_uri=http://localhost:8080/api/login/oauth2/code/kakao&response_type=code" +"\n"+
                "배포환경 : https://kauth.kakao.com/oauth/authorize?client_id="+KAKAO_ID +
                "&redirect_uri=https://sinzak.net/api/login/oauth2/code/kakao&response_type=code";
    }

    @ApiOperation(value = "스프링용 구글로그인 실행",notes = "로컬환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
            "&redirect_uri=http://localhost:8080/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true"+'\n'+
            "배포환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
            "&redirect_uri=https://sinzak.net/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true")
    @GetMapping("/test2")
    public String googleLogin(){
        return "로컬환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id="+GOOGLE_ID+
                "&redirect_uri=http://localhost:8080/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true" +"\n"+
                "배포환경 : https://accounts.google.com/o/oauth2/v2/auth?client_id="+GOOGLE_ID+
                "&redirect_uri=https://sinzak.net/api/login/oauth2/code/google&response_type=code&scope=profile%20email&include_granted_scopes=true";
    }

    @ApiOperation(value = "스프링용 네이버로그인 실행",notes =
            "로컬환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
                    "&redirect_uri=http://localhost:8080/api/login/oauth2/code/naver"+'\n'+
                    "배포환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
                    "&redirect_uri=https://sinzak.net/api/login/oauth2/code/naver")
    @GetMapping("/test3")
    public String naverLogin(){
        return "로컬환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+NAVER_ID+"&state=9kgsGTfH4j7IyAkg"+
                "&redirect_uri=http://localhost:8080/api/login/oauth2/code/naver"+ "\n" +
                "배포환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+NAVER_ID+"&state=9kgsGTfH4j7IyAkg"+
                "&redirect_uri=https://sinzak.net/api/login/oauth2/code/naver";
    }

    @ApiOperation(value = "스프링용 애플 로그인 실행",notes =
            "배포환경 : "+AUTH_URL+"/auth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + "https://sinzak.net/api/login/oauth2/code/apple&response_type=code&id_token&response_mode=form_post")
    @GetMapping("/test4")
    public String appleLogin(){return "";}

    @ApiOperation(value = "스프링용 카카오 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/kakao")
    public JSONObject oauthKakao(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        return oAuthService.getKakaoAccessToken(productURL+"/kakao", code);
    }

    @ApiOperation(value = "스프링용 구글 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/google")
    public String oauthGoogle(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        JSONObject obj = oAuthService.getGoogleAccessToken(productURL+"/google", code);
        return obj.toJSONString();
    }

    @ApiOperation(value = "스프링용 네이버 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/login/oauth2/code/naver")
    public String oauthNaver(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        JSONObject obj = oAuthService.getNaverAccessToken(code);
        return obj.toJSONString();
    }

    @ApiOperation(value = "스프링용 애플 코드 반환 로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @PostMapping(value = "/login/oauth2/code/apple")
    public String oauthApple(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        String client_id = CLIENT_ID;
        String client_secret = appleService.createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);
        String reqUrl = AUTH_URL + "/auth/token";
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("client_id", client_id);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("code", code);
        tokenRequest.put("grant_type", "authorization_code");
        String apiResponse = appleService.doPost(reqUrl, tokenRequest);
        log.warn("apiResponse = {}", apiResponse);
        return apiResponse;
    }

    @ApiOperation(value = "웹용 카카오 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/web/kakao")
    public JSONObject oauthWebKakao(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "redirect_uri") String redirect_uri) throws Exception {
        return oAuthService.getKakaoAccessToken(redirect_uri, code);
    }

    @ApiOperation(value = "웹용 구글 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/web/google")
    public JSONObject oauthWebGoogle(@RequestParam(value = "code") String code, @RequestParam(value = "redirect_uri") String redirect_uri) throws Exception {
        JSONObject obj = oAuthService.getGoogleAccessToken(redirect_uri, code);
        return obj;
    }

    @ApiOperation(value = "웹용 네이버 액세스토큰 추출로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @GetMapping(value = "/web/naver")
    public String oauthWebNaver(@RequestParam(value = "code", required = false) String code) throws Exception {
        JSONObject obj = oAuthService.getNaverAccessToken(code);
        return obj.toJSONString();
    }


}