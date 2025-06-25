package com.myTMS.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.typeconst.ChartType;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dto.chart.ChartDataDTO;
import com.myTMS.demo.dto.chart.ChartUnitDTO;
import com.myTMS.demo.dto.delivery.EmergencyDeliveryDTO;
import com.myTMS.demo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomScheduler {

    private final OrderService orderService;
    private final RedisService redisService;
    private final DeliveryService deliveryService;
    private final AlarmService alarmService;
    private final UserService userService;
    private final PostService postService;

    @Scheduled(cron = "0 * * * * *")
    public void checkEmergencyOrder(){
        List<EmergencyDeliveryDTO> emergencyDelivery = deliveryService.getEmergencyDelivery();
        for (EmergencyDeliveryDTO deliveryDTO : emergencyDelivery) {
            log.info("em orders = {}", deliveryDTO.getDeliveryId());
            if (deliveryService.addCacheEmDelivery(deliveryDTO.getDeliveryId())) {
                alarmService.sendAlarmToAll("alarm.order.emergency", true, MessageType.emMessage);
            }
        }
        redisService.setObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name(), emergencyDelivery);
    }

    @Scheduled(cron = "0 * * * * *")
    public void setDashboardChart(){
        cachingData();
    }

    private void cachingData() {
        // caching logics----------------------------
        String emDeliverySize = deliveryService.getEmDeliverySize();
        String usersCount = userService.getUsersCount();
        String countUnansweredPosts = postService.getCountUnansweredPosts();
        String countByDeliveryStatusNot = orderService.getCountByDeliveryStatusNot(DeliveryStatus.Delivered);
        Map<LocalDate, Long> localDateLongMap = userService.userSignUpTrendLast5Days();
        Map<LocalDate, Long> orderCountMapLast5Days = orderService.getOrderCountMapLast5Days();
        // --------------------------------------------

        ChartUnitDTO chartUnitDTO = generateChartUnit(ChartType.emSize, "Emergency Orders", "bar", List.of("Orders"), List.of(emDeliverySize));
        ChartUnitDTO chartUnitDTO1 = generateChartUnit(ChartType.userCount, "Total User", "bar", List.of("Users"), List.of(usersCount));
        ChartUnitDTO chartUnitDTO2 = generateChartUnit(ChartType.unAnsweredPosts, "UnAnswered Posts", "bar", List.of("Posts"), List.of(countUnansweredPosts));
        ChartUnitDTO chartUnitDTO3 = generateChartUnit(ChartType.unDeliveredOrders, "UnDelivered Orders", "bar", List.of("Orders"), List.of(countByDeliveryStatusNot));
        ChartUnitDTO chartUnitDTO4 = generateChartUnit(ChartType.signUpTrend5Days, "Trend of Sign Up Users", "line",
                localDateLongMap.keySet().stream().map(LocalDate::toString).toList(),
                localDateLongMap.values().stream().map(String::valueOf).toList());
        ChartUnitDTO chartUnitDTO5 = generateChartUnit(ChartType.orderTrend5Days, "Trend of Orders", "line",
                orderCountMapLast5Days.keySet().stream().map(LocalDate::toString).toList(),
                orderCountMapLast5Days.values().stream().map(String::valueOf).toList());

        ChartDataDTO chartDataDTO = generateChartData(chartUnitDTO, chartUnitDTO2, chartUnitDTO1, chartUnitDTO3, chartUnitDTO4, chartUnitDTO5);

        redisService.setObjectData(RedisConst.DASHBOARD_CACHE_KEY.name(), chartDataDTO);
    }

    private ChartUnitDTO generateChartUnit(ChartType canvasId, String label, String type, List<String> labels, List<String> data) {
        return new ChartUnitDTO(canvasId.name(), label, type, labels, data);
    }

    private ChartDataDTO generateChartData(ChartUnitDTO ... chartUnitDTO) {
        List<ChartUnitDTO> list = new ArrayList<>();
        Collections.addAll(list, chartUnitDTO);
        return new ChartDataDTO(list);
    }
}
