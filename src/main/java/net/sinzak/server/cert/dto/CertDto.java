package net.sinzak.server.cert.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sinzak.server.cert.Status;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
public class CertDto {
    @ApiModelProperty(value = "유저 아이디")
    private Long userId;

    @Setter
    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty(value = "인증 상태(학생증)", notes = "YET = 미인증, PROCESS = 처리중, COMPLETE = 처리완료")
    private Status univcardStatus = Status.YET;

    @Setter
    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty(value = "인증 상태(인증작가)", notes = "YET = 미인증, PROCESS = 처리중, COMPLETE = 처리완료")
    private Status celebStatus = Status.YET;

    @ApiModelProperty(value = "대학 인증 여부!")
    private boolean cert_uni;

    @ApiModelProperty(value = "인증작가 인증 여부!")
    private boolean cert_author;

    @Builder
    public CertDto(Long userId, boolean cert_uni, boolean cert_author) {
        this.userId = userId;
        this.cert_uni = cert_uni;
        this.cert_author = cert_author;
    }
}
