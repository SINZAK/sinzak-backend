package net.sinzak.server.alarm.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.alarm.domain.Alarm;
import net.sinzak.server.alarm.dto.GetAlarmDto;
import net.sinzak.server.alarm.repository.AlarmRepository;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final UserUtils userUtils;
    private final AlarmRepository alarmRepository;
    @Transactional
    public JSONObject getAlarms(){
        List<GetAlarmDto> getAlarmDtos = new ArrayList<>();
        Set<Alarm> alarms = alarmRepository.findByUserId(userUtils.getCurrentUserId());
        for(Alarm alarm : alarms){
            GetAlarmDto getAlarmDto = GetAlarmDto.builder()
                    .date(alarm.getCreatedDate().toString())
                    .detail(alarm.getDetail())
                    .thumbnail(alarm.getThumbnail())
                    .link(alarm.getLink())
                    .build();
            getAlarmDtos.add(getAlarmDto);
        }
        return PropertyUtil.response(getAlarmDtos);
    }
}
