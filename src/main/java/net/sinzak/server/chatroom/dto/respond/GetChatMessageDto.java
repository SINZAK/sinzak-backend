package net.sinzak.server.chatroom.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatMessageDto {
    private Long senderId;
    private Long messageId;
    private String message;
    private String senderName;
    private LocalDateTime sendAt;
    private String messageType;
}
