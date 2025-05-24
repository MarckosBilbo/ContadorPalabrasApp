package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Punto de entrada.  Ejecución:
 *
 *   java -jar contador.jar <N> <modo>
 *
 *   <N>    = nº de palabras del TOP (p.e. 20)
 *   <modo> = sec | par   → secuencial o paralelo
 */
public class Main {

    public static void main(String[] args) {
        /* Validamos argumentos ------------------------------------------------ */
        if (args.length != 2) {
            System.err.println("Uso: java Main <N> <modo>");
            System.err.println("       <modo>: sec | par");
            System.exit(1);
        }
        int topN;
        try { topN = Integer.parseInt(args[0]); }
        catch (NumberFormatException e) {
            System.err.println("El argumento N debe ser un entero.");
            return;
        }
        boolean paralelo = args[1].equalsIgnoreCase("par");

        /* Lanzamos procesamiento --------------------------------------------- */
        String directorio = "archivos";
        ProcesadorArchivos procesador = new ProcesadorArchivos(directorio);

        try {
            long inicio = System.currentTimeMillis();

            List<Resultado> resultados = paralelo
                    ? procesador.procesarParalelamente(topN)
                    : procesador.procesarSecuencialmente(topN);

            long fin = System.currentTimeMillis();

            /* Resultados por archivo ----------------------------------------- */
            resultados.forEach(r -> r.imprimir(topN));

            /* Resumen global -------------------------------------------------- */
            int totalPalabras = resultados.stream()
                    .mapToInt(Resultado::getTotalPalabras)
                    .sum();

            Map<String, Integer> conteoGlobal = new ConcurrentHashMap<>();
            resultados.forEach(r ->
                    r.getPalabrasFrecuentes()
                            .forEach(e -> conteoGlobal.merge(e.getKey(), e.getValue(), Integer::sum)));

            AnalizadorFrecuencias analizador = new AnalizadorFrecuencias();
            var topGlobal = analizador.obtenerTopFrecuentes(conteoGlobal, topN);

            System.out.println("=== RESUMEN GLOBAL ===");
            System.out.println("Total de palabras en todos los archivos: " + totalPalabras);
            topGlobal.forEach(e ->
                    System.out.printf("  %s: %d%n", e.getKey(), e.getValue()));

            System.out.printf("%nTiempo de ejecución (%s): %d ms%n",
                    paralelo ? "paralelo" : "secuencial",
                    (fin - inicio));

        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }
}
