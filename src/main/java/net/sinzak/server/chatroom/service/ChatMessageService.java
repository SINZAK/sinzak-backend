package net.sinzak.server.chatroom.service;


import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.dto.ChatMessageDto;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatMessageService {

    public static final String COLLECTION_NAME="chatMessage";

//    public JSONObject createChatMessage(ChatMessageDto chatMessageDto) throws Exception{
//        ChatMessage chatMessage = makeTestMessage(chatMessageDto);
//        Firestore firestore = FirestoreClient.getFirestore();
//        ApiFuture<WriteResult> apiFuture =
//                firestore.collection(COLLECTION_NAME).document(chatMessage.getId()).set(chatMessage);
//        return PropertyUtil.response(true);
//    }

    private ChatMessage makeTestMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setMessage(chatMessageDto.getMessage());
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setReceiver("유성욱");
        chatMessage.setType("test");
        return chatMessage;
    }
}
