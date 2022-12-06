package net.sinzak.server.chatroom.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public JSONObject createChatRoom(){
        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);
        return PropertyUtil.response(true);
    }

}
