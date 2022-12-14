package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class ActionForm {
    @ApiModelProperty(example = "true", notes = "true면 좋아요/찜 false면 취소")
    private boolean mode;  //true면 추가 false면 삭제
    @ApiModelProperty(example = "52", notes = "해당 대상 id")
    private Long id;
}