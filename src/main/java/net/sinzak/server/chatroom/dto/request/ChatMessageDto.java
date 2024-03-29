package net.sinzak.server.chatroom.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sinzak.server.chatroom.domain.MessageType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    @Setter
    private String message;
    private String senderName;
    private String roomId;
    private Long senderId;
    private MessageType messageType;
}
