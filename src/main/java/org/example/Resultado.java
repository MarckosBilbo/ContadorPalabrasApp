package org.example;

import java.util.List;
import java.util.Map;

public class Resultado {
    private final String nombreArchivo;
    private final int totalPalabras;
    private final List<Map.Entry<String, Integer>> palabrasFrecuentes;

    public Resultado(String nombreArchivo, int totalPalabras, List<Map.Entry<String, Integer>> palabrasFrecuentes) {
        this.nombreArchivo = nombreArchivo;
        this.totalPalabras = totalPalabras;
        this.palabrasFrecuentes = palabrasFrecuentes;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public int getTotalPalabras() {
        return totalPalabras;
    }

    public List<Map.Entry<String, Integer>> getPalabrasFrecuentes() {
        return palabrasFrecuentes;
    }

    public void imprimir(int topN) {
        System.out.println("Archivo: " + nombreArchivo);
        System.out.println("Total de palabras: " + totalPalabras);
        System.out.println("Top " + topN + " palabras más frecuentes:");
        for (Map.Entry<String, Integer> entrada : palabrasFrecuentes) {
            System.out.printf("  %s: %d\n", entrada.getKey(), entrada.getValue());
        }
        System.out.println();
    }
}

