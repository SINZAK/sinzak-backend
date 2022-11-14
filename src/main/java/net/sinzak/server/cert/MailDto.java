package net.sinzak.server.cert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class MailDto {
    @ApiModelProperty("이메일 주소")
    private String address;
    @ApiModelProperty(value = "인증코드", notes = "receive 에서 사용")
    private String code;
}
