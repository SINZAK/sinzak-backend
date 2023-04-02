package net.sinzak.server.cert.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class MailDto {
    @ApiModelProperty(value = "이메일 주소")
    private String univ_email;
    @ApiModelProperty(value = "인증코드", notes = "receive 에서 사용, send에선 생략")
    private int code;
    @ApiModelProperty(value = "선택한 대학교", example = "홍익대학교")
    private String univName;

    public MailDto(String univ_email, String univName) {
        this.univ_email = univ_email;
        this.univName = univName;
    }
}
