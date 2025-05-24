package org.example;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AnalizadorFrecuencias {

    public List<Entry<String, Integer>> obtenerTopFrecuentes(Map<String, Integer> contador, int topN) {
        return contador.entrySet()
                .stream()
                .sorted(Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Entry.comparingByKey()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}

