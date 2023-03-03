package net.sinzak.server.oauth;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

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

    @ApiOperation(value = "액세스토큰 body에 넣어주세요.  유저정보 가져오기", notes = "구글은 액세스토큰, idToken, origin까지 총 3개. " +
            "\n네이버, 카카오는 액세스토큰, origin 총 2개. " +
            "\n애플로그인은 idToken, origin 총 2개\n" +
            "data 안에는 공통적으로 토큰과 회원가입여부(joined)가 있습니다., \njoined = true -> 홈으로 보내면되고,  " +
            "joined = false 면 /join api 처리할 수 있게 회원가입 창으로 보내주시면 될 것 같아여")
    @PostMapping(value = "/oauth/get")
    public JSONObject getOauthToken(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto) throws Exception {
        JSONObject OauthInfo = getOauthInfo(tokenDto);
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

    private JSONObject getOauthInfo(OauthDto tokenDto) throws IOException, ParseException {
        JSONObject OauthInfo;
        if(tokenDto.getOrigin().equals("kakao"))
            OauthInfo = getKakaoInfo(tokenDto.getAccessToken());
        else if(tokenDto.getOrigin().equals("naver"))
            OauthInfo = getNaverInfo(tokenDto.getAccessToken());
        else if(tokenDto.getOrigin().equals("apple"))
            OauthInfo = getAppleInfo(tokenDto.getIdToken());
        else
            OauthInfo = getGoogleInfo(tokenDto);

        return OauthInfo;
    }


    @ApiOperation(value = "스프링용 카카오로그인 실행",notes = "로컬환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=" +
            "http://localhost:8080/api/login/oauth2/code/kakao&response_type=code" +
            "배포환경 : https://kauth.kakao.com/oauth/authorize?client_id=3201538a34f65dfa0fb2e96b0d268ca7&redirect_uri=\" +\n" +
            "\"https://sinzak.net/api/login/oauth2/code/kakao&response_type=code\\n")
    @GetMapping("/test")
    public String kakaoLogin() {
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
    public String googleLogin(){
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=782966145872-6shnmrvqi0q4sihr8etu9nrvh9jv43dh.apps.googleusercontent.com" +
                "&redirect_uri="+ developURL +"/api/login/oauth2/code/google";
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
        return (JSONObject) parser.parse(responseHTML.body().string());
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
        return (JSONObject) parser.parse(responseHTML.body().string());
    }


    @ApiOperation(value = "스프링용 네이버로그인 실행",notes =
            "로컬환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
            "&redirect_uri=http://localhost:8080/api/login/oauth2/code/naver"+'\n'+
            "배포환경 : https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=DwXMEfKZq0tmkrsn6kLk&state=STATE_STRING" +
            "&redirect_uri=https://sinzak.net/api/login/oauth2/code/naver")
    @GetMapping("/test3")
    public void naverLogin(){}

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



    public static final String TEAM_ID = "F7B353LPM3";
    public static final String CLIENT_ID = "net.sinzak.sinzak";
    public static final String KEY_ID = "37QTX3F226";

    public static final String AUTH_URL = "https://appleid.apple.com";
    public static final String KEY_PATH = "static/apple/AuthKey_37QTX3F226.p8";

    @ApiOperation(value = "스프링용 애플 로그인 실행",notes =
            "배포환경 : "+AUTH_URL+"/auth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + "https://sinzak.net/api/login/oauth2/code/apple"
            + "&response_type=code&id_token&response_mode=form_post")
    @GetMapping("/test4")
    public String appleLogin(){
        String url = AUTH_URL+"/auth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + "https://sinzak.net/api/login/oauth2/code/apple"
                + "&response_type=code id_token&response_mode=form_post";
        return url;
    }

    @ApiOperation(value = "도이님 idToken 받아보기",notes = "idToken 값에 담아주세요 액세스 토큰, 인가코드 가  아닌 애플측으로부터 id_token 으로 받았었던 값입니다")
    @PostMapping("/oauth/apple")
    public String getIdToken(@org.springframework.web.bind.annotation.RequestBody OauthDto tokenDto){
        String response = "받은 id토큰 = "+tokenDto.getIdToken();
        response+= "\n 추출한 유저의 json 값 = "+ decodeFromIdToken(tokenDto.getIdToken());
        return response;
    }

    private JSONObject getAppleInfo(String id_token) {
        return decodeFromIdToken(id_token);
    }

    @ApiOperation(value = "스프링용 애플 코드 반환 로직", notes = "웹, 안드, ios는 이 로직말고 /oauth/get으로 바로 액세스 토큰 전달해주세요")
    @RequestMapping(value = "/login/oauth2/code/apple")
    public String oauthApple(@RequestParam(value = "code", required = false) String code) throws Exception {
        log.warn("인가코드 = {}",code);
        String client_id = CLIENT_ID;
        String client_secret = createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);
        // 토큰 검증 및 발급
        String reqUrl = AUTH_URL + "/auth/token";
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("client_id", client_id);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("code", code);
        tokenRequest.put("grant_type", "authorization_code");
        String apiResponse = doPost(reqUrl, tokenRequest);
        log.warn("apiResponse = {}", apiResponse);
        return apiResponse;
    }

    public String createClientSecret(String teamId, String clientId, String keyId, String keyPath, String authUrl) throws NoSuchAlgorithmException {

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build();
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        Date now = new Date();

        claimsSet.setIssuer(teamId);
        claimsSet.setIssueTime(now);
        claimsSet.setExpirationTime(new Date(now.getTime() + 3600000));
        claimsSet.setAudience(authUrl);
        claimsSet.setSubject(clientId);

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readPrivateKey(keyPath));
        KeyFactory kf = KeyFactory.getInstance("EC");
        try {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey.getS());
            jwt.sign(jwsSigner);
        } catch (JOSEException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return jwt.serialize();
    }

    private byte[] readPrivateKey(String keyPath) {

        ClassPathResource resource = new ClassPathResource(keyPath);
        byte[] content = null;

        try (Reader keyReader = new InputStreamReader(resource.getInputStream());
             PemReader pemReader = new PemReader(keyReader)) {
            {
                PemObject pemObject = pemReader.readPemObject();
                content = pemObject.getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public String doPost(String url, Map<String, String> param) {
        String result = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        Integer statusCode;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> nvps = new ArrayList<>();
            Set<Map.Entry<String, String>> entrySet = param.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                nvps.add(new BasicNameValuePair(fieldName, fieldValue));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(formEntity);
            response = httpclient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");

            if (statusCode != 200) {
                System.out.println("애플로그인 애러");
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public JSONObject decodeFromIdToken(String id_token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            String appleInfo = getPayload.toJSONObject().toJSONString();
            JSONParser parser = new JSONParser();
            JSONObject payload = (JSONObject)parser.parse(appleInfo);

            if (payload != null) {
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}