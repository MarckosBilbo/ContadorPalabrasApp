package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ContadorPalabras {
    private static final Pattern PATRON_SEPARADOR = Pattern.compile("[^a-zA-ZáéíóúÁÉÍÓÚñÑ]+");

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
}

