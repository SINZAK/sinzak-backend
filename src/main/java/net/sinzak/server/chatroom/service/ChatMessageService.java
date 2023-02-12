package net.sinzak.server.chatroom.service;

//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.WriteResult;
//import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.MessageType;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.request.ChatMessageDto;
import net.sinzak.server.chatroom.dto.request.ChatRoomUuidDto;
import net.sinzak.server.chatroom.dto.respond.GetChatMessageDto;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.user.domain.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final SimpMessagingTemplate template;

    public static final String COLLECTION_NAME="chatMessage";

//    public JSONObject createChatMessage(ChatMessageDto chatMessageDto) throws Exception{
//        ChatMessage chatMessage = makeTestMessage(chatMessageDto);
//        Firestore firestore = FirestoreClient.getFirestore();
//        ApiFuture<WriteResult> apiFuture =
//                firestore.collection(COLLECTION_NAME).document(chatMessage.getId()).set(chatMessage);
//        return PropertyUtil.response(true);
//    }

    @Transactional
    public void sendChatMessage(ChatMessageDto message){
        ChatRoom findChatRoom =
                chatRoomRepository
                        .findByRoomUuidFetchChatMessage(message.getRoomId())
                        .orElseThrow(()->new InstanceNotFoundException("존재하지 않는 채팅방입니다."));
        ChatMessage newChatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .senderName(message.getSenderName())
                .senderId(message.getSenderId())
                .build();
        findChatRoom.addChatMessage(newChatMessage);
        GetChatMessageDto getChatMessageDto = GetChatMessageDto.builder()
                .message(message.getMessage())
                .messageId(newChatMessage.getId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .sendAt(newChatMessage.getCreatedDate())
                .messageType(MessageType.TEXT.name())
                .build();
        template.convertAndSend("/sub/chat/rooms/"+message.getRoomId(),getChatMessageDto);
    }
    @Transactional
    public void leaveChatRoom(User user, ChatRoomUuidDto chatRoomUuidDto){
        ChatRoom findChatroom = chatRoomRepository.findByRoomUuidFetchUserChatRoom(chatRoomUuidDto.getRoomId())
                .orElseThrow(()->new InstanceNotFoundException("존재하지 않는 채팅방입니다."));
        UserChatRoom userChatRoom = findChatroom.leaveChatRoom(user.getEmail());
        if(userChatRoom ==null){
            throw new InstanceNotFoundException("존재하지 않는 채팅방입니다.");
        }
        userChatRoomRepository.delete(userChatRoom);
        if(findChatroom.getParticipantsNumber()==0){
            chatRoomRepository.delete(findChatroom);
        }
        GetChatMessageDto getChatMessageDto = GetChatMessageDto.builder()
                .message("님이 채팅방을 나가셨습니다")
                .senderName(user.getName())
                .sendAt(LocalDateTime.now())
                .messageType(MessageType.LEAVE.name())
                .build();
        template.convertAndSend("/sub/chat/rooms/"+chatRoomUuidDto,getChatMessageDto);
        return;
    }



//    public ChatMessage getChatMessage(String id) throws Exception{
//        Firestore firestore = FirestoreClient.getFirestore();
//        DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document(id);
//        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
//        DocumentSnapshot documentSnapshot = apiFuture.get();
//        if(documentSnapshot.exists()){
//            return documentSnapshot.toObject(ChatMessage.class);
//        }
//        return null;
//    }
}
