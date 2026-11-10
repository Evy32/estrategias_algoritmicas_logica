import java.util.Arrays;

/**
 * Algoritmo voraz: mínimo de monedas para dar `m` con denominaciones 1, 5 y 10.
 * Se comparan los dos enfoques de la presentación.
 *
 * Ejecutar:  java ChangeGreedy          -> casos de ejemplo de la diapositiva
 *            java ChangeGreedy 7 99     -> valores propios
 */
public class ChangeGreedy {

    // Enfoque 1: aritmética básica (división entera y residuo).
    static int getChange(int m) {
        int count = m / 10;
        m = m % 10;
        count += m / 5;
        m = m % 5;
        count += m / 1;
        return count;
    }

    // Enfoque 2: voraz, siempre toma la denominación más grande que aún cabe.
    static int getChangeGreedy(int m) {
        int changes = 0, count = 0;
        int[] denominations = {1, 5, 10};
        int max = denominations.length - 1; // index pointing to next max denominations

        while (changes < m) {
            while (changes <= m && (m - changes) >= denominations[max]) {
                changes += denominations[max];
                count++;
            }
            max = max - 1;
        }
        return count;
    }

    public static void main(String[] args) {
        // Casos de la diapositiva: 2 -> 2, 28 -> 6
        String[] inputs = args.length > 0 ? args : new String[]{"2", "28"};
        Arrays.stream(inputs)
              .mapToInt(Integer::parseInt)
              .forEach(m -> System.out.println("m = " + m
                      + " -> aritmética = " + getChange(m)
                      + ", voraz = " + getChangeGreedy(m)));
    }
}
