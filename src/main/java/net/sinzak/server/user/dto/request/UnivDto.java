package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class UnivDto {

    @ApiModelProperty(example = "대학명")
    private String univ;
    @ApiModelProperty(example = "대학메일")
    private String univ_email;
}
