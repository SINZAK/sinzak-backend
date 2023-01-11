package net.sinzak.server.work.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.common.dto.DetailForm;

import java.util.List;

@Getter
public class DetailWorkForm extends DetailForm {
    @ApiModelProperty(value = "의뢰해요인지 작업해요인지", example = "true")
    private boolean employment;

    @Builder
    public DetailWorkForm(Long id, Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum, List<String> images, String title, String category, String date, String content, int price, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean complete, boolean employment) {
        super(id, userId, author, author_picture, univ, cert_uni, cert_celeb, followerNum, images, title, category, date, content, price, suggest, likesCnt, views, wishCnt, chatCnt, complete);
        this.employment = employment;
    }
}
