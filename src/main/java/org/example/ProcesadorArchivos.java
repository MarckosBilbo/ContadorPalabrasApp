package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcesadorArchivos {
    private final Path directorio;
    private final ContadorPalabras contadorPalabras;
    private final AnalizadorFrecuencias analizadorFrecuencias;

    public ProcesadorArchivos(String nombreDirectorio) {
        this.directorio = Paths.get(nombreDirectorio);
        this.contadorPalabras = new ContadorPalabras();
        this.analizadorFrecuencias = new AnalizadorFrecuencias();
    }

    public List<Resultado> procesarSecuencialmente(int topN) throws IOException {
        List<Resultado> resultados = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directorio, "*.txt")) {
            for (Path archivo : stream) {
                Map<String, Integer> conteo = contadorPalabras.contarPalabras(archivo);
                var topPalabras = analizadorFrecuencias.obtenerTopFrecuentes(conteo, topN);
                Resultado resultado = new Resultado(archivo.getFileName().toString(), conteo.values().stream().mapToInt(Integer::intValue).sum(), topPalabras);
                resultados.add(resultado);
            }
        }

        return resultados;
    }
}

