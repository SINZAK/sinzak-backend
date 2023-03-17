package net.sinzak.server.user.dto.respond;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class UserDto {
    private Long userId;
    private boolean myProfile;
    private String name;
    private String introduction;
    private String followingNumber;
    private String followerNumber;
    private String imageUrl;
    private String univ;
    private String categoryLike;

    private boolean cert_uni;
    private boolean cert_celeb;
    private boolean isFollow;
}
