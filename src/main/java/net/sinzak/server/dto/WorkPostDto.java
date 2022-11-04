package net.sinzak.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class WorkPostDto {
    @ApiModelProperty(example = "외주 모집글 제목")
    private String title;    // 프로젝트명
    @ApiModelProperty(example = "외주 모집글 내용")
    private String content; // 프로젝트 내용
    @ApiModelProperty(example = "외주 카테고리")
    private String category;
    @ApiModelProperty(example = "페이",notes = "int 값 이상 안들어오게 프론트라인 체크 필수")
    private int pay;
    @ApiModelProperty(example = "외주 모집글 가격 제안 여부 true/false",notes = "true -> 체크 한 사람(제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "외주 분야")
    private String field;
    @ApiModelProperty(example = "사진")
    private String photo;
    @ApiModelProperty(example = "true 시 고용자, false 시 피고용자")
    private boolean employment;

    public WorkPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public WorkPostDto() {
    }
}