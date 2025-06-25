package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Alarm;
import com.myTMS.demo.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/api")
@Slf4j
@Controller
@RequiredArgsConstructor
public class AlarmRestController {

    @Value("${alarm.max.page.size}")
    private Integer pageSize;

    private final AlarmService alarmService;

    @PostMapping("/alarm/employee")
    public ResponseEntity<Page<Alarm>> getAlarmForEmployee(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestBody Map<String, Object> data) {
        String s = String.valueOf(data.get("page"));
        Page<Alarm> alarms = alarmService.getAlarmsForEmployee(userDetails.getUserId(), Integer.valueOf(s), pageSize);
        if (alarms == null || alarms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(alarms);
    }

    @PostMapping("/alarm/general")
    public ResponseEntity<Page<Alarm>> getAlarmForGeneral(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestBody Map<String, Object> data) {
        String s = String.valueOf(data.get("page"));
        Page<Alarm> alarmsForGeneral = alarmService.getAlarmsForGeneral(userDetails.getUserId(), Integer.valueOf(s), pageSize);
        if (alarmsForGeneral == null || alarmsForGeneral.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(alarmsForGeneral);
    }

    @GetMapping("/alarm/remove")
    public ResponseEntity<Void> removeAlarmForGeneral(@AuthenticationPrincipal CustomUserDetails userDetails) {
        alarmService.removeAlarms(userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
