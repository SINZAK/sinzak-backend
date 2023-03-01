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
    private String origin = "";
    public void setIsJoined(boolean joined) {
        this.joined = joined;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}