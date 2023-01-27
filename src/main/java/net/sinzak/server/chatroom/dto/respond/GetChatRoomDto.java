package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatRoomDto {
    private String roomName;
    private Long productId;
    private String productName;
    private int price;
    private String thumbnail;
    private boolean complete;
    private boolean suggest;
}
