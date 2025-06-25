package com.myTMS.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.*;
import com.myTMS.demo.dao.delivery.Delivery;
import com.myTMS.demo.dao.delivery.OverseasDelivery;
import com.myTMS.demo.dao.typeconst.*;
import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.dto.*;
import com.myTMS.demo.dto.delivery.DeliveryDistanceDTO;
import com.myTMS.demo.dto.order.OrderDetailsDTO;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.dto.order.PFSOrderDTO;
import com.myTMS.demo.dto.user.UserCheckoutDTO;
import com.myTMS.demo.service.*;
import com.myTMS.demo.service.localrep.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final CenterService centerService;
    private final DeliveryService deliveryService;
    private final MessageSource messageSource;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    /**
     * userDetails 를 사용해 객체에 기본 데이터를 담아 model 에 담아 보냄 (setUserCheckoutDTO)
     * 장바구니 리스팅도 진행하는데 이 때 장바구니가 비어있으면 ErrorController 로 에러 메시지를 전송
     * 마지막으로 비어있는 payment 객체도 추가하여 model 에 담아 checkout 페이지로 보냄
     */
    @GetMapping("/checkout")
    public String order(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request) {
        Users users = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (users != null) {
            setUserCheckoutDTO(model, users);
        }
        Cart cart = cartService.getCart(userDetails.getUserId());
        if (cart != null) {
            cartListing(model, cart);
        } else {
            HttpSession session = request.getSession(false);
            session.setAttribute(MessageType.errorMessage.name(), messageSource.getMessage("Cart.Empty", null, request.getLocale()));
            return "redirect:/error";
        }

        Payment payment = new Payment();
        model.addAttribute("payment", payment);

        return "/valid/checkout";
    }

    /**
     * 주문 리스트를 출력하는 페이지, 실제 주문이 DB 상에 존재하지 않으면 에러메시지 처리
     * 주문이 존재한다면 캐싱된 주문 리스트를 읽어와 리스팅 시켜 보냄
     */
    @GetMapping("/order")
    public String orderPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest request){

        List<Orders> orders = orderService.getOrderByUserId(userDetails.getUserId()).orElse(null);

        if (orders == null || orders.isEmpty()) {
            HttpSession session = request.getSession(false);
            session.setAttribute(MessageType.errorMessage.name(), messageSource.getMessage("order.empty", null, request.getLocale()));
            return "redirect:/error";
        }

        String objectData = redisService.getData(userDetails.getUsername(),RedisConst.USER_ORDER_KEY.get());

        try {
            List<OrderItemListDTO> results = objectMapper.readValue(objectData, new TypeReference<List<OrderItemListDTO>>() {
            });
            model.addAttribute("orders", results);
        } catch (Exception e) {
            log.error("Error parsing order data from Redis: {}", e.getMessage());
        }

        return "/valid/profile/orders";
    }

    /**
     * submit 된 주문을 받아 실제로 생성하는 페이지, 검증이 완료되지 않으면 리스팅과 함께 체크아웃 페이지로 리턴
     * 검증이 완료됐으면 각 데이터를 매핑시켜주는 단계가 있는데 카카오 맵 API 를 통해 거리를 계산하고 이로 받은 데이터를 기반으로
     * 배송 담당 센터와 Delivery 객체를 매핑하여 설정, 각 주문에 대한 캐싱 정보도 Redis 에 등록해주고 Cart 는 초기화
     * orderList 페이지로 보냄
     */
    @PostMapping("/order")
    public String order(@Valid @ModelAttribute("user") UserCheckoutDTO userCheckoutDTO, BindingResult br, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (br.hasErrors()) {
            cartListing(model, cartService.getCart(userDetails.getUserId()));
            log.info("errors = {}", br.getAllErrors());
            return "/valid/checkout";
        }

        log.info("userCheckoutDTO = {}", userCheckoutDTO.toString());
        Double x = Double.parseDouble(userCheckoutDTO.getXAddress());
        Double y = Double.parseDouble(userCheckoutDTO.getYAddress());
        Center closestCenter = centerService.findClosestCenter(x, y);
        DeliveryDistanceDTO distance = deliveryService.getDistance(x, y, closestCenter);
        if (distance == null) {
            br.reject("Delivery.error", messageSource.getMessage("Delivery.error", null, Locale.getDefault()) );
            return "/valid/checkout";
        }
        Cart cart = cartService.getCart(userDetails.getUserId());
        Optional<Orders> optOrders = orderService.createOrder(cart);
        Payment payment = userCheckoutDTO.getPayment();
        payment.setUserId(userDetails.getUserId());
        optOrders.ifPresent(orders -> {
            orderService.enrollPayment(orders, payment);
        });

        Delivery delivery = deliveryService.createDelivery(userCheckoutDTO, userDetails.getUserId(), optOrders.orElse(null), closestCenter, distance);

        log.info("delivery = {}", delivery);
        log.info("center = {}", delivery.getDeparture().getName());


        setOrderRedisCache(userDetails);

        cartService.removeCart(userDetails.getUserId());

        return "redirect:/order";
    }

    /**
     * 해외직구 체크아웃 페이지, 유저 정보로 DTO 데이터를 매핑하고 model 에 담아 보냄
     * userType 에 대한 검사도 진행
     */
    @GetMapping("/order/overseas")
    public String overseasOrder(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails.getUserType().equals(UserType.Delivery)) {
            PFSOrderDTOSettings(userDetails, model);

            return "valid/pfs_checkout";
        }
        return "redirect:/home";
    }

    /**
     * 해외 직구 체크아웃 페이지에서 submit 한 데이터를 받아오는 메서드
     * 검증이 되지 않으면 다시 checkout 페이지로 빈 객체를 담아 보내고
     * 검증이 완료됐다면 센터 지정, Delivery 설정, 더미 아이템 설정, 주문 캐싱 데이터 설정 등을 진행하고
     * 오더 리스트 페이지로 보냄
     */
    @PostMapping("/order/overseas")
    public String overseasOrderSubmit(@Valid @ModelAttribute("pfs") PFSOrderDTO DTO, BindingResult br, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("pfs", DTO);
            model.addAttribute("weightCategoryValues", WeightCategory.values());
            model.addAttribute("paymentValues", PaymentType.values());
            log.info("dto = {}", DTO.toString());

            return "valid/pfs_checkout";
        }

        log.info("dto = {}", DTO.toString());
        String xAddress = DTO.getDTO().getXAddress();
        String yAddress = DTO.getDTO().getYAddress();
        Center closestCenter = centerService.findClosestCenter(Double.valueOf(xAddress), Double.valueOf(yAddress));
        DeliveryDistanceDTO distance = deliveryService.getDistance(Double.valueOf(xAddress), Double.valueOf(yAddress), closestCenter);

        orderService.createPFSOrder().ifPresent(o -> {
            o.setUsers(userService.findById(userDetails.getUserId()).orElse(null));
            orderService.enrollPayment(o, DTO.getDTO().getPayment());
            o.setOrderItems(null);
            OverseasDelivery overseasDelivery = deliveryService.createOverseasDelivery(DTO, userDetails.getUserId(), o , closestCenter, distance);
            o.setTotalPrice(overseasDelivery.getCustomsFee());
            orderService.updateOrder(o);
            log.info("overseasDelivery = {}", overseasDelivery);

            setOrderRedisCache(userDetails);
        });

        return "redirect:/order";
    }

    /**
     * 카트 리스팅을 위해 만든 메서드 단순히 각 아이템의 데이터를 DTO 로 매핑하고 totalPrice 를 계산 후 model 에 addAttribute 함
     */
    private void cartListing(Model model, Cart cart) {

        Set<CartListDTO> items = itemService.findItems(cart);
        long totalPrice = 0L;
        for (CartListDTO item : items) {
            totalPrice += (long) item.getPrice() * item.getQuantity();
        }
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cart", items);
    }

    /**
     * 기본 정보를 담은 DTO 를 model 에 담아냄
     */
    private static void setUserCheckoutDTO(Model model, Users users) {
        UserCheckoutDTO userCheckoutDTO = new UserCheckoutDTO();
        userCheckoutDTO.setFirstName(users.getFirstName());
        userCheckoutDTO.setLastName(users.getLastName());
        userCheckoutDTO.setEmail(users.getEmail());
        model.addAttribute("user", userCheckoutDTO);
    }

    /**
     * 기본 정보를 담은 DTO 를 model 에 담아냄
     */
    private static void PFSOrderDTOSettings(CustomUserDetails userDetails, Model model) {
        model.addAttribute("pfs", new PFSOrderDTO(userDetails.getUsername()));
        model.addAttribute("weightCategoryValues", WeightCategory.values());
        model.addAttribute("paymentValues", PaymentType.values());
    }

    /**
     * Redis 에 캐싱하기 위해서 orderList 를 불러온 다음 각 Order 들을 OrderItemListDTO 로 이뤄진 List 를 생성하는 메서드이다
     * DeliveryType 에 따라서 해외 직구의 경우에는 Delivery 를 unProxying 하여 이를 매핑해준다
     * result 에 모든 OrderItemListDTO 가 담기면 이를 redisService 에 등록해 캐싱해준다.
     */
    private void setOrderRedisCache(CustomUserDetails userDetails) {
        List<Orders> orders = orderService.getOrderByUserId(userDetails.getUserId()).orElse(null);

        ArrayList<OrderItemListDTO> results = new ArrayList<>();

        for (Orders order : orders) {
            Delivery orderDelivery = order.getDelivery();

            if (orderDelivery.getDeliveryType().equals(DeliveryType.PFS)) {
                OverseasDelivery unproxy = (OverseasDelivery) Hibernate.unproxy(orderDelivery);

                OrderItemListDTO orderItemListDTO = new OrderItemListDTO();
                orderItemListDTO.setOrderPrice(order.getTotalPrice());
                orderItemListDTO.setOrderId(order.getId());
                orderItemListDTO.setItemName(unproxy.getWeightCategory().name()+" (해외 배송)");
                orderItemListDTO.setItemImgSrc("/asset/no-image.jpg");
                orderItemListDTO.setCartSize(1);

                results.add(orderItemListDTO);
                continue;
            }

            OrderItemListDTO orderItemListDTO = new OrderItemListDTO();
            orderItemListDTO.setOrderId(order.getId());
            List<OrderItem> orderItems = order.getOrderItems();
            Hibernate.initialize(orderItems);
            for (OrderItem orderItem : orderItems) {
                Item item = orderItem.getItem();
                Hibernate.initialize(item);
                orderItemListDTO.setItemName(item.getName());
                orderItemListDTO.setItemImgSrc(
                        (item.getImgSrc() == null || item.getImgSrc().isEmpty()) ? "/asset/no-image.jpg" : item.getImgSrc()
                );
            }
            orderItemListDTO.setCartSize(orderItems.size());
            orderItemListDTO.setOrderPrice(order.getTotalPrice());
            results.add(orderItemListDTO);
        }


        redisService.setObjectData(userDetails.getUsername(),RedisConst.USER_ORDER_KEY.get(), results);
    }

}
