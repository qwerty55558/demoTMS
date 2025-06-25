package com.myTMS.demo.service;

import com.myTMS.demo.dao.*;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.DeliveryType;
import com.myTMS.demo.dao.typeconst.Keyword;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.dto.OrderCountProjection;
import com.myTMS.demo.dto.order.OrderDetailsDTO;
import com.myTMS.demo.dto.order.OrderDetailsItemListDTO;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.dto.order.OrderSearchParamDTO;
import com.myTMS.demo.repository.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final JPAOrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final JPAItemRepository itemRepository;
    private final JPAOrdersItemRepository ordersItemRepository;
    private final JPAPaymentRepository paymentRepository;
    private final JPACenterRepository centerRepository;
    private final RedisService redisService;

    public Optional<Orders> createOrder(Cart cart) {
        Orders orders = new Orders();

        userRepository.findUserById(cart.getUserId()).ifPresent(orders::setUsers);

        Map<Long, CartItem> cartItemMap = cart.getCartItemMap();

        AtomicLong totalPrice = new AtomicLong(0L);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem value : cartItemMap.values()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(orders);
            itemRepository.findById(value.getItemId()).ifPresent(item -> {
                orderItem.setItem(item);
                orderItem.setOrderPrice(item.getPrice() * value.getQuantity());
                orderItem.setAmount(value.getQuantity());
                totalPrice.addAndGet((long) item.getPrice() * value.getQuantity());
            });
            orderItems.add(orderItem);
            ordersItemRepository.save(orderItem);
        }
        orders.setTotalPrice(totalPrice.get());
        orders.setOrderItems(orderItems);

        ordersRepository.save(orders);

        return Optional.of(orders);
    }

    public Optional<Orders> enrollPayment(Orders orders, Payment payment) {
        paymentRepository.save(payment);
        orders.setPayment(payment);
        return Optional.of(ordersRepository.save(orders));
    }

    public Optional<Orders> createPFSOrder() {
        Orders orders = new Orders();
        ordersRepository.save(orders);
        return Optional.of(orders);
    }

    public Optional<Orders> updateOrder(Orders orders) {
        return Optional.of(ordersRepository.save(orders));
    }


    @Transactional(readOnly = true)
    public Optional<List<Orders>> getOrderByUserId(Long userId) {
        return Optional.ofNullable(ordersRepository.findOrdersByUsers_Id(userId));
    }

    @Transactional(readOnly = true)
    public Optional<Users> getUserByOrderId(Long orderId) {
        Users users = ordersRepository.findById(orderId).get().getUsers();
        Hibernate.initialize(users);
        return Optional.of(users);
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderItem>> getOrderItemByOrderId(Long orderId) {
        Optional<Orders> byId = ordersRepository.findById(orderId);
        if (byId.isPresent()) {
            Orders orders = byId.get();
            Hibernate.initialize(orders.getOrderItems());
            return Optional.of(orders.getOrderItems());
        } else {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Orders> getOrderById(Long orderId) {
        return ordersRepository.findById(orderId);
    }


    @Transactional(readOnly = true)
    public Optional<OrderDetailsDTO> getOrderDetails(Long orderId, Long userId) {
        return ordersRepository.findOrderDetails(orderId)
                .map(orderDetailsDTO -> {
                    if (!orderDetailsDTO.getUserId().equals(userId)) {
                        return null;
                    }
                    return getOrderDetailsDTO(orderId, orderDetailsDTO);
                });
    }

    @Transactional(readOnly = true)
    public Optional<OrderDetailsDTO> getOrderDetails(Long orderId, UserType userType) {
        return ordersRepository.findOrderDetails(orderId)
                .map(orderDetailsDTO -> {
                    if (!userType.equals(UserType.Employee) &&
                            !userType.equals(UserType.Admin)) {
                        return null;
                    }
                    return getOrderDetailsDTO(orderId, orderDetailsDTO);
                });
    }

    private OrderDetailsDTO getOrderDetailsDTO(Long orderId, OrderDetailsDTO orderDetailsDTO) {
        List<OrderItem> orderItems = ordersItemRepository.findOrderItemsWithItemsByOrders_Id(orderId).orElse(List.of());
        orderDetailsDTO.setOrderItems(
                orderItems.stream()
                        .map(orderItem -> {
                            Item item = orderItem.getItem();
                            return new OrderDetailsItemListDTO(
                                    orderItem.getId(), item.getName(), (item.getImgSrc() == null || item.getImgSrc().isEmpty() ? "/asset/no-image.jpg" : item.getImgSrc()), item.getDescription(),
                                    item.getPrice(), orderItem.getAmount()
                            );
                        })
                        .toList()
        );
        return orderDetailsDTO;
    }

    public Long proceedDelivery(Long orderId, Long userId) {
        Optional<Orders> orderById = getOrderById(orderId);
        if (orderById.isPresent()) {
            Orders orders = orderById.get();

            Hibernate.initialize(orders.getUsers());
            if (!orders.getUsers().getId().equals(userId)) {
                return -1L;
            }

            DeliveryStatus status = DeliveryStatus.nextStatus(orders.getDelivery().getStatus());
            orders.getDelivery().setStatus(status);
            ordersRepository.save(orders);

            if (status.equals(DeliveryStatus.Delivered)) {
                return orders.getDelivery().getId();
            }
        }
        return -1L;
    }

    public Long proceedDelivery(Long orderId) {
        Optional<Orders> orderById = getOrderById(orderId);
        if (orderById.isPresent()) {
            Orders orders = orderById.get();
            DeliveryStatus currentStatus = orders.getDelivery().getStatus();

            if (currentStatus.equals(DeliveryStatus.Delivered)) {
                return -1L;
            }

            DeliveryStatus nextStatus = DeliveryStatus.nextStatus(currentStatus);
            orders.getDelivery().setStatus(nextStatus);
            ordersRepository.save(orders);

            if (nextStatus.equals(DeliveryStatus.Delivered)) {
                return orders.getDelivery().getUserId();
            }
        }
        return -1L;
    }

    @Transactional(readOnly = true)
    public void updateOrderDeliveryExpectedAt(Long orderId) {
        getOrderById(orderId).ifPresent(orders -> {
            orders.getDelivery().setExpectedAt(
                    Date.from(Instant.now().plus(20, ChronoUnit.HOURS))
            );
            ordersRepository.save(orders);
        });

    }

    public Page<OrderItemListDTO> getOrderItemList(Pageable pageable) {
        Page<Orders> all = ordersRepository.findAll(pageable);
        return toDtoPage(all, pageable);
    }

    public Object dtoToParameter(OrderSearchParamDTO dto) {
        Class<?> type = dto.getKeyword().getType();
        String rawValue = dto.getValue();
        Object castedValue;

        if (type == Long.class) {
            castedValue = Long.parseLong(rawValue);
        } else if (type == LocalDateTime.class) {
            LocalDate date = LocalDate.parse(rawValue, DateTimeFormatter.BASIC_ISO_DATE);
            castedValue = date.atStartOfDay();
        } else if (type == DeliveryStatus.class) {
            castedValue = DeliveryStatus.valueOf(rawValue);
        } else if (type == DeliveryType.class) {
            castedValue = DeliveryType.valueOf(rawValue);
        } else if (type == Center.class) {
            castedValue = centerRepository.findCenterByName(rawValue)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 센터입니다."));
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
        return castedValue;
    }

    @Transactional(readOnly = true)
    public <T> Page<OrderItemListDTO> getOrderItemList(int page, int pageSize, Keyword keyword, Sort sort, T value) {
        PageRequest pageable = PageRequest.of(page, pageSize, sort);

        if (keyword == null && value == null) {
            Page<Orders> result = ordersRepository.findAll(pageable);
            return toDtoPage(result, pageable);
        }

        switch (keyword) {
            case departure:
                if (keyword.getType().isInstance(value)) {
                    Center center = (Center) value;
                    Page<Orders> result = ordersRepository.findByDelivery_Departure_Id(center.getId(), pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case deliveryId:
                if (keyword.getType().isInstance(value)) {
                    Long id = (Long) value;
                    Page<Orders> result = ordersRepository.findByDelivery_Id(id, pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case createdAt:
                if (keyword.getType().isInstance(value)) {
                    LocalDateTime date = (LocalDateTime) value;
                    Page<Orders> result = ordersRepository.findByCreatedAtBetween(date, date.plusDays(1), pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case deliveryType:
                if (keyword.getType().isInstance(value)) {
                    DeliveryType type = (DeliveryType) value;
                    Page<Orders> result = ordersRepository.findByDelivery_DeliveryType(type, pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case status:
                if (keyword.getType().isInstance(value)) {
                    DeliveryStatus status = (DeliveryStatus) value;
                    Page<Orders> result = ordersRepository.findByDelivery_Status(status, pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case userId:
                if (keyword.getType().isInstance(value)) {
                    Long id = (Long) value;
                    Page<Orders> result = ordersRepository.findByDelivery_UserId(id, pageable);
                    return toDtoPage(result, pageable);
                }
                break;
            case null:
                break;
            default:
                log.info("Unhandled keyword !!!!!!!!!!");
        }

        return Page.empty();
    }

    private Page<OrderItemListDTO> toDtoPage(Page<Orders> ordersPage, Pageable pageable) {
        List<Orders> ordersList = ordersPage.getContent();

        List<OrderItem> allItems = ordersItemRepository.findOrderItemsWithItemsByOrdersIn(ordersList);

        Map<Long, List<OrderItem>> grouped = allItems.stream()
                .collect(Collectors.groupingBy(oi -> oi.getOrders().getId()));

        List<OrderItemListDTO> list = ordersList.stream()
                .map(orders -> {
                    List<OrderItem> items = grouped.getOrDefault(orders.getId(), Collections.emptyList());
                    if (items.isEmpty()) {
                        return new OrderItemListDTO(orders.getId(), "PFS", "/asset/no-image.jpg", 1, orders.getTotalPrice());
                    }
                    OrderItem first = items.getFirst();
                    Item item = first.getItem();
                    return new OrderItemListDTO(
                            orders.getId(), item.getName(), item.getImgSrc(), items.size(), orders.getTotalPrice()
                    );
                }).toList();

        return new PageImpl<>(list, pageable, ordersPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public String getCountByDeliveryStatusNot(DeliveryStatus deliveryStatus) {
        return String.valueOf(ordersRepository.countByDelivery_StatusNot(deliveryStatus));
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, Long> getOrderCountMapLast5Days() {
        LocalDateTime fiveDaysAgo = LocalDate.now().minusDays(4).atStartOfDay();
        List<OrderCountProjection> results = ordersRepository.countOrdersByDate(fiveDaysAgo);

        Map<LocalDate, Long> map = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            map.put(LocalDate.from(fiveDaysAgo.plusDays(i)), 0L);
        }

        for (OrderCountProjection result : results) {
            map.put(result.getDate(), result.getCount());
        }

        return map;
    }

}