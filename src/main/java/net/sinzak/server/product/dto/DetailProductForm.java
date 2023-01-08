package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.common.dto.DetailForm;

import java.util.List;

@Getter
public class DetailProductForm extends DetailForm {
    @ApiModelProperty(value = "작품 가격", example = "30000")
    private int price;
    @ApiModelProperty(example = "150")
    private int width;
    @ApiModelProperty(example = "70")
    private int vertical;
    @ApiModelProperty(example = "30", notes = "nullable")
    private int height;

    @Builder
    public DetailProductForm(Long id, String author, String author_picture, String univ, boolean cert_uni, boolean cert_celeb, String followerNum, List<String> images, String title, String category, String date, String content, boolean suggest, int likesCnt, int views, int wishCnt, int chatCnt, boolean trading, boolean complete, int price, int width, int vertical, int height) {
        super(id, author, author_picture, univ, cert_uni, cert_celeb, followerNum, images, title, category, date, content, suggest, likesCnt, views, wishCnt, chatCnt, trading, complete);
        this.price = price;
        this.width = width;
        this.vertical = vertical;
        this.height = height;
    }
}
