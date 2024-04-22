package com.example.hello.myMemory;

import java.util.HashMap;
import java.util.Map;

public class MemoryStatus {
    private String strategy;
    private Map<String, Object> details;

    public MemoryStatus(String strategy) {
        this.strategy = strategy;
        this.details = new HashMap<>();
    }

    public void addDetail(String key, Object value) {
        details.put(key, value);
    }

    public String getStrategy() {
        return strategy;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
