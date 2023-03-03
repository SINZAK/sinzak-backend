package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class OauthDto {
    @ApiModelProperty(example = "액세스 토큰")
    private String accessToken;
    @ApiModelProperty(example = "구글, 애플전용")
    private String idToken;
    @ApiModelProperty(example = "소셜 로그인 origin 소문자")
    private String origin;
}
