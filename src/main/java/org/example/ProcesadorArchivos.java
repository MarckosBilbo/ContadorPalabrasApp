package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Orquesta el procesamiento de todos los archivos *.txt* que hay en el
 * directorio 'archivos'. Ofrece métodos secuencial y paralelo.
 */
public class ProcesadorArchivos {

    private final Path directorio;
    private final ContadorPalabras contadorPalabras;
    private final AnalizadorFrecuencias analizadorFrecuencias;

    public ProcesadorArchivos(String nombreDirectorio) {
        this.directorio = Paths.get(nombreDirectorio);
        this.contadorPalabras = new ContadorPalabras();
        this.analizadorFrecuencias = new AnalizadorFrecuencias();
    }

    /* -------------- Procesamiento SECUENCIAL (igual que antes) -------------- */
    public List<Resultado> procesarSecuencialmente(int topN) throws IOException {
        try (Stream<Path> archivos = Files.list(directorio)
                .filter(p -> p.toString().endsWith(".txt"))) {
            return archivos
                    .map(archivo -> {
                        Map<String, Integer> conteo;
                        try { conteo = contadorPalabras.contarPalabras(archivo); }
                        catch (IOException e) { throw new RuntimeException(e); }

                        var top = analizadorFrecuencias.obtenerTopFrecuentes(conteo, topN);
                        int total = conteo.values().stream().mapToInt(Integer::intValue).sum();

                        return new Resultado(archivo.getFileName().toString(), total, top);
                    })
                    .collect(Collectors.toList());
        }
    }

    /* -------------- Procesamiento PARALELO ---------------------- *
     * 1)   Files.list(...).parallel() reparte los archivos (.txt)  *
     *      entre tareas del *common ForkJoinPool*.                 *
     * 2)   Cada tarea invoca contarPalabrasParalelo(), que a su    *
     *      vez procesa las líneas del archivo en paralelo.         */
    public List<Resultado> procesarParalelamente(int topN) throws IOException {
        try (Stream<Path> archivos = Files.list(directorio)
                .filter(p -> p.toString().endsWith(".txt"))
                .parallel()) {        // ← clave
            return archivos
                    .map(archivo -> {
                        Map<String, Integer> conteo;
                        try { conteo = contadorPalabras.contarPalabrasParalelo(archivo); }
                        catch (IOException e) { throw new RuntimeException(e); }

                        var top = analizadorFrecuencias.obtenerTopFrecuentes(conteo, topN);
                        int total = conteo.values().stream().mapToInt(Integer::intValue).sum();

                        return new Resultado(archivo.getFileName().toString(), total, top);
                    })
                    .collect(Collectors.toList());
        }
    }
}
