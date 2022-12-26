package net.sinzak.server.chatroom.service;

import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatRoomServiceTest {
    @Autowired
    private ChatRoomCommandService chatRoomCommandService;
    @Autowired
    private ChatRoomQueryService chatRoomQueryService;
    @Autowired
    private UserRepository userRepository;
    User user = new User("송인서@지메일","송인서","그림2");
    User user1 = new User("유성욱@지메일","유성욱","그림1");
    JSONObject originalJsonObject =new JSONObject();
    @BeforeEach
    public void makeChatRoom(){
        userRepository.save(user);
        userRepository.save(user1);
        originalJsonObject = chatRoomCommandService.createUserChatRoom(user,user1);
        System.out.println("originalJson ="+ originalJsonObject);
    }
    @Test
    public void createDuplicateUserChatRoomTest(){
        JSONObject newJsonObject = chatRoomCommandService.createUserChatRoom(user1,user);
        assertThat(getUuid(newJsonObject)).isEqualTo(getUuid(originalJsonObject));
        newJsonObject = chatRoomCommandService.createUserChatRoom(user,user1);
        assertThat(getUuid(newJsonObject)).isEqualTo(getUuid(originalJsonObject));
    }

    public UUID getUuid(JSONObject jsonObject){
        return (UUID)jsonObject.get("UUID");
    }

}