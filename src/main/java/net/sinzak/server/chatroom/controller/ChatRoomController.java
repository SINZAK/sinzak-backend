package net.sinzak.server.chatroom.controller;


import lombok.RequiredArgsConstructor;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.service.ChatRoomQueryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomQueryService chatRoomQueryService;

//    @Transactional
//    public JSONObject createChatRoom(){
//        ChatRoom chatRoom = new ChatRoom();
//    }



}
