package net.sinzak.server.chatroom.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.sinzak.server.chatroom.domain.MessageType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String message;
    private String sender;
    private String roomId;
    private Long senderId;
    private MessageType messageType;
}
