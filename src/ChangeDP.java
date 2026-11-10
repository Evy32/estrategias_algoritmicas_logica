import java.util.Arrays;

/**
 * Programación dinámica: mínimo de monedas para dar `money` con denominaciones 1, 3 y 4.
 *
 * Ejecutar:  java ChangeDP          -> casos de ejemplo de la diapositiva
 *            java ChangeDP 7 100    -> valores propios
 */
public class ChangeDP {

    static int getChange(int money) {
        int[] coins = {1, 3, 4};
        int[] minCoins = new int[money + 1];

        for (int m = 1; m <= money; m++) {
            minCoins[m] = Integer.MAX_VALUE;
            for (int j = 0; j < coins.length; j++) {
                if (m >= coins[j]) {
                    int numCoins = minCoins[m - coins[j]] + 1;
                    minCoins[m] = Math.min(minCoins[m], numCoins);
                }
            }
        }

        return minCoins[money];
    }

    public static void main(String[] args) {
        // Casos de la diapositiva: 2 -> 2, 34 -> 9
        String[] inputs = args.length > 0 ? args : new String[]{"2", "34"};
        Arrays.stream(inputs)
              .mapToInt(Integer::parseInt)
              .forEach(money -> System.out.println("money = " + money + " -> monedas = " + getChange(money)));
    }
}
