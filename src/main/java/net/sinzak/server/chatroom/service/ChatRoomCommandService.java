package net.sinzak.server.chatroom.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    @Transactional
    public JSONObject createUserChatRoom(User user,User inviteUser){
        UUID uuid = findIfChatRoomExist(user,inviteUser);
        if(uuid!=null){
            return makeChatRoomJson(uuid);
        }
        ChatRoom chatRoom = makeChatRoom(user, inviteUser);
        return makeChatRoomJson(chatRoom.getUuid());
    }
    private JSONObject makeChatRoomJson(UUID uuid) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);
        jsonObject.put("UUID",uuid);
        return jsonObject;
    }
    private UUID findIfChatRoomExist(User user, User inviteUser) {
        List<UserChatRoom> myUserChatRoom =
                userChatRoomRepository.findUserChatRoomBySessionUserEmail(user.getEmail());
        for(UserChatRoom userChatRoom : myUserChatRoom){
            if(userChatRoom.getRoomName().equals(inviteUser.getName())){
                return userChatRoom.getChatRoom().getUuid();
            }
        }
        return null;
    }
    private ChatRoom makeChatRoom(User user, User inviteUser) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.addUserChatRoom(user, inviteUser);
        chatRoom.addUserChatRoom(inviteUser, user);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}
