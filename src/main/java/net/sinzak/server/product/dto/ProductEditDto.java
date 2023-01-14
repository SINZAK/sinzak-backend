package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import net.sinzak.server.common.dto.EditDto;

@Getter
public class ProductEditDto extends EditDto {
    @ApiModelProperty(value = "가로 사이즈 1m50cm -> 150",example ="150")
    private int width;
    @ApiModelProperty(value = "세로 사이즈",example ="150")
    private int vertical;
    @ApiModelProperty(value = "높이 사이즈",example ="50")
    private int height;

    public ProductEditDto(String title, String content, int price, boolean suggest, int width, int vertical, int height) {
        super(title, content, price, suggest);
        this.width = width;
        this.vertical = vertical;
        this.height = height;
    }
}
