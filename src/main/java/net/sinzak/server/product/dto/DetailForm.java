package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.user.domain.embed.Size;

import javax.persistence.*;

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
    @ApiModelProperty(example = "작가 대학 인증여부")
    private boolean cert_uni;
    @ApiModelProperty(example = "작가 인플루언서 여부")
    private boolean cert_celeb;
    @ApiModelProperty(example = "작가 팔로워 수")
    private String followerNum;
    @ApiModelProperty(example = "이미 팔로잉 중인지 여부")
    private boolean isFollowing;


    @ApiModelProperty(example = "작품 대표 사진") /** 작품 조회 **/
    private String photo;
    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(example = "작품 가격")
    private int price;
    @ApiModelProperty(example = "카테고리")
    private String category; //분류
    @ApiModelProperty(example = "작품 게시일자")
    private String date;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(example = "boolean",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "좋아요 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean isLike;
    @ApiModelProperty(example = "좋아요 수",notes = "true -> 누른 사람")
    private int likesCnt;
    @ApiModelProperty(example = "찜 누른 사람인지 여부",notes = "true -> 누른 사람")
    private boolean isWish;
    @ApiModelProperty(example = "작품 조회수")
    private int views;
    @ApiModelProperty(example = "작품 찜 수")
    private int wishCnt;
    @ApiModelProperty(example = "작품 채팅 수")
    private int chatCnt;
    @ApiModelProperty(example = "가로 길이")
    private int width;
    @ApiModelProperty(example = "세로 길이")
    private int vertical;
    @ApiModelProperty(example = "높이", notes = "nullable")
    private int height;
    @ApiModelProperty(example = "판매완료 여부",notes = "true -> 판매완료")
    private boolean complete;


    public void setLikeAndWish(boolean like, boolean wish, boolean isFollowing) {
        isLike = like;
        isWish = wish;
        this.isFollowing = isFollowing;
    }



}
