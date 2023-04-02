package net.sinzak.server.alarm.controller;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.alarm.service.AlarmService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    @GetMapping(value ="/alarms")
    public JSONObject getAlarms(){
        return alarmService.getAlarms();
    }
}
