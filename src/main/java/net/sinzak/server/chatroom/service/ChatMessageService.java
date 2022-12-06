package net.sinzak.server.chatroom.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import net.sinzak.server.chatroom.domain.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    public static final String COLLECTION_NAME="chatMessage";

    public String createChatMessage(ChatMessage chatMessage) throws Exception{
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> apiFuture =
                firestore.collection(COLLECTION_NAME).document(chatMessage.getId().toString()).set(chatMessage);
        return apiFuture.get().getUpdateTime().toString();
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
