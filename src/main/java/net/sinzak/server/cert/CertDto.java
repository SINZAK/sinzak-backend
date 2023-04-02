package net.sinzak.server.cert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CertDto {
    @ApiModelProperty(value = "유저 아이디")
    private Long userId;
    @ApiModelProperty(value = "인증 상태(인증작가)", notes = "YET = 미인증, PROCESS = 처리중, COMPLETE = 처리완료")
    private Status status;
    @ApiModelProperty(value = "대학 인증 여부!")
    private boolean cert_uni;
    @ApiModelProperty(value = "인증작가 인증 여부!")
    private boolean cert_celeb;

    @Builder
    public CertDto(Long userId, Status status, boolean cert_uni, boolean cert_celeb) {
        this.userId = userId;
        this.status = status;
        this.cert_uni = cert_uni;
        this.cert_celeb = cert_celeb;
    }
}
