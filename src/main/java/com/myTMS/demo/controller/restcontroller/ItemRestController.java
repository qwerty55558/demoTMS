package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.dto.ItemListDTO;
import com.myTMS.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemRestController {
    private final ItemService itemService;


    /**
     * 인피니티 스크롤의 페이징을 구현하는 API
     * @param page offset 값으로 사용됨
     * @return 페이징된 데이터를 리스트에 담아 DTO 로 전송, offset 값이 넘어서면 NO_CONTENT 리턴하여 요청을 막음
     */
    @GetMapping("/item")
    public ResponseEntity<List<ItemListDTO>> getItemList(@RequestParam("page") int page) {
        List<ItemListDTO> items = itemService.findItems(page, 3);
        if (items.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(items);
    }
}
