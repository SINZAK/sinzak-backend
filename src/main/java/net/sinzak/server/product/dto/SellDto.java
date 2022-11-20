package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SellDto {

    @ApiModelProperty(example = "판매한 유저 id")
    private Long userId;
    @ApiModelProperty(example = "판매한 작품 id")
    private Long productId;
}
