package net.sinzak.server.chatroom.service;

//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.WriteResult;
//import com.google.firebase.cloud.FirestoreClient;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.chatroom.domain.MessageType;
import net.sinzak.server.chatroom.dto.request.ChatMessageDto;
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
        chatMessage.setRoomId(UUID.randomUUID().toString());
        chatMessage.setMessage(chatMessageDto.getMessage());
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setReceiver("유성욱");
        chatMessage.setType(MessageType.TEXT);
        return chatMessage;
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
