package org.example;

import java.util.List;
import java.util.Map;

/**
 * DTO para devolver tres cosas por archivo:
 *   – nombre del archivo
 *   – nº total de palabras
 *   – top-N palabras con su frecuencia
 */
public class Resultado {

    private final String nombreArchivo;
    private final int totalPalabras;
    private final List<Map.Entry<String, Integer>> palabrasFrecuentes;

    public Resultado(String nombreArchivo,
                     int totalPalabras,
                     List<Map.Entry<String, Integer>> palabrasFrecuentes) {
        this.nombreArchivo = nombreArchivo;
        this.totalPalabras = totalPalabras;
        this.palabrasFrecuentes = palabrasFrecuentes;
    }

    /* getters */
    public String getNombreArchivo()      { return nombreArchivo; }
    public int getTotalPalabras()         { return totalPalabras; }
    public List<Map.Entry<String, Integer>> getPalabrasFrecuentes() { return palabrasFrecuentes; }

    /** Imprime un resumen legible por consola */
    public void imprimir(int topN) {
        System.out.println("Archivo: " + nombreArchivo);
        System.out.println("Total de palabras: " + totalPalabras);
        System.out.println("Top " + topN + " palabras más frecuentes:");
        for (var e : palabrasFrecuentes) {
            System.out.printf("  %s: %d%n", e.getKey(), e.getValue());
        }
        System.out.println();
    }
}
