package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import java.util.List;

@Getter
public class DetailForm {

    @ApiModelProperty(example = "작품 ID")
    private Long id;
    @ApiModelProperty(example = "유저 ID")
    private Long userId;
    @ApiModelProperty(example = "작가명")
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


    @ApiModelProperty(value = "[이미지[0], 이미지배열[1], 이미지배열[2] ...]")
    private List<String> images;
    @ApiModelProperty(example = "작품 판매글 제목")
    private String title;
    @ApiModelProperty(example = "동양화")
    private String category;
    @ApiModelProperty(example = "2023-01-02T18:26:27", notes = "작품 글 올린 날짜")
    private String date;
    @ApiModelProperty(example = "작품 판매글 내용")
    private String content;
    @ApiModelProperty(value = "작품(가격), 외주(페이)", example = "30000")
    private int price;
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
    @ApiModelProperty(example = "false",notes = "true -> 내가 올린 글")
    private boolean myPost;
    @ApiModelProperty(example = "false",notes = "true -> 판매완료")
    private boolean complete;


    public void setUserAction(boolean like, boolean wish, boolean isFollowing) {
        this.isLike = like;
        this.isWish = wish;
        this.isFollowing = isFollowing;
    }

    public DetailForm(Long id, Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum, List<String> images, String title, String category, String date, String content, int price, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean complete) {
        this.id = id;
        this.userId = userId;
        this.author = author;
        this.author_picture = author_picture;
        this.univ = univ;
        this.cert_uni = cert_uni;
        this.cert_celeb = cert_celeb;
        this.followerNum = followerNum;
        this.images = images;
        this.title = title;
        this.category = category;
        this.date = date;
        this.content = content;
        this.price = price;
        this.suggest = suggest;
        this.likesCnt = likesCnt;
        this.views = views;
        this.wishCnt = wishCnt;
        this.chatCnt = chatCnt;
        this.complete = complete;
    }

    public void setUserInfo(Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum) {
        this.userId = userId;
        this.author = "(탈퇴한 회원) " + author;
        this.author_picture = author_picture;
        this.univ = univ;
        this.cert_uni = cert_uni;
        this.cert_celeb = cert_celeb;
        this.followerNum = followerNum;
    }

    public void setMyPost() {
        this.myPost = true;
    }


}
