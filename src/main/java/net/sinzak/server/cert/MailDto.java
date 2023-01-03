package net.sinzak.server.cert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class MailDto {
    @ApiModelProperty(value = "이메일 주소")
    private String address;
    @ApiModelProperty(value = "인증코드", notes = "receive 에서 사용, send에선 생략")
    private String code;
    @ApiModelProperty(value = "선택한 대학교", example = "홍익대학교")
    private String univ;
}
