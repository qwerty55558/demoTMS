package com.myTMS.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.delivery.OverseasDelivery;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import com.myTMS.demo.dto.CenterDeliveryCount;
import com.myTMS.demo.dto.delivery.DeliveryDistanceDTO;
import com.myTMS.demo.dto.delivery.EmergencyDeliveryDTO;
import com.myTMS.demo.dto.order.PFSOrderDTO;
import com.myTMS.demo.dto.user.UserCheckoutDTO;
import com.myTMS.demo.repository.interfaces.JPADeliveryRepository;
import com.myTMS.demo.repository.interfaces.JPAOrdersRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeliveryService {
    private final List<Long> cachingDelivery = Collections.synchronizedList(new ArrayList<>());

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final JPADeliveryRepository deliveryRepository;
    private final JPAOrdersRepository ordersRepository;

    @Value("${kakao.rest.api.key}")
    private String restApiKey;

    @Value("${delivery.days.time}")
    private Long deliveryTime;

    @Value("${delivery.days.time.pfs}")
    private Long deliveryTimeForPFS;

    // 배송비
    @Getter
    @Value("${delivery.price}")
    private Long deliveryPrice;

    @Value("${delivery.international.company}")
    private String internationalCooperationCompany;

    @Value("${delivery.excepted.time}")
    private Long expectedTime;

    public Delivery createDelivery(UserCheckoutDTO userCheckoutDTO, Long userId, Orders order, Center departure, DeliveryDistanceDTO distanceDTO) {
        Delivery delivery = new Delivery();
        delivery.setUserId(userId);
        delivery.setDeliveryType(DeliveryType.General);
        delivery.setStatus(DeliveryStatus.OrderPlacement);
        delivery.setOrders(order);
        delivery.setDeparture(departure);
        delivery.setDistance(distanceDTO.getDistance().toString());
        delivery.setPrice(deliveryPrice);
        delivery.setDestination(userCheckoutDTO.getAddress()
                + (userCheckoutDTO.getAddress2() != null ? " " + userCheckoutDTO.getAddress2() : ""));

        delivery.setExpectedAt(Date.from(
                Instant.now()
                        .truncatedTo(ChronoUnit.SECONDS)
                        .plus(deliveryTime * 24 * 60 * 60, ChronoUnit.SECONDS)
                        .plus(distanceDTO.getDuration(), ChronoUnit.SECONDS)
        ));

        Delivery saved = deliveryRepository.save(delivery); // 마지막에 한 번만 save

        order.setDelivery(saved);
        ordersRepository.save(order);

        return saved;
    }

    public OverseasDelivery createOverseasDelivery(PFSOrderDTO dto, Long userId, Orders order, Center departure, DeliveryDistanceDTO distanceDTO) {
        OverseasDelivery overseasDelivery = new OverseasDelivery();

        // parent class field
        overseasDelivery.setUserId(userId);
        overseasDelivery.setDeliveryType(DeliveryType.PFS);
        overseasDelivery.setStatus(DeliveryStatus.OrderPlacement);
        overseasDelivery.setDistance(distanceDTO.getDistance().toString());
        overseasDelivery.setPrice(deliveryPrice);
        overseasDelivery.setDeparture(departure);
        overseasDelivery.setDestination(dto.getDTO().getAddress()
                + (dto.getDTO().getAddress2() != null ? " " + dto.getDTO().getAddress2() : ""));
        overseasDelivery.setOrders(order);

        // child class field
        overseasDelivery.setTrackingNumber(dto.getTrackingNumber());
        overseasDelivery.setIntlCarrier(internationalCooperationCompany);
        overseasDelivery.setWeightCategory(dto.getWeightCategory());
        overseasDelivery.setCustomsFee(dto.getWeightCategory().get());

        overseasDelivery.setExpectedAt(Date.from(
                Instant.now()
                        .truncatedTo(ChronoUnit.SECONDS)
                        .plus(deliveryTimeForPFS * 24 * 60 * 60, ChronoUnit.SECONDS)
                        .plus(distanceDTO.getDuration(), ChronoUnit.SECONDS)
        ));

        OverseasDelivery saved = deliveryRepository.save(overseasDelivery);
        order.setDelivery(saved);
        ordersRepository.save(order);

        return overseasDelivery;
    }

    @Transactional(readOnly = true)
    public DeliveryDistanceDTO getDistance(Double x, Double y, Center departure) {
        String url = "https://apis-navi.kakaomobility.com/v1/directions?" +
                "origin=" + departure.getXAddress() + "," + departure.getYAddress() +
                "&destination=" + x + "," + y +
                "&roadevent=2" +
                "&summary=true";

        log.info("url = {}", url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + restApiKey);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            String body = exchange.getBody();

            try {

                JsonNode jsonNode = objectMapper.readTree(body);
                JsonNode leafNode = jsonNode.path("routes").get(0).path("sections").get(0);
                int duration = leafNode.path("duration").asInt();
                int distance = leafNode.path("distance").asInt();

                DeliveryDistanceDTO deliveryDistanceDTO = new DeliveryDistanceDTO();
                deliveryDistanceDTO.setDuration(duration);
                deliveryDistanceDTO.setDistance(distance);
                return deliveryDistanceDTO;

            } catch (Exception e) {
                log.info("Distance Request Error ", e);
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<EmergencyDeliveryDTO> getEmergencyDelivery(){
        ArrayList<EmergencyDeliveryDTO> emDTOs = new ArrayList<>();
        List<Delivery> emDAOs = deliveryRepository.findByExpectedAtLessThanAndStatusNot(
                Date.from(Instant.now().plus(expectedTime, ChronoUnit.HOURS)),
                DeliveryStatus.Delivered
        );
        for (Delivery delivery : emDAOs) {
            emDTOs.add(new EmergencyDeliveryDTO(delivery));
        }
        return emDTOs;
    }

    @Transactional(readOnly = true)
    public Map<Center, Long> getCenterMapByDeliveryCounts(){
        List<CenterDeliveryCount> results = deliveryRepository
                .countDeliveriesByCenterBeforeExpectedAtAndStatusNot(Date.from(Instant.now()), DeliveryStatus.Delivered);

        return results.stream()
                .collect(Collectors.toMap(CenterDeliveryCount::getCenter, CenterDeliveryCount::getCount));
    }

    public boolean addCacheEmDelivery(Long id) {
        synchronized (cachingDelivery) {
            if (!cachingDelivery.contains(id)) {
                cachingDelivery.add(id);
                return true;
            }
            return false;
        }
    }

    public void removeCacheEmDelivery(Long id) {
        synchronized (cachingDelivery) {
            cachingDelivery.remove(id);
        }
    }

    public String getEmDeliverySize(){
        return String.valueOf(cachingDelivery.size());
    }


}
