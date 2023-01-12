package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetChatRoomsDto {
    private String roomUuid;
    private String roomName;
    private String image;
    private String univ;
}
