package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class OauthDto {
    @ApiModelProperty(example = "액세스 토큰")
    private String accessToken;
    @ApiModelProperty(example = "소셜 로그인 origin")
    private String origin;
}
