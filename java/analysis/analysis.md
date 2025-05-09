# Análise de Algoritmos de Ordenação

## 1. Tabelas de Análise de Algoritmos de Ordenação

## Tempo Médio de Execução (ms)
| Algorithm     |    Serial |   2 threads |   4 threads |   8 threads |   16 threads |
|:--------------|----------:|------------:|------------:|------------:|-------------:|
| BubbleSort    | 1942.93   |   8275.03   |   8359.57   |   8608.27   |    8853.74   |
| InsertSort    |  151.153  |      1.5339 |      1.2624 |      1.4013 |       1.7397 |
| MergeSort     |    1.7262 |      1.3696 |      1.1955 |      1.1653 |       1.2311 |
| SelectionSort |  657.39   |    178.398  |     49.6806 |     19.1518 |       8.8674 |

## Speedup Médio
| Algorithm     |   Serial |   2 threads |   4 threads |   8 threads |   16 threads |
|:--------------|---------:|------------:|------------:|------------:|-------------:|
| BubbleSort    |        0 |        0.1  |        0.1  |        0.09 |         0.09 |
| InsertSort    |        0 |       48.07 |       58.06 |       63.26 |        56.32 |
| MergeSort     |        0 |        0.76 |        0.9  |        0.88 |         0.85 |
| SelectionSort |        0 |        2.51 |        7.36 |       15.69 |        31.35 |

## Eficiência Média
| Algorithm     |   Serial |   2 threads |   4 threads |   8 threads |   16 threads |
|:--------------|---------:|------------:|------------:|------------:|-------------:|
| BubbleSort    |        0 |        0.05 |        0.02 |        0.01 |         0.01 |
| InsertSort    |        0 |       24.04 |       14.51 |        7.91 |         3.52 |
| MergeSort     |        0 |        0.38 |        0.22 |        0.11 |         0.05 |
| SelectionSort |        0 |        1.26 |        1.84 |        1.96 |         1.96 |

## Menor Tempo de Execução (ms)
| Algorithm     |   Serial |   2 threads |   4 threads |   8 threads |   16 threads |
|:--------------|---------:|------------:|------------:|------------:|-------------:|
| BubbleSort    |   0.0008 |     20.8632 |     21.1761 |     15.8987 |      21.1388 |
| InsertSort    |   0.0013 |      0.233  |      0.2616 |      0.2838 |       0.2502 |
| MergeSort     |   0.0177 |      0.1816 |      0.2346 |      0.2474 |       0.2932 |
| SelectionSort |   0.1737 |      0.2814 |      0.3046 |      0.5136 |       0.9166 |

## Maior Tempo de Execução (ms)
| Algorithm     |     Serial |   2 threads |   4 threads |   8 threads |   16 threads |
|:--------------|-----------:|------------:|------------:|------------:|-------------:|
| BubbleSort    | 11481      |  43164.6    |  43085.3    |  44155.3    |   44172.4    |
| InsertSort    |  1079.59   |      4.923  |      3.9615 |      3.4534 |       4.4083 |
| MergeSort     |     9.4398 |      6.5277 |      6.7345 |      4.3035 |       3.6981 |
| SelectionSort |  2888.55   |    788.345  |    218.07   |     72.08   |      29.6077 |

## 2. Algoritmo mais rápido para cada tipo de array (Serial)
### Tipo de Array: Random
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 3.3825 ms
- **Vezes mais rápido que a média dos outros algoritmos**: 448.26x

### Tipo de Array: Sorted
- **Algoritmo mais rápido**: BubbleSort
- **Tempo médio**: 0.0154 ms
- **Vezes mais rápido que a média dos outros algoritmos**: 12125.26x

### Tipo de Array: Reverse
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 1.0629 ms
- **Vezes mais rápido que a média dos outros algoritmos**: 1001.33x

### Tipo de Array: PartiallySorted
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 1.6207 ms
- **Vezes mais rápido que a média dos outros algoritmos**: 556.36x

## 2. Algoritmo mais rápido para cada tamanho de array (Serial)
### Tamanho: 1000
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 0.0878 ms
- **Vezes mais rápido que o algoritmo mais lento**: 7.75x

### Tamanho: 10000
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 0.4170 ms
- **Vezes mais rápido que o algoritmo mais lento**: 101.17x

### Tamanho: 100000
- **Algoritmo mais rápido**: MergeSort
- **Tempo médio**: 4.6739 ms
- **Vezes mais rápido que o algoritmo mais lento**: 1237.92x

## 3. Algoritmos ordenados por benefício do paralelismo
### 1. InsertSort
- **Speedup máximo**: 473.65x
- **Melhor número de threads**: 8
- **Tamanho do array**: 100000
- **Tipo do array**: Reverse
- **Tempo serial**: 1032.7984 ms → **Tempo paralelo**: 2.1805 ms

### 2. SelectionSort
- **Speedup máximo**: 104.33x
- **Melhor número de threads**: 16
- **Tamanho do array**: 100000
- **Tipo do array**: Reverse
- **Tempo serial**: 2826.4820 ms → **Tempo paralelo**: 27.0924 ms

### 3. MergeSort
- **Speedup máximo**: 2.60x
- **Melhor número de threads**: 16
- **Tamanho do array**: 100000
- **Tipo do array**: Random
- **Tempo serial**: 9.1469 ms → **Tempo paralelo**: 3.5163 ms

### 4. BubbleSort
- **Speedup máximo**: 0.40x
- **Melhor número de threads**: 2
- **Tamanho do array**: 100000
- **Tipo do array**: Random
- **Tempo serial**: 11366.0633 ms → **Tempo paralelo**: 28351.0975 ms

## 4. Melhor caso para cada algoritmo
### Algoritmo: SelectionSort
- **Melhor caso**: Sorted (tamanho 1000)
- **Tempo**: 0.1746 ms
- **Pior caso**: Reverse (tamanho 100000)
- **Tempo do pior caso**: 2826.4820 ms
- **O melhor caso é 16186.47x mais rápido que o pior**

### Algoritmo: MergeSort
- **Melhor caso**: Sorted (tamanho 1000)
- **Tempo**: 0.0184 ms
- **Pior caso**: Random (tamanho 100000)
- **Tempo do pior caso**: 9.1469 ms
- **O melhor caso é 498.20x mais rápido que o pior**

### Algoritmo: BubbleSort
- **Melhor caso**: Sorted (tamanho 1000)
- **Tempo**: 0.0010 ms
- **Pior caso**: Random (tamanho 100000)
- **Tempo do pior caso**: 11366.0633 ms
- **O melhor caso é 11143199.33x mais rápido que o pior**

### Algoritmo: InsertSort
- **Melhor caso**: Sorted (tamanho 1000)
- **Tempo**: 0.0013 ms
- **Pior caso**: Reverse (tamanho 100000)
- **Tempo do pior caso**: 1032.7984 ms
- **O melhor caso é 770745.09x mais rápido que o pior**

## 5. Eficiência de paralelização por número de threads
### Número de threads: 2
- **InsertSort**: 24.0360 (2403.6%)
- **SelectionSort**: 1.2563 (125.6%)
- **MergeSort**: 0.3824 (38.2%)
- **BubbleSort**: 0.0496 (5.0%)

### Número de threads: 4
- **InsertSort**: 14.5145 (1451.5%)
- **SelectionSort**: 1.8401 (184.0%)
- **MergeSort**: 0.2249 (22.5%)
- **BubbleSort**: 0.0243 (2.4%)

### Número de threads: 8
- **InsertSort**: 7.9069 (790.7%)
- **SelectionSort**: 1.9608 (196.1%)
- **MergeSort**: 0.1103 (11.0%)
- **BubbleSort**: 0.0119 (1.2%)

### Número de threads: 16
- **InsertSort**: 3.5202 (352.0%)
- **SelectionSort**: 1.9595 (196.0%)
- **MergeSort**: 0.0528 (5.3%)
- **BubbleSort**: 0.0057 (0.6%)

## 6. Análise de escalabilidade com aumento de threads
### Algoritmo: SelectionSort
- **Speedup médio por número de threads**:
  - 2 threads: 2.51x
  - 4 threads: 7.36x
  - 8 threads: 15.69x
  - 16 threads: 31.35x
- **Aumento percentual do speedup ao dobrar o número de threads**:
  - 2 → 4 threads: 192.9% de aumento
  - 4 → 8 threads: 113.1% de aumento
  - 8 → 16 threads: 99.9% de aumento

### Algoritmo: MergeSort
- **Speedup médio por número de threads**:
  - 2 threads: 0.76x
  - 4 threads: 0.90x
  - 8 threads: 0.88x
  - 16 threads: 0.85x
- **Aumento percentual do speedup ao dobrar o número de threads**:
  - 2 → 4 threads: 17.6% de aumento
  - 4 → 8 threads: -1.9% de aumento
  - 8 → 16 threads: -4.2% de aumento

### Algoritmo: BubbleSort
- **Speedup médio por número de threads**:
  - 2 threads: 0.10x
  - 4 threads: 0.10x
  - 8 threads: 0.09x
  - 16 threads: 0.09x
- **Aumento percentual do speedup ao dobrar o número de threads**:
  - 2 → 4 threads: -1.9% de aumento
  - 4 → 8 threads: -2.4% de aumento
  - 8 → 16 threads: -4.7% de aumento

### Algoritmo: InsertSort
- **Speedup médio por número de threads**:
  - 2 threads: 48.07x
  - 4 threads: 58.06x
  - 8 threads: 63.26x
  - 16 threads: 56.32x
- **Aumento percentual do speedup ao dobrar o número de threads**:
  - 2 → 4 threads: 20.8% de aumento
  - 4 → 8 threads: 9.0% de aumento
  - 8 → 16 threads: -11.0% de aumento

## 7. Análise de overhead da paralelização
### Algoritmo: BubbleSort
- **Tempo serial médio**: 1942.9274 ms
- **Tempo paralelo (2 threads) médio**: 8275.0327 ms
- **Fator de overhead**: 4.26
- **ALERTA**: Este algoritmo tem overhead de paralelização significativo!

### Algoritmo: MergeSort
- **Tempo serial médio**: 1.7262 ms
- **Tempo paralelo (2 threads) médio**: 1.3696 ms
- **Fator de overhead**: 0.79

### Algoritmo: SelectionSort
- **Tempo serial médio**: 657.3903 ms
- **Tempo paralelo (2 threads) médio**: 178.3977 ms
- **Fator de overhead**: 0.27

### Algoritmo: InsertSort
- **Tempo serial médio**: 151.1529 ms
- **Tempo paralelo (2 threads) médio**: 1.5339 ms
- **Fator de overhead**: 0.01

## 8. Tamanho do array a partir do qual o paralelismo compensa
### Algoritmo: SelectionSort
- **Tamanho mínimo**: 10000
- **Melhor número de threads**: 8
- **Speedup**: 11.22x

### Algoritmo: MergeSort
- **Tamanho mínimo**: 100000
- **Melhor número de threads**: 16
- **Speedup**: 2.00x

### Algoritmo: BubbleSort
- Paralelismo não compensa para nenhum tamanho testado

### Algoritmo: InsertSort
- **Tamanho mínimo**: 10000
- **Melhor número de threads**: 4
- **Speedup**: 4.93x

ritmo: InsertSort
- **Tamanho mínimo**: 4000
- **Melhor número de threads**: 2
- **Speedup**: 1.48x

