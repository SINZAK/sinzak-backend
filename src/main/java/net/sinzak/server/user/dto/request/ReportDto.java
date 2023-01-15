package net.sinzak.server.user.dto.request;

import lombok.Getter;

@Getter
public class ReportDto {
    private Long userId;
    private String reason;
}