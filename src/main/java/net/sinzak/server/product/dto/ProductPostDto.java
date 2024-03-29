package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

@Getter
public class ProductPostDto {

    @ApiModelProperty(example = "작품 판매글 제목")
    @NotEmpty
    private String title;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(example = "작품 카테고리")
    private String category;
    @ApiModelProperty(value ="작품 가격",example ="30000",notes = "int 값 이상 안들어오게 프론트라인 체크 필수")
    @Max(value = Integer.MAX_VALUE, message = "가격 값 초과 Integer Max Value 미만으로 전송바람")
    private int price;
    @ApiModelProperty(value = "작품 모집글 가격 제안 여부", example = "false" ,notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;

    @ApiModelProperty(value = "가로 사이즈 1m50cm -> 150",example ="150")
    @Max(value = Integer.MAX_VALUE, message = "사이즈 값 초과 Integer Max Value 미만으로 전송바람")
    private int width;
    @ApiModelProperty(value = "세로 사이즈",example ="150")
    @Max(value = Integer.MAX_VALUE, message = "사이즈 값 초과 Integer Max Value 미만으로 전송바람")
    private int vertical;
    @ApiModelProperty(value = "높이 사이즈",example ="50")
    @Max(value = Integer.MAX_VALUE, message = "사이즈 값 초과 Integer Max Value 미만으로 전송바람")
    private int height;
}
