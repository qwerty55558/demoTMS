package com.myTMS.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.*;
import com.myTMS.demo.dto.CartListDTO;
import com.myTMS.demo.dto.ItemListDTO;
import com.myTMS.demo.repository.interfaces.JPACategoryItemRepository;
import com.myTMS.demo.repository.interfaces.JPACategoryRepository;
import com.myTMS.demo.repository.interfaces.JPAItemRepository;
import com.myTMS.demo.repository.interfaces.JPAOrdersItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final JPAItemRepository itemRepository;
    private final JPACategoryRepository categoryRepository;
    private final JPACategoryItemRepository categoryItemRepository;
    private final JPAOrdersItemRepository ordersItemRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public Boolean createItem(String name, Object description, Integer price) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        if (description instanceof String) {
            item.setDescription(String.valueOf(description));
            itemRepository.save(item);
        } else {
            return false;
        }
        return true;
    }

    public Boolean setImg(String name, String imgPath) {
        Optional<Item> item = findItem(name);
        if (item.isPresent()) {
            Item optItem = item.get();
            optItem.setImgSrc(imgPath);
            itemRepository.save(optItem);
            return true;
        }
        return false;
    }

    public Boolean setCategory(String itemName, String categoryName) {
        try {
            CategoryItem categoryItem = new CategoryItem();
            Optional<Category> optCategory = categoryRepository.findByName(categoryName);
            optCategory.ifPresent(categoryItem::setCategory);
            Optional<Item> optItem = itemRepository.findByName(itemName);
            optItem.ifPresent(categoryItem::setItem);
            categoryItemRepository.save(categoryItem);
        } catch (Exception e) {
            log.error("Error Setting Category to Item: ", e);
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Item> findItem(String itemName) {
        return itemRepository.findByName(itemName);
    }

    public void cacheAllItems() {
        List<Item> all = itemRepository.findAll();
        List<ItemListDTO> list = all.stream()
                .map(item -> {
                    return new ItemListDTO(item.getId(), item.getName(), item.getImgSrc(), item.getCategory().getFirst().getCategory().getId(), item.getDescription(), item.getPrice());
                }).toList();
        redisService.setObjectData(RedisConst.ITEM_CACHE_KEY.get(), list);
    }

    @Transactional(readOnly = true)
    public List<ItemListDTO> findItems() {
        String objectData = redisService.getObjectData(RedisConst.ITEM_CACHE_KEY.get());
        try {
            return objectMapper.readValue(objectData, new TypeReference<List<ItemListDTO>>() {
            });
        } catch (Exception e) {
            log.info("Error Reading Item List: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ItemListDTO> findItems(int pageNum, int pageSize) {
        String objectData = redisService.getObjectData(RedisConst.ITEM_CACHE_KEY.get());

        try {
            List<ItemListDTO> itemListDTOS = objectMapper.readValue(objectData, new TypeReference<List<ItemListDTO>>() {
            });

            int size = itemListDTOS.size();
            int start = pageNum * pageSize;
            int end = Math.min(start + pageSize, size);

            if (start >= size) {
                return Collections.emptyList();
            }

            return itemListDTOS.subList(start, end);
        } catch (Exception e) {
            log.error("Error Reading Item PageList: ", e);
        }

        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public Set<CartListDTO> findItems(Cart cart) {
        List<ItemListDTO> items = findItems();
        Set<CartListDTO> objects = new HashSet<>();

        cart.getCartItemMap().values().forEach(cartItem -> {
            items.stream()
                    .filter(item -> item.getId().equals(cartItem.getItemId()))
                    .forEach(item -> {
                        objects.add(new CartListDTO(item.getId(), item.getName(), item.getImgSrc(), item.getDescription(), item.getPrice(), cartItem.getQuantity()));
                    });
        });

        return objects;
    }
}
