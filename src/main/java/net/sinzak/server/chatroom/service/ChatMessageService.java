package net.sinzak.server.chatroom.service;

//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.WriteResult;
//import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.chatroom.domain.MessageType;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.chatroom.dto.request.ChatMessageDto;
import net.sinzak.server.chatroom.dto.request.ChatRoomUuidDto;
import net.sinzak.server.chatroom.dto.respond.GetChatMessageDto;
import net.sinzak.server.chatroom.repository.ChatMessageRepository;
import net.sinzak.server.chatroom.repository.ChatRoomRepository;
import net.sinzak.server.chatroom.repository.UserChatRoomRepository;
import net.sinzak.server.common.error.ChatRoomNotFoundException;
import net.sinzak.server.firebase.FireBaseService;
import net.sinzak.server.user.domain.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final SimpMessagingTemplate template;
    private final ChatMessageRepository chatMessageRepository;
    private final FireBaseService fireBaseService;
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
                        .orElseThrow(()->new ChatRoomNotFoundException());
        if(findChatRoom.isBlocked() || findChatRoom.getParticipantsNumber()<2){
            //차단 되어있거나 한 명이 나간 상태라면 보내지 않음
            return;
        }
        User opponentUser = addChatMessageToChatRoom(message, findChatRoom);
        fireBaseService.sendIndividualNotification(opponentUser,"채팅 알림",message.getSenderName()+": "+message.getMessage(),message.getRoomId()); // 메시지 이동 루트를 보내줌
        GetChatMessageDto getChatMessageDto = makeMessageDto(message);
        template.convertAndSend("/sub/chat/rooms/"+message.getRoomId(),getChatMessageDto);
    }
    private GetChatMessageDto makeMessageDto(ChatMessageDto message) {
        GetChatMessageDto getChatMessageDto = GetChatMessageDto.builder()
                .message(message.getMessage())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .sendAt(LocalDateTime.now())
                .messageType(message.getMessageType().name())
                .build();
        return getChatMessageDto;
    }

    private User addChatMessageToChatRoom(ChatMessageDto message, ChatRoom findChatRoom) {
        ChatMessage newChatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .type(message.getMessageType())
                .senderName(message.getSenderName())
                .senderId(message.getSenderId())
                .build();
//        chatMessageRepository.save(newChatMessage);
        User opponentUser = findChatRoom.addChatMessage(newChatMessage);
        return opponentUser;
    }

    @Transactional
    public void leaveChatRoom(User user, String roomUuid){
        log.info(roomUuid+":uuid");
        ChatRoom findChatroom = chatRoomRepository.findByRoomUuidFetchUserChatRoom(roomUuid)
                .orElseThrow(()->new ChatRoomNotFoundException());
        UserChatRoom userChatRoom = findChatroom.leaveChatRoom(user.getId());
        if(userChatRoom == null){
            throw new ChatRoomNotFoundException();
        }
        deleteChatRoom(findChatroom);
        addLeaveChatMessageToChatRoom(user, findChatroom);
        GetChatMessageDto getChatMessageDto = makeLeaveChatMessageDto(user);
        template.convertAndSend("/sub/chat/rooms/"+roomUuid,getChatMessageDto);
        return;
    }


    private GetChatMessageDto makeLeaveChatMessageDto(User user) {
        GetChatMessageDto getChatMessageDto = GetChatMessageDto.builder()
                .message("님이 채팅방을 나가셨습니다")
                .senderName(user.getName())
                .sendAt(LocalDateTime.now())
                .messageType(MessageType.LEAVE.name())
                .build();
        return getChatMessageDto;
    }

    private void deleteChatRoom(ChatRoom findChatroom) {
        if(findChatroom.getParticipantsNumber()==0){
            for(UserChatRoom userChatRoom : findChatroom.getUserChatRooms()){
                userChatRoomRepository.delete(userChatRoom);
            }
            chatRoomRepository.delete(findChatroom);
        }
    }


    private void addLeaveChatMessageToChatRoom(User user, ChatRoom findChatroom) {
        ChatMessage leaveChatMessage = ChatMessage.builder()
                .message(user.getName()+"님이 채팅방을 나가셨습니다")
                .senderName(user.getName())
                .senderId(user.getId())
                .type(MessageType.LEAVE)
                .build();
//        chatMessageRepository.save(leaveChatMessage);
        findChatroom.addChatMessage(leaveChatMessage);
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
