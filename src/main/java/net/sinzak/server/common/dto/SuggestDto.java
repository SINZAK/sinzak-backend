package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SuggestDto {
    @ApiModelProperty(value = "해당 대상 id", example = "1024", dataType = "int")
    private Long id;
    @ApiModelProperty(value = "제안가격", example = "1024", dataType = "int")
    private int price;
}
