package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Se encarga de recorrer el directorio con los .txt y procesarlos
 * – bien secuencialmente, bien en paralelo – devolviendo una lista de
 * objetos {@link Resultado}.
 */
public class ProcesadorArchivos {

    private final Path directorio;                       // carpeta que contiene los .txt
    private final ContadorPalabras contadorPalabras;     // lógica de conteo de palabras
    private final AnalizadorFrecuencias analizador;      // obtiene el TOP-N

    public ProcesadorArchivos(String nombreDirectorio) {
        this.directorio        = Paths.get(nombreDirectorio);
        this.contadorPalabras  = new ContadorPalabras();
        this.analizador        = new AnalizadorFrecuencias();
    }

    /* ------------------------------------------------------------------
     *  VERSIÓN SECUENCIAL
     * ------------------------------------------------------------------ */
    public List<Resultado> procesarSecuencialmente(int topN) throws IOException {
        try (Stream<Path> archivos = Files.list(directorio)
                .filter(p -> p.toString().endsWith(".txt"))) {

            return archivos.map(archivo -> {
                long inicio = System.nanoTime();              // marca de tiempo
                Map<String,Integer> conteo;
                try {
                    conteo = contadorPalabras.contarPalabras(archivo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long duracion = (System.nanoTime() - inicio) / 1_000_000; // ns → ms

                var topPalabras = analizador.obtenerTopFrecuentes(conteo, topN);
                int total = conteo.values().stream().mapToInt(Integer::intValue).sum();

                return new Resultado(archivo.getFileName().toString(),
                        total, topPalabras, duracion);
            }).collect(Collectors.toList());
        }
    }

    /* ------------------------------------------------------------------
     *  VERSIÓN PARALELA
     * ------------------------------------------------------------------ */
    public List<Resultado> procesarParalelamente(int topN) throws IOException {
        try (Stream<Path> archivos = Files.list(directorio)
                .filter(p -> p.toString().endsWith(".txt"))
                .parallel()) {

            return archivos.map(archivo -> {
                long inicio = System.nanoTime();
                Map<String,Integer> conteo;
                try {
                    conteo = contadorPalabras.contarPalabrasParalelo(archivo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long duracion = (System.nanoTime() - inicio) / 1_000_000;

                var topPalabras = analizador.obtenerTopFrecuentes(conteo, topN);
                int total = conteo.values().stream().mapToInt(Integer::intValue).sum();

                return new Resultado(archivo.getFileName().toString(),
                        total, topPalabras, duracion);
            }).collect(Collectors.toList());
        }
    }
}
