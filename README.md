# [Ejercicio Parctica Final Enunciado 2.2]

## Contador de Palabras – Adaptación de C++/oneTBB a Java

ENLACE AL REPO --> https://github.com/MarckosBilbo/ContadorPalabrasApp

## Participantes del proyecto

- José Daniel Martín
- Hugo Sánchez Gallego
- Fernando Santamaría
- Marcos García Benito


## 1. Objetivo original

> *Procesar todos los `.txt` de un directorio (≥ 10 000 palabras c/u),  
> contar el total de palabras y el **Top-N** más frecuente (por archivo y global),  
> **paralelizar** con Intel oneTBB (`parallel_for`, `parallel_reduce`, …),  
> sin usar `std::thread` ni librerías de paralelismo externas.*

---

## 2. Reto al portarlo a Java 17+

* **Java no dispone de oneTBB**.
* El enunciado seguía prohibiendo “hilos manuales”, por lo que tampoco podíamos
  emplear `Thread`, `ExecutorService` ni frameworks externos.

---

## 3. Equivalencias Java ↔ oneTBB

| C++ / oneTBB                              | Java estándar                            | Descripción |
|-------------------------------------------|------------------------------------------|-------------|
| `tbb::parallel_for`                       | `Files.list(dir).parallel()`             | Distribuye los archivos entre *tasks* del **Fork-Join Pool** común. |
| `tbb::parallel_reduce`, `tbb::combinable` | `Files.lines(path).parallel()` + `ConcurrentHashMap.merge()` | Reducción concurrente de palabras por línea. |
| `concurrent_hash_map`                     | `ConcurrentHashMap`, `LongAdder`         | Estructuras *thread-safe* incluidas en la JDK. |
| Control de paralelismo                    | Propiedad JVM `java.util.concurrent.ForkJoinPool.common.parallelism` | Sin tocar código fuente. |

---

## 4. Arquitectura resultante

Main
├─ lee flags (--top, --mode, --dir)
├─ ProcesadorArchivos
│ ├─ Stream<Path>.parallel() ← distribuye archivos
│ └─ para cada archivo
│ ├─ Files.lines().parallel() ← distribuye líneas
│ └─ ConcurrentHashMap.merge()
└─ imprime resultados y métricas


* **Doble nivel de paralelismo**: entre archivos y dentro de cada archivo.
* Sin dependencias externas: solo JDK.

---

## 5. Métricas de rendimiento (ejemplo)

| Métrica | Valor |
|---------|-------|
| Suma de duraciones individuales | **166 ms** |
| Tiempo real de la aplicación (paralelo) | **51 ms** |
| Aceleración ≈ | **× 3,3** |

La diferencia demuestra que los archivos se procesaron simultáneamente.

---

## 6. Ventajas del enfoque

* **Portabilidad** total (cualquier JVM ≥ 11).
* Código conciso: sólo *parallel streams* + colecciones concurrentes.
* Escalabilidad automática a los núcleos disponibles.
* Se cumple la restricción de *no usar hilos manuales*.

---

## 7. Limitaciones & mejoras

1. **E/S de disco** puede ser el cuello de botella → usar buffering o NIO.
2. Para archivos gigantes, particionar por bloques en lugar de por línea.
3. Flag `--threads` que ajuste la paralelidad (`ForkJoinPool` común).
4. Filtrado de *stop-words* o normalización de acentos para análisis lingüístico más fino.

---

## 8. Conclusión

La práctica demuestra que puede lograrse el mismo paralelismo exigido para C++/oneTBB **sin** hilos explícitos y **sin** librerías externas, aprovechando únicamente las utilidades de concurrencia que vienen de serie en Java.
