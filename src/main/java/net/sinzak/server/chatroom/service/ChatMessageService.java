package net.sinzak.server.chatroom.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import net.sinzak.server.chatroom.domain.ChatMessage;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    public static final String COLLECTION_NAME="chatMessage";

    public JSONObject createChatMessage(String message) throws Exception{
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId("1");
        chatMessage.setMessage(message);
        chatMessage.setSender("송인서");
        chatMessage.setReceiver("유성욱");
        chatMessage.setType("test");
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> apiFuture =
                firestore.collection(COLLECTION_NAME).document(chatMessage.getId()).set(chatMessage);
        return PropertyUtil.response(true);
    }

    public ChatMessage getChatMessage(String id) throws Exception{
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot documentSnapshot = apiFuture.get();
        if(documentSnapshot.exists()){
            return documentSnapshot.toObject(ChatMessage.class);
        }
        return null;
    }
}
