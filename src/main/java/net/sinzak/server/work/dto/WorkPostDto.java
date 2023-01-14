package net.sinzak.server.work.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
public class WorkPostDto {
    @ApiModelProperty(example = "외주 모집글 제목")
    private String title;
    @ApiModelProperty(example = "외주 모집글 내용")
    private String content;
    @ApiModelProperty(example = "외주 카테고리")
    private String category;
    @ApiModelProperty(value ="페이",example = "100000",notes = "int 값 이상 안들어오게 프론트라인 체크 필수")
    private int price;
    @ApiModelProperty(value = "외주 모집글 가격 제안 여부 true/false", example = "true", notes = "true -> 체크 한 사람(제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(value = "true : 고용자, false : 피고용자", example = "true 시 고용자, false 시 피고용자")
    private boolean employment;


    public WorkPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

}