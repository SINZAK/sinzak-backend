package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class GetChatRoomsDto {
    private String roomUuid;
    private String roomName;
    private String image;
    private String univ;
    private String latestMessage;
    private LocalDateTime latestMessageTime;
}
