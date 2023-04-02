package net.sinzak.server.cert.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class PortFolioDto {

    @ApiModelProperty(value = "포폴 링크", example = "https://github.com/in-seo , notion.so/dfaef")
    private String portFolio;
}
