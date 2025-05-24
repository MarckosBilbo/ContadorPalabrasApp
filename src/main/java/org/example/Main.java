package org.example;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main que reconoce argumentos sueltos o con nombre.
 *
 * Sintaxis admitida (cualquier orden):
 *   --top <N>        → número de palabras del TOP (entero positivo)
 *   --mode <sec|par> → sec = secuencial, par = paralelo
 *   --dir  <carpeta> → carpeta que contiene los .txt   (opcional)
 *
 * Ejemplos:
 *   java -jar app.jar                    (usa valores por defecto)
 *   java -jar app.jar --top 50           (TOP-50 paralelo)
 *   java -jar app.jar 80 sec             (TOP-80 secuencial)
 *   java -jar app.jar par 100 --dir datos
 */
public class Main {

    public static void main(String[] args) {

        /* ---------- 1.  Valores por defecto ---------- */
        int     topN      = 20;
        boolean paralelo  = true;             // true = par, false = sec
        String  directorio = "archivos";

        /* ---------- 2.  Analizar argumentos ---------- */
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            /* --top 30  /  30  */
            if (arg.equalsIgnoreCase("--top")) {
                if (++i >= args.length) { error("--top necesita un número"); return; }
                topN = parseEntero(args[i], "El valor de --top debe ser entero positivo");
            } else if (maybeNumero(arg)) {
                topN = parseEntero(arg, "El argumento numérico debe ser un entero.");
            }

            /* --mode par  /  par  /  sec */
            else if (arg.equalsIgnoreCase("--mode")) {
                if (++i >= args.length) { error("--mode necesita sec o par"); return; }
                paralelo = parseModo(args[i]);
            } else if (arg.equalsIgnoreCase("par") || arg.equalsIgnoreCase("sec")) {
                paralelo = parseModo(arg);
            }

            /* --dir textos  */
            else if (arg.equalsIgnoreCase("--dir")) {
                if (++i >= args.length) { error("--dir necesita un nombre de carpeta"); return; }
                directorio = args[i];
            }

            /* Argumento no reconocido → aviso, pero no abortamos */
            else {
                System.err.println("Aviso: se ignora argumento desconocido '" + arg + "'");
            }
        }

        /* ---------- 3.  Procesar archivos ---------- */
        ProcesadorArchivos procesador =
                new ProcesadorArchivos(Paths.get(directorio).toString());

        try {
            long inicio = System.currentTimeMillis();

            List<Resultado> resultados = paralelo
                    ? procesador.procesarParalelamente(topN)
                    : procesador.procesarSecuencialmente(topN);

            long fin = System.currentTimeMillis();

            /* Resultados por archivo */
            int finalTopN = topN;
            resultados.forEach(r -> r.imprimir(finalTopN));

            /* Resumen global */
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

            System.out.printf("%nTiempo de ejecución (%s): %d ms%n",
                    paralelo ? "paralelo" : "secuencial",
                    (fin - inicio));

        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }

    /* ----------  utilidades ---------- */

    private static void error(String msg) {
        System.err.println("Error: " + msg);
        System.err.println("Uso: java Main [--top N] [--mode sec|par] [--dir carpeta]");
    }

    private static int parseEntero(String s, String msgError) {
        try {
            int n = Integer.parseInt(s);
            if (n <= 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            error(msgError); System.exit(1); return -1;
        }
    }

    private static boolean parseModo(String s) {
        if (s.equalsIgnoreCase("par")) return true;
        if (s.equalsIgnoreCase("sec")) return false;
        error("Modo inválido: usa sec o par");
        System.exit(1); return true;            // nunca llega
    }

    private static boolean maybeNumero(String s) {
        return s.chars().allMatch(Character::isDigit);
    }
}
