package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class JoinDto {

    @ApiModelProperty(example = "선호 카테고리",notes = "{\"category_like\" : \"orient, painting\" 처럼 영어 및 콤마로 구분해서 보내주세요")
    private String category_like;
    @ApiModelProperty(example = "닉네임")
    private String nickName;
    @ApiModelProperty(example = "선택 약관")
    boolean term;
}
