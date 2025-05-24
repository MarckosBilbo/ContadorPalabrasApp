package org.example;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Utilidad que, dado un mapa <palabra, frecuencia>,
 * devuelve las N palabras más frecuentes ordenadas
 * primero por frecuencia descendente y después por orden alfabético.
 */
public class AnalizadorFrecuencias {

    public List<Entry<String, Integer>> obtenerTopFrecuentes(Map<String, Integer> contador,
                                                             int topN) {
        return contador.entrySet()
                .stream()
                /* frecuencia ↓  y  luego alfabético ↑ */
                .sorted(Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Entry.comparingByKey()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}
