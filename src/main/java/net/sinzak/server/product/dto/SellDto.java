package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SellDto {

    @ApiModelProperty(value = "판매한 유저 id", example = "3", dataType = "int")
    private Long userId;
    @ApiModelProperty(value = "판매한 작품 id", example = "1024", dataType = "int")
    private Long productId;
}
