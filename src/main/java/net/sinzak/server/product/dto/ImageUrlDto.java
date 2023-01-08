package net.sinzak.server.product.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class ImageUrlDto {
    @ApiModelProperty(name = "이미지URL", example = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/8293d5ef-20f9-4596-85dc-aeaa012ecb24.jpg")
    private String url;
}
