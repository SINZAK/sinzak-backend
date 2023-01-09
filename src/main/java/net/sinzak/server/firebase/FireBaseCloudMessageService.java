package net.sinzak.server.firebase;

//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FireBaseCloudMessageService {
    private static final String FIREBASE_CONFIG_PATH = "src/main/resources/fireBaseAccountKey.json";
    private static final String GOOGLE_CLOUD_URL = "https://www.googleapis.com/auth/cloud-platform";
    private static final String API_URL = "https://www.fcm.googleapis.com/v1/projects/sinzak-372703/messages:send";
    private final ObjectMapper objectMapper;
    public void sendMessageTo(String targetToken, String title,String body) throws IOException{ //targetToken으로 fcm 푸시알람 전송(프론트에서 전송해줌)
        String message = makeMessage(targetToken, title, body);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Response response = addAccessTokenToHeader(client, requestBody);
        System.out.println(response.body().string());
    }
    @NotNull
    private Response addAccessTokenToHeader(OkHttpClient client, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer" +getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
        Response response = client.newCall(request)
                .execute();
        return response;
    }

    private String makeMessage(String targetToken,String title,String body) throws JsonProcessingException{
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessageDto);
    }
    @PostConstruct
    private String getAccessToken() throws IOException{
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
                .createScoped(List.of(GOOGLE_CLOUD_URL));
        googleCredentials.refreshIfExpired();  //accessToken 생성
        System.out.println(googleCredentials.getAccessToken().getTokenValue());
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

//    @PostConstruct
//    public void init(){
//        try{
//            FileInputStream serviceAccount =
//                    new FileInputStream("src/main/resources/fireBaseAccountKey.json");
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }


