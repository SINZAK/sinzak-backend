package net.sinzak.server.chatroom.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomUuidDto {
    private String roomId;
}
