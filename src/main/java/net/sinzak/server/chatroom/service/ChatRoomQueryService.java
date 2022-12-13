package net.sinzak.server.chatroom.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.ChatRoomDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public List<ChatRoomDto> getChatRooms(SessionUser user){
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomBySessionUserEmail(user.getEmail());
        List<ChatRoomDto> chatRoomDtos =new ArrayList<>();
        for(UserChatRoom userChatRoom: userChatRooms){
            ChatRoomDto chatRoomDto = getChatRoomDto(userChatRoom);
            chatRoomDtos.add(chatRoomDto);
        }
        return chatRoomDtos;
    }
    private ChatRoomDto getChatRoomDto(UserChatRoom userChatRoom) {
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .image(userChatRoom.getImage())
                .roomName(userChatRoom.getRoomName())
                .build();
        return chatRoomDto;
    }
}
