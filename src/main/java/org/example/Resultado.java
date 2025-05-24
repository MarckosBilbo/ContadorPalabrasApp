package org.example;

import java.util.List;
import java.util.Map;

/**
 * Estructura de datos que agrupa la información obtenida para un archivo:
 *   • nombre del archivo
 *   • número total de palabras
 *   • lista TOP-N con sus frecuencias
 *   • tiempo empleado en procesarlo (milisegundos)
 *
 * El método {@code imprimir()} se encarga de mostrar el contenido por consola.
 */
public class Resultado {

    private final String nombreArchivo;
    private final int totalPalabras;
    private final List<Map.Entry<String, Integer>> palabrasFrecuentes;
    private final long duracionMs;  // tiempo de procesamiento de este archivo

    /**
     * @param nombreArchivo      nombre del fichero analizado
     * @param totalPalabras      total de palabras encontradas
     * @param palabrasFrecuentes lista de las N palabras más frecuentes
     * @param duracionMs         tiempo que tardó en procesarse (ms)
     */
    public Resultado(String nombreArchivo,
                     int totalPalabras,
                     List<Map.Entry<String, Integer>> palabrasFrecuentes,
                     long duracionMs) {
        this.nombreArchivo     = nombreArchivo;
        this.totalPalabras     = totalPalabras;
        this.palabrasFrecuentes = palabrasFrecuentes;
        this.duracionMs        = duracionMs;
    }

    /* GETTERS */
    public String getNombreArchivo() { return nombreArchivo; }
    public int    getTotalPalabras() { return totalPalabras; }
    public List<Map.Entry<String,Integer>> getPalabrasFrecuentes() { return palabrasFrecuentes; }
    public long   getDuracionMs()    { return duracionMs; }

    /**
     * Imprime un resumen legible, indicando también la duración.
     * @param topN número de palabras que se muestran en el TOP
     */
    public void imprimir(int topN) {
        System.out.println("=========================================");
        System.out.println("Archivo: " + nombreArchivo);
        System.out.println("Total de palabras: " + totalPalabras);
        System.out.println("Tiempo de procesamiento: " + duracionMs + " ms");
        System.out.println("Top " + topN + " palabras más frecuentes:");
        for (Map.Entry<String, Integer> e : palabrasFrecuentes) {
            System.out.printf("  %s: %d%n", e.getKey(), e.getValue());
        }
        System.out.println();
    }
}
