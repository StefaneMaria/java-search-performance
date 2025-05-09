package AtvExtra.parallel;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelSelectionSort {

    public static void blockParallelSelectionSort(int[] array, int numThreads) {
        if (array == null || array.length <= 1) {
            return;
        }

        int n = array.length;

        if (n < numThreads * 2 || numThreads <= 1) {
            sequentialSelectionSort(array);
            return;
        }

        try {
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            int blockSize = n / numThreads;

            for (int i = 0; i < numThreads; i++) {
                final int startIndex = i * blockSize;
                final int endIndex = (i == numThreads - 1) ? n : (i + 1) * blockSize;

                executor.submit(() -> {
                    int[] block = Arrays.copyOfRange(array, startIndex, endIndex);
                    sequentialSelectionSort(block);

                    System.arraycopy(block, 0, array, startIndex, block.length);
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            mergeBlocks(array, numThreads);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Ordenação interrompida: " + e.getMessage());
        }
    }

    private static void mergeBlocks(int[] array, int numThreads) {
        int n = array.length;
        int blockSize = n / numThreads;
        int[] temp = new int[n];

        System.arraycopy(array, 0, temp, 0, n);

        int[] indices = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            indices[i] = i * blockSize;
        }

        for (int i = 0; i < n; i++) {
            int minBlock = -1;
            int minValue = Integer.MAX_VALUE;

            for (int j = 0; j < numThreads; j++) {
                int blockEndIndex = (j == numThreads - 1) ? n : (j + 1) * blockSize;
                if (indices[j] < blockEndIndex && temp[indices[j]] < minValue) {
                    minValue = temp[indices[j]];
                    minBlock = j;
                }
            }

            if (minBlock != -1) {
                array[i] = temp[indices[minBlock]];
                indices[minBlock]++;
            }
        }
    }


    private static void sequentialSelectionSort(int[] array) {
        int n = array.length;

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }
    }

    public static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }


    public static void main(String[] args) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Usando " + numThreads + " threads");

        int[] smallArray = {64, 25, 12, 22, 11, 90, 45, 33};
        int[] smallArrayCopy = Arrays.copyOf(smallArray, smallArray.length);

        System.out.println("\nUsando blockParallelSelectionSort:");
        long startTime = System.currentTimeMillis();
        blockParallelSelectionSort(smallArrayCopy, numThreads);
        long endTime = System.currentTimeMillis();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");

        Boolean sorted = true;
        for (int i = 0; i < smallArrayCopy.length - 1; i++) {
            if (smallArrayCopy[i] > smallArrayCopy[i + 1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Array ordenado? " + sorted);
    }
}
