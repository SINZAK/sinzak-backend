package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class JoinDto {

    @ApiModelProperty(example = "선호 카테고리",notes = "{\"category_like\" : \"orient, painting\" 처럼 영어 및 콤마로 구분해서 보내주세요")
    private String category_like;
    @ApiModelProperty(example = "닉네임")
    @Pattern(regexp = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{2,16}$",
            message = "2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성\n특이사항 : 한글 초성 및 모음은 허가하지 않는다.")
    @NotNull(message = "닉네임을 설정해주세요")
    private String nickName;
    @ApiModelProperty(example = "선택 약관")
    boolean term;
}
