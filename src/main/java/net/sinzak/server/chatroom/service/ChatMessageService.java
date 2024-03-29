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
                        .orElseThrow(ChatRoomNotFoundException::new);
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
        return GetChatMessageDto.builder()
                .message(message.getMessage())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .sendAt(LocalDateTime.now())
                .messageType(message.getMessageType().name())
                .build();
    }

    private User addChatMessageToChatRoom(ChatMessageDto message, ChatRoom findChatRoom) {
        ChatMessage newChatMessage = ChatMessage.builder()
                .message(message.getMessage())
                .type(message.getMessageType())
                .senderName(message.getSenderName())
                .senderId(message.getSenderId())
                .build();
//        chatMessageRepository.save(newChatMessage);
        return findChatRoom.addChatMessage(newChatMessage);
    }

    @Transactional
    public void leaveChatRoom(ChatMessageDto chatMessageDto){
        log.info(chatMessageDto.getRoomId()+":uuid");
        ChatRoom findChatroom = chatRoomRepository.findByRoomUuidFetchUserChatRoom(chatMessageDto.getRoomId())
                .orElseThrow(ChatRoomNotFoundException::new);
        UserChatRoom userChatRoom = findChatroom.leaveChatRoom(chatMessageDto.getSenderId());
        if(userChatRoom == null){
            throw new ChatRoomNotFoundException();
        }
        deleteChatRoom(findChatroom);
        addLeaveChatMessageToChatRoom(chatMessageDto, findChatroom);
        GetChatMessageDto getChatMessageDto = makeLeaveChatMessageDto(chatMessageDto);
        template.convertAndSend("/sub/chat/rooms/"+chatMessageDto.getRoomId(),getChatMessageDto);
    }


    private GetChatMessageDto makeLeaveChatMessageDto(ChatMessageDto chatMessageDto) {
        return GetChatMessageDto.builder()
                .message("님이 채팅방을 나가셨습니다")
                .senderName(chatMessageDto.getSenderName())
                .sendAt(LocalDateTime.now())
                .messageType(MessageType.LEAVE.name())
                .build();
    }

    private void deleteChatRoom(ChatRoom findChatroom) {
        if(findChatroom.getParticipantsNumber()==0){
            userChatRoomRepository.deleteAll(findChatroom.getUserChatRooms());
            chatRoomRepository.delete(findChatroom);
        }
    }
    private void addLeaveChatMessageToChatRoom(ChatMessageDto chatMessageDto, ChatRoom findChatroom) {
        ChatMessage leaveChatMessage = ChatMessage.builder()
                .message(chatMessageDto.getSenderName()+"님이 채팅방을 나가셨습니다")
                .senderName(chatMessageDto.getSenderName())
                .senderId(chatMessageDto.getSenderId())
                .type(MessageType.LEAVE)
                .build();
//        chatMessageRepository.save(leaveChatMessage);
        findChatroom.addChatMessage(leaveChatMessage);
    }

}
