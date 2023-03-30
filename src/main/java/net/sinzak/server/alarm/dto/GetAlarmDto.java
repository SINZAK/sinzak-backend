package net.sinzak.server.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAlarmDto {
    private String detail;
    private String date;
    private String thumbnail;
    private String link;
}
