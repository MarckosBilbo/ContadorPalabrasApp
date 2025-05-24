package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Clase encargada de contar palabras en un archivo.
 * Incluye versión secuencial y paralela.
 */
public class ContadorPalabras {

    /* Regex que separa por todo lo que no sea letra ni acentos españoles. */
    private static final Pattern PATRON_SEPARADOR =
            Pattern.compile("[^a-zA-ZáéíóúÁÉÍÓÚñÑ]+");

    /** ------------ Versión SECUENCIAL (tal cual tenías) ------------ */
    public Map<String, Integer> contarPalabras(Path archivo) throws IOException {
        Map<String, Integer> contador = new HashMap<>();

        try (Stream<String> lineas = Files.lines(archivo)) {
            lineas.forEach(linea -> {
                String[] palabras = PATRON_SEPARADOR.split(linea.toLowerCase());
                for (String palabra : palabras) {
                    if (!palabra.isBlank()) {
                        contador.merge(palabra, 1, Integer::sum);
                    }
                }
            });
        }
        return contador;
    }

    /** ------------ Versión PARALELA ------------ */
    public Map<String, Integer> contarPalabrasParalelo(Path archivo) throws IOException {
        /* ConcurrentHashMap permite updates seguros desde varios threads. */
        ConcurrentHashMap<String, Integer> contador = new ConcurrentHashMap<>();

        /* `Files.lines(...).parallel()` crea un stream en paralelo:   *
         *   – El fork-join pool divide las líneas entre subtareas.    */
        try (Stream<String> lineas = Files.lines(archivo).parallel()) {
            lineas
                    /* convertimos la línea en palabras (Stream<String>)   */
                    .flatMap(l -> Arrays.stream(
                            PATRON_SEPARADOR.split(l.toLowerCase())))
                    .filter(p -> !p.isBlank())
                    /* merge atómico: si la clave existe, suma 1; si no, pone 1 */
                    .forEach(p -> contador.merge(p, 1, Integer::sum));
        }
        return contador;
    }
}
