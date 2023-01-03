package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class EmailDto {

    @ApiModelProperty(example = "조회 하고 싶은 이메일")
    private String email;
}
