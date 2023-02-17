package net.sinzak.server.config.auth.jwt;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;
    private boolean joined = true;

    public void setIsJoined(boolean joined) {
        this.joined = joined;
    }
}