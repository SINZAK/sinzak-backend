package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class EditDto {

    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(value ="가격",example ="30000",notes = "int 값 이상 안들어오게 프론트라인 체크 필수")
    private int price;
    @ApiModelProperty(value = "가격 제안 여부", example = "false" ,notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;

    public EditDto(String title, String content, int price, boolean suggest) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.suggest = suggest;
    }
}
