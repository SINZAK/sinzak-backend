package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.user.domain.embed.Size;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
public class DetailForm {

    @ApiModelProperty(example = "작품 ID")
    private Long id;

    @ApiModelProperty(example = "작가명")   /** 작가 조회 **/
    private String author;
    @ApiModelProperty(example = "작가 프사")
    private String author_picture;
    @ApiModelProperty(example = "작가 대학명")
    private String univ;
    @ApiModelProperty(value = "작가 대학 인증여부", example = "true")
    private boolean cert_uni;
    @ApiModelProperty(value = "작가 인플루언서 여부", example = "false")
    private boolean cert_celeb;
    @ApiModelProperty(example = "작가 팔로워 수")
    private String followerNum;
    @ApiModelProperty(value = "이미 팔로잉 중인지 여부" , example = "true")
    private boolean isFollowing;


    @ApiModelProperty(value = "[이미지[0], 이미지배열[1], 이미지배열[2] ...]")  /** 작품 조회 **/
    private List<String> images;
    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(value = "작품 가격", example = "30000")
    private int price;
    @ApiModelProperty(example = "동양화")
    private String category;
    @ApiModelProperty(example = "2023-01-02T18:26:27", notes = "작품 글 올린 날짜")
    private String date;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(example = "false",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "true",notes = "true -> 누른 사람")
    private boolean isLike;
    @ApiModelProperty(example = "3")
    private int likesCnt;
    @ApiModelProperty(example = "false",notes = "true -> 누른 사람")
    private boolean isWish;
    @ApiModelProperty(example = "102")
    private int views;
    @ApiModelProperty(example = "1")
    private int wishCnt;
    @ApiModelProperty(example = "2")
    private int chatCnt;
    @ApiModelProperty(example = "150")
    private int width;
    @ApiModelProperty(example = "70")
    private int vertical;
    @ApiModelProperty(example = "30", notes = "nullable")
    private int height;
    @ApiModelProperty(example = "false",notes = "true -> 거래중")
    private boolean trading;
    @ApiModelProperty(example = "false",notes = "true -> 판매완료")
    private boolean complete;


    public void setLikeAndWish(boolean like, boolean wish, boolean isFollowing) {
        isLike = like;
        isWish = wish;
        this.isFollowing = isFollowing;
    }
}
