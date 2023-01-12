package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.common.PostType;

@Builder
@Getter
public class GetCreatedChatRoomDto {
    private String roomUuid;
}
