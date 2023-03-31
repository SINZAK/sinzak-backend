package net.sinzak.server.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import net.sinzak.server.alarm.domain.AlarmType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAlarmDto {
    private AlarmType alarmType;
    private String date;
    private String thumbnail;
    private String link;
    private String opponentUserName;
}
