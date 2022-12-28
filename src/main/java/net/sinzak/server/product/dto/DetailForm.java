package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailForm {

    @ApiModelProperty(example = "작품 ID")
    private Long id;
    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(example = "작가명")
    private String author;
    @ApiModelProperty(example = "작품 가격")
    private int price;
    @ApiModelProperty(example = "작품 대표 사진")
    private String photo;
    @ApiModelProperty(example = "작품 게시일자")
    private String date;
    @ApiModelProperty(example = "boolean",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "좋아요 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean isLike;
    @ApiModelProperty(example = "좋아요 수",notes = "true -> 누른 사람")
    private int likesCnt;
    @ApiModelProperty(example = "찜 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean isWish;
    @ApiModelProperty(example = "판매완료 여부",notes = "true -> 판매완료")
    private boolean complete;


    public void setLikeAndWish(boolean like, boolean wish) {
        isLike = like;
        isWish = wish;
    }

}
