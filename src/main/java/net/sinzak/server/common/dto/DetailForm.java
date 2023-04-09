package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class DetailForm implements Serializable{ //redis에 저장하기 위한 Serializable

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
    private boolean cert_author;
    @ApiModelProperty(example = "작가 팔로워 수")
    private String followerNum;
    @ApiModelProperty(value = "이미 팔로잉 중인지 여부" , example = "true")
    private boolean following; //접두사 is 붙히면 에러남 (json은 언제나 자동으로 is를 삭제해줌) 컨트롤러 반환객체중 is붙혀져 있는 boolean 에만 적용하면 될듯
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
    @ApiModelProperty(value = "현 최고 제안 가격", example = "50000")
    private int topPrice;
    @ApiModelProperty(example = "false",notes = "true -> 체크 한 사람(가격 제안 받겠다는 사람)")
    private boolean suggest;
    @ApiModelProperty(example = "true",notes = "true -> 누른 사람")
    private boolean like;
    @ApiModelProperty(example = "3")
    private int likesCnt;
    @ApiModelProperty(example = "false",notes = "true -> 누른 사람")
    @JsonProperty("wish")
    private boolean wish;
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


    public DetailForm(Long id, Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_author, String followerNum, List<String> images, String title, String category, String date, String content, int price, int topPrice, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean complete) {
        this.id = id;
        this.userId = userId;
        this.author = author;
        this.author_picture = author_picture;
        this.univ = univ;
        this.cert_uni = cert_uni;
        this.cert_author = cert_author;
        this.followerNum = followerNum;
        this.images = images;
        this.title = title;
        this.category = category;
        this.date = date;
        this.content = content;
        this.price = price;
        this.topPrice = topPrice;
        this.suggest = suggest;
        this.likesCnt = likesCnt;
        this.views = views;
        this.wishCnt = wishCnt;
        this.chatCnt = chatCnt;
        this.complete = complete;
    }

    public void setUserInfo(Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum) {
        this.userId = userId;
        this.author = author;
        this.author_picture = author_picture;
        this.univ = univ;
        this.cert_uni = cert_uni;
        this.cert_author = cert_celeb;
        this.followerNum = followerNum;
    }

    public void setUserAction(boolean like, boolean wish, boolean isFollowing) {
        this.like = like;
        this.wish = wish;
        this.following = isFollowing;
    }

    public void setMyPost() {
        this.myPost = true;
    }


}
