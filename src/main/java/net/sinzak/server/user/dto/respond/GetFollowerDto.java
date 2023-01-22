package net.sinzak.server.user.dto.respond;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetFollowerDto {
    private Long userId;

    private String name;
    private String picture;
    private boolean ifFollow;
}
