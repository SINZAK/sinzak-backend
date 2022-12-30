package net.sinzak.server.common.resource;

import lombok.Getter;

import javax.persistence.Entity;

@Getter
public class ApiResponseFail {
    private boolean success;
    private String message;
}
