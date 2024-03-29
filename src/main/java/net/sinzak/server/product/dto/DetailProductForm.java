package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.sinzak.server.common.dto.DetailForm;

import java.util.List;

@Getter
public class DetailProductForm extends DetailForm {
    @ApiModelProperty(example = "150")
    private int width;
    @ApiModelProperty(example = "70")
    private int vertical;
    @ApiModelProperty(example = "30", notes = "nullable")
    private int height;
    @Builder
    public DetailProductForm(Long id, Long userId, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum, List<String> images, String title, String category, String date, String content, int price, int topPrice, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean complete, int width, int vertical, int height) {
        super(id, userId, author, author_picture, univ, cert_uni, cert_celeb, followerNum, images, title, category, date, content, price, topPrice, suggest, likesCnt, views, wishCnt, chatCnt, complete);
        this.width = width;
        this.vertical = vertical;
        this.height = height;
    }
    public DetailProductForm() {
        super();
    }
}
