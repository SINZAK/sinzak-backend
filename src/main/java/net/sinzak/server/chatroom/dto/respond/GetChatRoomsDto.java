package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetChatRoomsDto {
    private String roomName;
    private String lastMessage;
    private String image;
    private String univ;
    private int unReadMessage;
}
