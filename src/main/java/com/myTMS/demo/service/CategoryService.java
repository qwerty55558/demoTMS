package com.myTMS.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.Category;
import com.myTMS.demo.dto.CategoryDTO;
import com.myTMS.demo.repository.interfaces.JPACategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final JPACategoryRepository categoryRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<List<Category>> getCategoriesByIds(List<Long> ids) {
        List<Category> objects = new ArrayList<>();
        for (Long id : ids) {
            categoryRepository.findById(id).ifPresent(objects::add);
        }
        return Optional.of(objects);
    }


    public Optional<Category> createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setDepth(0);
        category.setParent(null);
        categoryRepository.save(category);
        return Optional.of(category);
    }

    public Optional<Category> setChild(String name, String childName) {
        Optional<Category> byName = categoryRepository.findByName(name);
        if (byName.isPresent()) {
            Category category = byName.get();
            Category childrenName = new Category();
            childrenName.setName(childName);
            childrenName.setParent(category);
            if (category.getDepth() == null) {
                childrenName.setDepth(1);
            } else {
                childrenName.setDepth(category.getDepth() + 1);
            }
            category.getChildren().add(childrenName);
            categoryRepository.save(category);
            categoryRepository.save(childrenName);
            return Optional.of(category);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public void cacheAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> dtoList = categories.stream()
                .sorted(Comparator.comparing(Category::getDepth))
                .map(cat -> {
                    if (cat.getParent() != null) {
                        return new CategoryDTO(cat.getId(), cat.getName(), cat.getParent().getId(), cat.getDepth());
                    } else {
                        return new CategoryDTO(cat.getId(), cat.getName(), null, cat.getDepth());
                    }
                })
                .toList();
        redisService.setObjectData(RedisConst.CATEGORY_CACHE_KEY.get(), dtoList);
    }

    public List<CategoryDTO> getAllCategories() {
        String objectData = redisService.getObjectData(RedisConst.CATEGORY_CACHE_KEY.get());
        if (objectData != null) {
            try {
                return objectMapper.readValue(objectData, new TypeReference<List<CategoryDTO>>() {
                });
            } catch (IOException e) {
                log.error("Failed to deserialize categories from Redis", e);
            }
        }
        return null;
    }

    public List<CategoryDTO> getSortedCategories(List<Long> data) {
        String objectData = redisService.getObjectData(RedisConst.CATEGORY_CACHE_KEY.get());
        try {
            List<CategoryDTO> categoryDTOS = objectMapper.readValue(objectData, new TypeReference<List<CategoryDTO>>() {});
            if (!categoryDTOS.isEmpty()) {
                List<CategoryDTO> list = categoryDTOS.stream()
                        .filter(c -> data.contains(c.getId())).toList();

                int maxDepth = list.stream()
                        .mapToInt(CategoryDTO::getDepth)
                        .max()
                        .orElseThrow(() -> new IllegalStateException("No categories found"));

                return list.stream()
                        .filter(category -> category.getDepth() == maxDepth)
                        .map(category -> {
                            if (category.getParentId() != null) {
                                return new CategoryDTO(category.getId(), category.getName(), category.getParentId(), category.getDepth());
                            } else {
                                return new CategoryDTO(category.getId(), category.getName(), null, category.getDepth());
                            }
                        })
                        .toList();
            }
        } catch (JsonProcessingException e) {
            log.info("Failed to deserialize categories from Redis", e);
        }

        return Collections.emptyList();
    }
}