package net.sinzak.server.firebase;


import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.user.domain.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Async
@Slf4j
@Transactional
public class FireBaseService {

    public void sendIndividualNotification(User user, String title, String body, String route){
        if(user.getFcm() ==null || user.getFcm().equals("")){ //로그아웃 한 상태라면
            return;
        }
        Notification notification = new Notification(title,body);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(user.getFcm())
                .putData("route",route)
                .build();
        try{
            FirebaseMessaging.getInstance().sendAsync(message);
            log.info("알림 전송에 성공하였습니다.");
        }
        catch (Exception e){
            log.error("알림 전송에 실패하였습니다. {}", e.getMessage());
        }
    }
    public void sendToAllNotification(List<String> tokenList,String title, String body, String route){
        List<Message> messages = tokenList.stream().map(token->Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(new Notification(title,body))
                .setToken(token)
                .build()).collect(Collectors.toList());

        BatchResponse response;
        try {

            // 알림 발송
            response = FirebaseMessaging.getInstance().sendAll(messages);

            // 요청에 대한 응답 처리
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error("List of tokens are not valid FCM token : " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("cannot send to memberList push message. error info : {}", e.getMessage());
        }

    }
}
