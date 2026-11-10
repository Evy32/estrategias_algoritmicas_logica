# Estrategias algorítmicas

> **Autor:** _<Vanessa Bernal>_

Implementación en Java 17 de los ejercicios de la presentación *Lógica II – Estrategias algorítmicas*: divide y vencerás, programación dinámica y algoritmos voraces.

## Contenido

| Clase | Técnica | Problema |
|---|---|---|
| [`Inversions`](src/Inversions.java) | Divide y vencerás | Número de inversiones de una secuencia (merge sort) |
| [`ChangeDP`](src/ChangeDP.java) | Programación dinámica | Cambio de monedas con denominaciones 1, 3, 4 |
| [`ChangeGreedy`](src/ChangeGreedy.java) | Voraz | Cambio de monedas con denominaciones 1, 5, 10 (enfoque aritmético y voraz) |
| [`Tiempos`](src/Tiempos.java) | — | Tiempo de ejecución de cada algoritmo con datos mock grandes |

## Cómo ejecutar

```bash
javac -d out src/*.java
```

```bash
echo "5 2 3 9 2 9" | java -cp out Inversions
```

```bash
java -cp out ChangeDP
```

```bash
java -cp out ChangeGreedy
```

`ChangeDP` y `ChangeGreedy` aceptan valores propios como argumentos (`java -cp out ChangeDP 7 100`). `java -cp out Inversions stress` corre la prueba de estrés (versión ingenua vs. merge sort) hasta encontrar una diferencia; se detiene con `Ctrl+C`.

## Ejecución

```
$ echo "5 2 3 9 2 9" | java -cp out Inversions
2

$ java -cp out ChangeDP
money = 2 -> monedas = 2
money = 34 -> monedas = 9

$ java -cp out ChangeGreedy
m = 2 -> aritmética = 2, voraz = 2
m = 28 -> aritmética = 6, voraz = 6
```

Los resultados coinciden con los *samples* de la presentación. Además se verificó que:

- La prueba de estrés de `Inversions` corrió 5 000 arreglos aleatorios sin diferencias entre la versión `O(n²)` y la `O(n log n)`.
- En `ChangeGreedy` los dos enfoques dan el mismo resultado para todo `m` entre 1 y 1 000.

## Tiempos de ejecución

```bash
java -cp out Tiempos
```

Mide el tiempo promedio de cada algoritmo con datos mock (arreglos aleatorios con semilla fija 42, valores entre 1 y 10⁹). Antes de medir, cada caso se calienta 200 ms para que el JIT no distorsione el resultado. Los tiempos son orientativos y cambian entre máquinas.

```
1. Divide y vencerás: número de inversiones
  merge sort   O(n log n), n = 100.000            5,621,215 ns         5.621 ms   resultado = 2,501,817,025
  merge sort   O(n log n), n = 1.000.000         68,255,929 ns        68.256 ms   resultado = 250,123,851,010
  fuerza bruta O(n²),      n = 100.000        2,309,951,125 ns     2,309.951 ms   resultado = 2,501,817,025

2. Programación dinámica: cambio con monedas 1, 3, 4
  money = 1.000      O(money·k)                       2,146 ns         0.002 ms   resultado = 250
  money = 1.000.000  O(money·k)                   1,581,191 ns         1.581 ms   resultado = 250,000

3. Voraz: cambio con monedas 1, 5, 10
  aritmética m = 1.000.000      O(k)                     35 ns         0.000 ms   resultado = 100,000
  voraz      m = 1.000.000      O(m)                 24,797 ns         0.025 ms   resultado = 100,000
  voraz      m = 1.000.000.000  O(m)             26,088,791 ns        26.089 ms   resultado = 100,000,000
```

Lectura:

- **Divide y vencerás:** con n = 100 000, merge sort es unas **400 veces** más rápido que la fuerza bruta (5,6 ms vs. 2,3 s) y ambos dan el mismo resultado. Al multiplicar n por 10, merge sort tarda unas 12 veces más (`n log n`); la fuerza bruta tardaría unas 100 veces más (`n²`).
- **Programación dinámica:** crece en línea recta con `money` (×1 000 en datos ≈ ×700 en tiempo) y además reserva un arreglo de `money + 1` enteros (~4 MB para 1 000 000).
- **Voraz:** el enfoque aritmético es prácticamente instantáneo (`O(k)`, no depende de `m`). El bucle voraz es `O(m)` porque suma una moneda de 10 a la vez, pero sin memoria extra.

## Tabla comparativa

| Criterio | Divide y vencerás | Programación dinámica | Voraz |
|---|---|---|---|
| **Idea** | Divide el problema en subproblemas **independientes**, los resuelve recursivamente y combina las soluciones. | Resuelve subproblemas **que se repiten** una sola vez y guarda sus resultados (tabla o memoización) para construir la solución. | En cada paso toma la opción que **parece mejor en ese momento** y nunca la reconsidera. |
| **¿Garantiza el óptimo?** | Sí, si la combinación es correcta. | Sí, si el problema tiene subestructura óptima. | Solo si el problema tiene la *propiedad voraz* (p. ej. monedas 1, 5, 10). Con 1, 3, 4 falla: para 6 da 4+1+1 = 3 monedas, el óptimo es 3+3 = 2. |
| **Eficiencia (tiempo)** | Recurrencia `T(n) = a·T(n/b) + f(n)`; suele dar `O(n log n)`. Inversiones: `O(n log n)` vs. `O(n²)` de fuerza bruta. | Número de subproblemas × costo de cada uno. Cambio de monedas: `O(money · k)` (k = número de denominaciones). | La más rápida: un recorrido de decisiones. Cambio de monedas: `O(k)` con aritmética, `O(m)` en el peor caso con el bucle voraz. |
| **Memoria (espacio)** | Pila de recursión `O(log n)` + estructuras de combinación. Inversiones: `O(n)` por el arreglo `aux`. | La más costosa: la tabla de resultados. Cambio de monedas: `O(money)` por `minCoins`. | La mínima: `O(1)`, solo contadores. |
| **Subproblemas** | No se solapan. | Se solapan (por eso conviene guardarlos). | No se exploran; se elige uno por paso. |
| **Ventajas** | Paraleliza bien, aprovecha la caché, simplifica problemas difíciles. | Evita recalcular; convierte soluciones exponenciales en polinomiales. | Simple de implementar, rápido y con poca memoria. |
| **Inconvenientes** | Overhead de la recursión; ineficiente si los subproblemas se repiten. | Requiere memoria extra y identificar bien el estado y la recurrencia. | No siempre da el óptimo; hay que demostrar que la elección local es segura. |
| **Ejemplos** | Merge sort, quicksort, búsqueda binaria, número de inversiones. | Fibonacci, mochila 0/1, cambio de monedas general, distancia de edición. | Cambio con monedas canónicas, Dijkstra, Prim, Kruskal, Huffman. |
# estrategias_algoritmicas_logica
