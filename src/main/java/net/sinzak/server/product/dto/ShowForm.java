package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ShowForm implements Serializable {
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
    @ApiModelProperty(example = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/7aea0508-4b3b-4b52-a98e-8f699b5b4bc7.jpg")
    private String thumbnail;
    @ApiModelProperty(example = "2023-01-02T18:26:27", notes = "작품 글 올린 날짜")
    private String date;
    @ApiModelProperty(example = "boolean",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "좋아요 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean like;
    @ApiModelProperty(value = "좋아요 수",example = "1",notes = "true -> 누른 사람")
    private int likesCnt;
    @ApiModelProperty(example = "판매완료 여부",notes = "true -> 판매완료")
    private boolean complete;
    @ApiModelProperty(hidden = true)
    private int popularity;

    public ShowForm() {}

    @Builder
    public ShowForm(Long id, String title, String content, String author, int price, String thumbnail, String date, boolean suggest, boolean like, int likesCnt, boolean complete, int popularity) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.price = price;
        this.thumbnail = thumbnail;
        this.date = date;
        this.suggest = suggest;
        this.like = like;
        this.likesCnt = likesCnt;
        this.complete = complete;
        this.popularity = popularity;
    }
}
