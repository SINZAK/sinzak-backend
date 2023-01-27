package net.sinzak.server.chatroom.dto.respond;

import lombok.*;
import net.sinzak.server.common.PostType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCreatedChatRoomDto {
    private String roomUuid;
    private boolean newChatRoom;

}
