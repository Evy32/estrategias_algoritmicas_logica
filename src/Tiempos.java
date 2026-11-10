import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Imprime el tiempo promedio y la memoria reservada por ejecución de cada algoritmo con datos mock grandes.
 * Cada caso se calienta antes de medir para que el JIT no distorsione el resultado;
 * aun así los tiempos son orientativos y cambian entre máquinas y ejecuciones.
 *
 * Ejecutar:  java Tiempos
 */
public final class Tiempos {
    private Tiempos() { }

    private static final long CALENTAMIENTO_MS = 200;

    // Bytes reservados en el heap por este hilo: mide lo que pide cada algoritmo, sin depender del GC.
    // shortcut: no cuenta la pila de recursión (merge sort usa O(log n) marcos), usar un profiler si hace falta.
    private static final ThreadMXBean HILO = (ThreadMXBean) ManagementFactory.getThreadMXBean();

    // Semilla fija: mismos datos en cada ejecución. Rango 1..10^9 como en la restricción del ejercicio.
    private static final int[] MOCK_100K = new Random(42).ints(100_000, 1, 1_000_000_001).toArray();
    private static final int[] MOCK_1M = new Random(42).ints(1_000_000, 1, 1_000_000_001).toArray();

    public static void main(String[] args) {
        System.out.printf("Tiempo y memoria (heap) promedio por ejecución (datos mock, semilla 42, %d ms de calentamiento)%n", CALENTAMIENTO_MS);

        // clone(): getNumberOfInversions ordena el arreglo, sin copia la siguiente repetición mediría un arreglo ya ordenado.
        // La copia suma 4n bytes a la memoria medida de merge sort (además de los 4n de aux).
        System.out.println(System.lineSeparator() + "1. Divide y vencerás: número de inversiones");
        medir("merge sort   O(n log n), n = 100.000", 50, () -> Inversions.getNumberOfInversions(MOCK_100K.clone()));
        medir("merge sort   O(n log n), n = 1.000.000", 10, () -> Inversions.getNumberOfInversions(MOCK_1M.clone()));
        medir("fuerza bruta O(n²),      n = 100.000", 1, () -> Inversions.getNumberOfInversionsNaive(MOCK_100K));

        System.out.println(System.lineSeparator() + "2. Programación dinámica: cambio con monedas 1, 3, 4");
        medir("money = 1.000      O(money·k)", 1_000, () -> ChangeDP.getChange(1_000));
        medir("money = 1.000.000  O(money·k)", 20, () -> ChangeDP.getChange(1_000_000));

        System.out.println(System.lineSeparator() + "3. Voraz: cambio con monedas 1, 5, 10");
        medir("aritmética m = 1.000.000      O(k)", 1_000, () -> ChangeGreedy.getChange(1_000_000));
        medir("voraz      m = 1.000.000      O(m)", 100, () -> ChangeGreedy.getChangeGreedy(1_000_000));
        medir("voraz      m = 1.000.000.000  O(m)", 3, () -> ChangeGreedy.getChangeGreedy(1_000_000_000));
    }

    private static void medir(String caso, int repeticiones, Supplier<Object> algoritmo) {
        // Calentar por tiempo y no por número de llamadas; siempre corre al menos una vez.
        final long finCalentamiento = System.nanoTime() + CALENTAMIENTO_MS * 1_000_000;
        Stream.generate(algoritmo).takeWhile(r -> System.nanoTime() < finCalentamiento).count();
        final long memoriaInicio = HILO.getCurrentThreadAllocatedBytes();
        final long inicio = System.nanoTime();
        final Object resultado = IntStream.range(0, repeticiones).mapToObj(i -> algoritmo.get()).reduce((a, b) -> b).orElseThrow();
        final long promedio = (System.nanoTime() - inicio) / repeticiones;
        final long memoria = (HILO.getCurrentThreadAllocatedBytes() - memoriaInicio) / repeticiones;
        System.out.printf("  %-40s %,16d ns  %,12.3f ms  %,12.1f KB   resultado = %,d%n",
                caso, promedio, promedio / 1e6, memoria / 1024.0, resultado);
    }
}
