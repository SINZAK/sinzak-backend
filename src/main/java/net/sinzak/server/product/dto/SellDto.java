package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SellDto {
    @ApiModelProperty(value = "해당 작품/의뢰 id", example = "102", dataType = "int")
    private Long postId;
}
