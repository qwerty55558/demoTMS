package com.myTMS.demo.service;

import com.myTMS.demo.dao.Category;
import com.myTMS.demo.dto.CategoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Test
    void getCategories() {
        List<CategoryDTO> allCategories = categoryService.getAllCategories();
        for (CategoryDTO allCategory : allCategories) {
            log.info("categoryID = {}", allCategory.getId());
            log.info("categoryName = {}", allCategory.getName());
            log.info("categoryParent = {}", allCategory.getParentId());
        }
    }
}