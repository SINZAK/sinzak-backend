package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class ShowForm {
    @ApiModelProperty(example = "작품 ID")
    private Long id;
    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(example = "작가명")
    private String author;
    @ApiModelProperty(value = "작품 가격",example ="1")
    private int price;
    @ApiModelProperty(value = "좋아요 수",example ="1")
    private int likes;
    @ApiModelProperty(value = "작품 대표 사진",example ="1")
    private String photo;
    @ApiModelProperty(example = "작품 게시일자")
    private String date;
    @ApiModelProperty(example = "boolean",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "좋아요 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean isLike;

    public ShowForm(Long id, String title, String content, String author, int price, int likes, String photo, String date, boolean suggest, boolean isLike) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.price = price;
        this.likes = likes;
        this.photo = photo;
        this.date = date;
        this.suggest = suggest;
        this.isLike = isLike;
    }
}
