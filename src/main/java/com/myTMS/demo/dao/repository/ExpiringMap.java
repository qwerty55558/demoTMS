package com.myTMS.demo.dao.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpiringMap<K extends Comparable<K>, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public ExpiringMap(int maxSize) {
        super(maxSize, 1, false); // insertion order 유지
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

    public List<Map.Entry<K, V>> getEntriesAfter(K key) {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        boolean found = false;
        for (Map.Entry<K, V> entry : this.entrySet()) {
            if (!found && entry.getKey().compareTo(key) > 0) {
                found = true;
            }
            if (found) {
                result.add(entry);
            }
        }
        return result;
    }
}



