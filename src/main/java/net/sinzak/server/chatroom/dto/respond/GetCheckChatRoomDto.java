package net.sinzak.server.chatroom.dto.respond;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCheckChatRoomDto {
    private String roomUuid;
    private boolean exist;
}
