package net.sinzak.server.oauth;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.user.dto.request.OauthDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String productURL = "https://sinzak.net";
    private static final String developURL = "http://localhost:8080";

    @Value("${google.client-id}")
    private String GOOGLE_ID;
    @Value("${google.client-secret}")
    private String GOOGLE_SECRET;
    @Value("${kakao.client-id}")
    private String KAKAO_ID;
    @Value("${naver.client-id}")
    private String NAVER_ID;
    @Value("${naver.client-secret}")
    private String NAVER_SECRET;

    public JSONObject getOauthInfo(OauthDto tokenDto) throws IOException, ParseException {
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

    public String getKakaoAccessToken(String code) throws IOException, ParseException {
        String url = "https://kauth.kakao.com/oauth/token"
                + "?client_id="+KAKAO_ID
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
        return response.get("access_token").toString();
    }

    public JSONObject getGoogleAccessToken(String code) throws IOException, ParseException {
        String url = "https://oauth2.googleapis.com/token"
                + "?client_id="+GOOGLE_ID
                + "&client_secret="+GOOGLE_SECRET
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
        return response;
    }

    public JSONObject getNaverAccessToken(String code) throws IOException, ParseException {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id="+NAVER_ID+"&client_secret="+NAVER_SECRET+"&code="+code+"&state=9kgsGTfH4j7IyAkg";;
        System.out.println(code+'\n'+'\n');
        Request.Builder builder = new Request.Builder().header("Content-type", " application/x-www-form-urlencoded")
                .url(url);
        JSONObject postObj = new JSONObject();
        RequestBody requestBody = RequestBody.create(postObj.toJSONString().getBytes());
        builder.post(requestBody);
        Request request = builder.build();

        Response responseHTML = client.newCall(request).execute();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(responseHTML.body().string());
        return response;
    }

    public JSONObject getGoogleInfo(OauthDto dto) throws IOException, ParseException {
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

    public JSONObject getKakaoInfo(String accessToken) throws IOException, ParseException {
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


    public JSONObject getNaverInfo(String accessToken) throws IOException, ParseException {
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

    private JSONObject getAppleInfo(String id_token) {
        return decodeFromIdToken(id_token);
    }

    private JSONObject decodeFromIdToken(String id_token) {
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


    public JSONObject getWebGoogleURL(String redirect_uri, String code) throws IOException, ParseException {
        String url = "https://oauth2.googleapis.com/token"
                + "?client_id="+GOOGLE_ID
                + "&client_secret="+GOOGLE_SECRET + "&grant_type=authorization_code"
                + "&redirect_uri="+redirect_uri
                + "&code="+code;
        Request.Builder builder = new Request.Builder().header("Content-type", " application/x-www-form-urlencoded")
                .url(url);
        JSONObject postObj = new JSONObject();
        RequestBody requestBody = RequestBody.create(postObj.toJSONString().getBytes());
        builder.post(requestBody);
        Request request = builder.build();

        Response responseHTML = client.newCall(request).execute();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(responseHTML.body().string());
        return response;
    }
}
