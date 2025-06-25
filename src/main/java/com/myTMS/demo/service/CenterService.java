package com.myTMS.demo.service;

import com.myTMS.demo.dao.Center;
import com.myTMS.demo.repository.interfaces.JPACenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CenterService {
    private final JPACenterRepository centerRepository;

    public void createCenter(String name, Double xAddress, Double yAddress, String description) {
        Center createdCenter = Center.builder()
                .name(name)
                .xAddress(xAddress)
                .yAddress(yAddress)
                .description(description)
                .build();
        centerRepository.save(createdCenter);
    }

    @Transactional(readOnly = true)
    public Center findClosestCenter(Double xAddress, Double yAddress) {
        return centerRepository.findAll().stream()
                .min((o1, o2) -> {
                    double distance1 = calculateDistance(o1, xAddress, yAddress);
                    double distance2 = calculateDistance(o2, xAddress, yAddress);
                    return Double.compare(distance1, distance2);
                })
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public void findAllCenters() {
        List<Center> centers = centerRepository.findAll();
        for (Center center : centers) {
            log.info("Center ID: {}, Name: {}, X Address: {}, Y Address: {}, Description: {}",
                    center.getId(), center.getName(), center.getXAddress(), center.getYAddress(), center.getDescription());
        }
    }

    private double calculateDistance(Center center, Double xAddress, Double yAddress) {
        double dx = center.getXAddress() - xAddress;
        double dy = center.getYAddress() - yAddress;
        return Math.sqrt(dx * dx + dy * dy);
    }

}
