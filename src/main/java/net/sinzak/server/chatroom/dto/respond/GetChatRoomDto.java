package net.sinzak.server.chatroom.dto.respond;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.common.PostType;

@Getter
@Builder
public class GetChatRoomDto {
    private String roomName;
    private Long postId;
    private String postName;
    private int price;
    private String thumbnail;
    private boolean complete;
    private boolean suggest;
    private Long postUserId;
    private PostType postType;
    private Long opponentUserId;

    public void setPostId(Long postId) {
        this.postId = postId;
    }

}
