package net.sinzak.server.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FcmDto {
    private Long userId;
    private String fcmToken;
}
