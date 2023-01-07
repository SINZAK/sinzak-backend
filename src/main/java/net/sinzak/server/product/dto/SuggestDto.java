package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SuggestDto {
    @ApiModelProperty(value = "해당 작품 id", example = "1024", dataType = "int")
    private Long productId;
    @ApiModelProperty(value = "제안가격", example = "1024", dataType = "int")
    private int price;
}
