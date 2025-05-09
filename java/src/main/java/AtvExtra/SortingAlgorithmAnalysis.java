package AtvExtra;

import AtvExtra.parallel.ParallelBubbleSort;
import AtvExtra.parallel.ParallelInsertSort;
import AtvExtra.parallel.ParallelMergeSort;
import AtvExtra.parallel.ParallelSelectionSort;
import AtvExtra.serial.BubbleSort;
import AtvExtra.serial.InsertSort;
import AtvExtra.serial.MergeSort;
import AtvExtra.serial.SelectionSort;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SortingAlgorithmAnalysis {

//    Contantes de configuracao
    private static final int[] ARRAY_SIZES = {1000, 10000, 100000};

    private static final int[] THREAD_COUNTS = {1, 2, 4, 8, Runtime.getRuntime().availableProcessors()};
    private static final int NUM_SAMPLES = 5;
    private static final String CSV_FILE = "sorting_results.csv";

//    Tipos de arrays para teste
    private static final int RANDOM_ARRAY = 0;
    private static final int SORTED_ARRAY = 1;
    private static final int REVERSE_ARRAY = 2;
    private static final int PARTIALLY_SORTED_ARRAY = 3;
    private static final String[] ARRAY_TYPE_NAMES = {"Random", "Sorted", "Reverse", "PartiallySorted"};

//    Resultados
    private static List<ResultEntry> results = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Iniciando análise de algoritmos de ordenação...");

//        Executar testes para cada tamanho de array
        for (int size : ARRAY_SIZES) {
            System.out.println("\nTestando com tamanho de array: " + size);

//            Gerar diferentes tipos de arrays
            for (int arrayType = 0; arrayType <= 3; arrayType++) {
                System.out.println("  Tipo de array: " + ARRAY_TYPE_NAMES[arrayType]);

                try {
                    runTests(size, arrayType);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

//        Mapea o tempo serial de cada algoritmo
        Map<String, Double> serialTimes = new HashMap<>();
        for (ResultEntry e : results) {
            if (e.threadCount == 1) {
                String key = String.join("|",
                        String.valueOf(e.size),
                        e.algorithm,
                        "Serial",
                        e.arrayType,
                        String.valueOf(e.sample)
                );
                serialTimes.put(key, e.timeMs);
            }
        }

        for (ResultEntry e : results) {
            if (e.threadCount > 1) {
                String key = String.join("|",
                        String.valueOf(e.size),
                        e.algorithm,
                        "Serial",
                        e.arrayType,
                        String.valueOf(e.sample)
                );

                Double serialTime = serialTimes.get(key);
                if (serialTime != null && serialTime > 0) {
                    e.speedup    = serialTime / e.timeMs;
                    e.efficiency = e.speedup / e.threadCount;
                    System.out.println("SpeedUp: " + e.speedup);
                    System.out.println("Efficiency: " + e.efficiency);
                }
            }
        }

        // Exportar resultados para CSV
        exportResultsToCSV();

        System.out.println("\nAnálise concluída! Resultados salvos em " + CSV_FILE);
    }


//    Executa chamadas para os algoritmos de teste
    private static void runTests(int size, int arrayType) throws InterruptedException{
        for (int sample = 1; sample <= NUM_SAMPLES; sample++) {
            int[] array = generateArray(size, arrayType);

            benchmarkSerialAlgorithm(array, "SelectionSort", arrayType, size, sample, results, SelectionSort::selectionSort);
            benchmarkSerialAlgorithm(array, "MergeSort", arrayType, size, sample, results, MergeSort::mergeSort);
            benchmarkSerialAlgorithm(array, "BubbleSort", arrayType, size, sample, results, BubbleSort::bubbleSort);
            benchmarkSerialAlgorithm(array, "InsertSort", arrayType, size, sample, results, InsertSort::insertionSort);

            for (int threadCount: THREAD_COUNTS) {
                if (threadCount == 1) continue;

                benchmarkParallelAlgorithm(array, "SelectionSort", arrayType, size, sample, threadCount, results, ParallelSelectionSort::blockParallelSelectionSort);
                benchmarkParallelAlgorithm(array, "MergeSort", arrayType, size, sample, threadCount, results, ParallelMergeSort::parallelMergeSort);
                benchmarkParallelAlgorithm(array, "BubbleSort", arrayType, size, sample, threadCount, results, ParallelBubbleSort::parallelBubbleSort);
                benchmarkParallelAlgorithm(array, "InsertSort", arrayType, size, sample, threadCount, results, ParallelInsertSort::parallelInsertionSort);

            }
        }
    }

//    Prepara a chamada dos algoritmos seriais
    private static void benchmarkSerialAlgorithm(
            int[] array,
            String algorithmName,
            int arrayType,
            int size,
            int sample,
            List<ResultEntry> results,
            Consumer<int[]> sortingFunction) {

        int[] arrayCopy = Arrays.copyOf(array, array.length);

        long startTime = System.nanoTime();
        sortingFunction.accept(arrayCopy);
        long serialTime = System.nanoTime() - startTime;

        results.add(new ResultEntry(
                size,
                algorithmName,
                "Serial",
                1,
                ARRAY_TYPE_NAMES[arrayType],
                sample,
                serialTime / 1_000_000.0));
    }

//    Prepara a chamada dos algoritmos paralelos
    private static void benchmarkParallelAlgorithm(
            int[] array,
            String algorithmName,
            int arrayType,
            int size,
            int sample,
            int threadCount,
            List<ResultEntry> results,
            BiConsumer<int[], Integer> sortingFunction) {

        int[] arrayCopy = Arrays.copyOf(array, array.length);

        long startTime = System.nanoTime();
        sortingFunction.accept(arrayCopy, threadCount);
        long parallelTime = System.nanoTime() - startTime;

        results.add(new ResultEntry(
                size,
                algorithmName,
                "Parallel",
                threadCount,
                ARRAY_TYPE_NAMES[arrayType],
                sample,
                parallelTime / 1_000_000.0));
    }

//  Gera array com as caracteristicas desejadas
    private static int[] generateArray(int size, int arrayType) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000000);
        }

        switch (arrayType) {
            case SORTED_ARRAY:
                Arrays.sort(array);
                break;
            case REVERSE_ARRAY:
                Arrays.sort(array);
                for (int i = 0; i < size / 2; i++) {
                    int temp = array[i];
                    array[i] = array[size - 1 - i];
                    array[size - 1 - i] = temp;
                }
                break;
            case PARTIALLY_SORTED_ARRAY:
                Arrays.sort(array, 0, (int) (size * 0.7));
                break;
            case RANDOM_ARRAY:
            default:
                break;
        }

        return array;
    }

    private static void exportResultsToCSV() {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            // Escrever cabeçalho
            writer.append("Size,Algorithm,Type,ThreadCount,ArrayType,Sample,TimeMs,SpeedUp,Efficiency\n");

            // Escrever resultados
            for (ResultEntry entry : results) {
                writer.append(String.valueOf(entry.size)).append(",")
                        .append(entry.algorithm).append(",")
                        .append(entry.type).append(",")
                        .append(String.valueOf(entry.threadCount)).append(",")
                        .append(entry.arrayType).append(",")
                        .append(String.valueOf(entry.sample)).append(",")
                        .append(String.valueOf(entry.timeMs)).append(",")
                        .append(String.valueOf(entry.speedup)).append(",")
                        .append(String.valueOf(entry.efficiency)).append("\n");
            }

            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao exportar resultados para CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ResultEntry {
        int size;
        String algorithm;
        String type;
        int threadCount;
        String arrayType;
        int sample;
        double timeMs;
        double speedup;
        double efficiency;

        ResultEntry(int size, String algorithm, String type, int threadCount,
                    String arrayType, int sample, double timeMs) {
            this.size = size;
            this.algorithm = algorithm;
            this.type = type;
            this.threadCount = threadCount;
            this.arrayType = arrayType;
            this.sample = sample;
            this.timeMs = timeMs;
            this.speedup = 0.0;
            this.efficiency = 0.0;
        }
    }
}