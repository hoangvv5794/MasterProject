package com.viettel.vtcc.master.data.test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestMapSorted {
    private static final Map<String, Long> unsortMap = new HashMap<>();

    public static void main(String[] args) {
        unsortMap.put("3", 3L);
        unsortMap.put("5", 5L);
        unsortMap.put("1", 1L);

        List<String> sorted = unsortMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }
}
