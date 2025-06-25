package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.dto.CategoryDTO;
import com.myTMS.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CategoryRestController {
    private final CategoryService categoryService;

    @PostMapping("/sort/category")
    public ResponseEntity<List<Long>> getCategory(@RequestBody List<Long> arrayData) {
        List<Long> list = categoryService.getSortedCategories(arrayData)
                .stream()
                .map(CategoryDTO::getId).toList();

        return ResponseEntity.ok(list);
    }
}
