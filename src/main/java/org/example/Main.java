package org.example;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Punto de entrada de la aplicación.
 *
 * Parámetros admitidos en cualquier orden:
 *   --top  N        → número de palabras en el TOP  (entero > 0)
 *   --mode sec|par  → modo de ejecución: secuencial o paralelo
 *   --dir  carpeta  → carpeta que contiene los archivos .txt
 *
 * Cualquier argumento que no encaje en los anteriores se ignora mostrando aviso.
 */


public class Main {

    public static void main(String[] args) {

        /* ---------------- 1. Valores por defecto ---------------- */
        int     topN       = 20;
        boolean paralelo   = true;           // true = par, false = sec
        String  directorio = "archivos";

        /* ---------------- 2. Parseo de argumentos ---------------- */
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            /* --top 50  |  50 */
            if (arg.equalsIgnoreCase("--top")) {
                if (++i >= args.length) { mostrarError("--top necesita un número"); return; }
                topN = parseEntero(args[i], "El valor de --top debe ser > 0");
            } else if (esNumero(arg)) {
                topN = parseEntero(arg, "El primer argumento numérico debe ser > 0");
            }

            /* --mode par  |  par | sec */
            else if (arg.equalsIgnoreCase("--mode")) {
                if (++i >= args.length) { mostrarError("--mode necesita sec o par"); return; }
                paralelo = parseModo(args[i]);
            } else if (arg.equalsIgnoreCase("par") || arg.equalsIgnoreCase("sec")) {
                paralelo = parseModo(arg);
            }

            /* --dir textos */
            else if (arg.equalsIgnoreCase("--dir")) {
                if (++i >= args.length) { mostrarError("--dir necesita un nombre de carpeta"); return; }
                directorio = args[i];
            }

            /* Cualquier otra cosa se ignora mostrando aviso */
            else {
                System.err.println("Aviso: se ignora argumento desconocido '" + arg + "'");
            }
        }

        /* ---------------- 3. Procesamiento ---------------- */
        ProcesadorArchivos procesador =
                new ProcesadorArchivos(Paths.get(directorio).toString());

        try {
            long appInicio = System.currentTimeMillis();

            List<Resultado> resultados = paralelo
                    ? procesador.procesarParalelamente(topN)
                    : procesador.procesarSecuencialmente(topN);

            long appFin = System.currentTimeMillis();

            /* ---- 3.1. Imprimir resultados por archivo ---- */
            int finalTopN = topN;
            resultados.forEach(r -> r.imprimir(finalTopN));

            /* ---- 3.2. Resumen global ---- */
            int totalPalabras = resultados.stream()
                    .mapToInt(Resultado::getTotalPalabras)
                    .sum();

            Map<String,Integer> global = new ConcurrentHashMap<>();
            resultados.forEach(r ->
                    r.getPalabrasFrecuentes()
                            .forEach(e -> global.merge(e.getKey(), e.getValue(), Integer::sum)));

            var topGlobal = new AnalizadorFrecuencias()
                    .obtenerTopFrecuentes(global, topN);

            System.out.println("=== RESUMEN GLOBAL ===");
            System.out.println("Total de palabras en todos los archivos: " + totalPalabras);
            topGlobal.forEach(e ->
                    System.out.printf("  %s: %d%n", e.getKey(), e.getValue()));

            /* ---- 3.3. Métricas de tiempo ---- */
            long tiempoArchivos = resultados.stream()
                    .mapToLong(Resultado::getDuracionMs)
                    .sum();
            double media = tiempoArchivos / (double) resultados.size();

            System.out.printf("%nTiempo total archivos ( suma de duraciones ): %d ms  (media %.1f ms)%n",
                    tiempoArchivos, media);

            System.out.printf("Tiempo de ejecución completo (%s): %d ms%n",
                    paralelo ? "paralelo" : "secuencial",
                    (appFin - appInicio));

        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }

    /* ---------------- utilidades internas ---------------- */

    private static void mostrarError(String msg) {
        System.err.println("Error: " + msg);
        System.err.println("Uso: java Main [--top N] [--mode sec|par] [--dir carpeta]");
    }

    private static int parseEntero(String s, String msgError) {
        try {
            int n = Integer.parseInt(s);
            if (n <= 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            mostrarError(msgError);
            System.exit(1);
            return 1; // nunca se alcanza
        }
    }

    private static boolean parseModo(String s) {
        if (s.equalsIgnoreCase("par")) return true;
        if (s.equalsIgnoreCase("sec")) return false;
        mostrarError("Modo inválido: usa sec o par");
        System.exit(1);
        return true; // nunca se alcanza
    }

    private static boolean esNumero(String s) {
        return s.chars().allMatch(Character::isDigit);
    }
}
