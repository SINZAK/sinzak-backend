package net.sinzak.server.work.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.common.dto.DetailForm;

import java.util.List;

@Getter
public class DetailWorkForm extends DetailForm {
    @ApiModelProperty(value = "외주 페이", example = "30000")
    private int pay;
    @ApiModelProperty(value = "의뢰해요인지 작업해요인지 ", example = "true")
    private boolean employment;

    @Builder
    public DetailWorkForm(Long id, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum, List<String> images, String title, String category, String date, String content, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean trading, boolean complete, int pay, boolean employment) {
        super(id, author, author_picture, univ, cert_uni, cert_celeb, followerNum, images, title, category, date, content, suggest, likesCnt, views, wishCnt, chatCnt, trading, complete);
        this.pay = pay;
        this.employment = employment;
    }
}
