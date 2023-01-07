package net.sinzak.server.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class JoinDto {

    @ApiModelProperty(example = "토큰 ID(애플 로그인만 필수)",notes = "선택값")
    private String tokenId;
    @ApiModelProperty(example = "이름")
    private String name;
    @ApiModelProperty(example = "이메일")
    private String email;
    @ApiModelProperty(example = "kakao")
    private String origin;
    @ApiModelProperty(example = "선호 카테고리",notes = "{\"category_like\" : \"orient, painting\" 처럼 콤마로 구분해서 보내주세요")
    private String category_like;
    @ApiModelProperty(example = "닉네임")
    private String nickName;
    @ApiModelProperty(example = "선택 약관")
    boolean term;

}
