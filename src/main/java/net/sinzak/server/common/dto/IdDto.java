package net.sinzak.server.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class IdDto {
    @ApiModelProperty(value = "대상 id")
    private Long id;
}
