package com.myTMS.demo.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.dao.Cart;
import com.myTMS.demo.dto.CartListDTO;
import com.myTMS.demo.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class CartRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemService itemService;

    @Test
    void addItem() throws Exception{
        // 로그인 요청을 통해 세션 가져오기
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .locale(Locale.KOREA)
                        .param("email", "test@test.com")
                        .param("pw", "testtest1!"))
                .andReturn()
                .getRequest()
                .getSession();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/create")
                .locale(Locale.KOREA)
                .session(session));


        Map<String, Object> itemData = new HashMap<>();
        itemData.put("itemId", 1);

        String json = objectMapper.writeValueAsString(itemData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addItem")
                .locale(Locale.KOREA)
                .content(json)
                .contentType("application/json")
                .session(session));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addItem")
                .locale(Locale.KOREA)
                .content(json)
                .contentType("application/json")
                .session(session));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addItem")
                .locale(Locale.KOREA)
                .content(json)
                .contentType("application/json")
                .session(session));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/minusItem")
                .locale(Locale.KOREA)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .session(session));


//        Optional<Cart> cart = cartRepository.getCart(1L);
//        cart.map(c -> {
//            log.info("cart = {}", c.getCartItemMap());
//            return c;
//        }).orElseGet(() -> {
//            log.info("cart is empty");
//            return null;
//        });

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/list")
                        .locale(Locale.KOREA)
                        .session(session))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        if(contentAsString.isEmpty()){
            log.info("cart is empty");
        }else{
            Cart cart = objectMapper.readValue(contentAsString, Cart.class);

            cart.getCartItemMap().forEach((key, value) -> {
                log.info("itemId = {}, quantity = {}", key, value.getQuantity());
            });
            Set<CartListDTO> items = itemService.findItems(cart);
            for (CartListDTO item : items) {
                log.info("itemId = {}, itemName = {}, itemPrice = {}, itemQuantity = {}", item.getId(), item.getName(), item.getPrice(), item.getQuantity());
            }

        }



    }
}