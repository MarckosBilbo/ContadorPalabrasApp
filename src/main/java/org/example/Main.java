package org.example;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java Main <N>");
            System.exit(1);
        }

        int topN;
        try {
            topN = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("El argumento N debe ser un número entero.");
            return;
        }

        String directorio = "archivos";
        ProcesadorArchivos procesador = new ProcesadorArchivos(directorio);

        try {
            long inicio = System.currentTimeMillis();

            List<Resultado> resultados = procesador.procesarSecuencialmente(topN);

            long fin = System.currentTimeMillis();

            // Mostrar resultados por archivo
            for (Resultado resultado : resultados) {
                resultado.imprimir(topN);
            }

            // Mostrar resumen total
            int totalPalabras = resultados.stream().mapToInt(Resultado::getTotalPalabras).sum();
            Map<String, Integer> globalConteo = new HashMap<>();
            for (Resultado resultado : resultados) {
                for (var entrada : resultado.getPalabrasFrecuentes()) {
                    globalConteo.merge(entrada.getKey(), entrada.getValue(), Integer::sum);
                }
            }

            AnalizadorFrecuencias analizador = new AnalizadorFrecuencias();
            List<Map.Entry<String, Integer>> topGlobal = analizador.obtenerTopFrecuentes(globalConteo, topN);

            System.out.println("=== RESUMEN GLOBAL ===");
            System.out.println("Total de palabras en todos los archivos: " + totalPalabras);
            System.out.println("Top " + topN + " palabras más frecuentes globales:");
            for (var entrada : topGlobal) {
                System.out.printf("  %s: %d\n", entrada.getKey(), entrada.getValue());
            }

            System.out.printf("\nTiempo de ejecución (secuencial): %d ms\n", (fin - inicio));

        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }
}
