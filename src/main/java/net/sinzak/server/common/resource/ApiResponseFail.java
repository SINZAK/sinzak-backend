package net.sinzak.server.common.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Entity;

@Getter
public class ApiResponseFail {
    @ApiModelProperty(value = "false", example = "false")
    private boolean success;
    private String message;
}
