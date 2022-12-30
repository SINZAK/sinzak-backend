package net.sinzak.server.common.resource;

import lombok.Getter;

import javax.persistence.GeneratedValue;

@Getter
public class ApiResponseSuccess {
    private boolean success;
}
