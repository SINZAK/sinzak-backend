package net.sinzak.server.chatroom.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String message;
    private String sender;
    private Long senderId;
}
