package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailDto {
    @ApiModelProperty(example = "이메일 or 토큰id(애플로그인)")
    private String email;

    public EmailDto(String email) {
        this.email = email;
    }
}
