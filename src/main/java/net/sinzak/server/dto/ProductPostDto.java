package net.sinzak.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //후에 지워야됨
public class ProductPostDto {

    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;    // 프로젝트명
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content; // 프로젝트 내용
    @ApiModelProperty(example = "작품 카테고리")
    private String category;
    @ApiModelProperty(example = "작품 가격",notes = "int 값 이상 안들어오게 프론트라인 체크 필수")
    private int price;
    @ApiModelProperty(example = "작품 모집글 가격 제안 여부 true/false",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "작품 분야")
    private String field;
    @ApiModelProperty(example = "작품 사진")
    private String photo;

    @ApiModelProperty(example = "가로 사이즈")
    private int width;
    @ApiModelProperty(example = "세로 사이즈")
    private int vertical;
    @ApiModelProperty(example = "높이 사이즈")
    private int height;
}
