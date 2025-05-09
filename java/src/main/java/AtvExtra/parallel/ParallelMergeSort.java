package AtvExtra.parallel;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort {

    private static final int SEQUENTIAL_THRESHOLD = 1000;

    private static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int[] temp;
        private final int left;
        private final int right;
        private final int depth;
        private final int maxDepth;

        MergeSortTask(int[] array, int[] temp, int left, int right, int depth, int maxDepth) {
            this.array = array;
            this.temp = temp;
            this.left = left;
            this.right = right;
            this.depth = depth;
            this.maxDepth = maxDepth;
        }

        @Override
        protected void compute() {
            if (depth >= maxDepth) {
                sequentialMergeSort(array, temp, left, right);
            } else {
                int middle = left + (right - left) / 2;

                MergeSortTask leftTask = new MergeSortTask(array, temp, left, middle, depth + 1, maxDepth);
                MergeSortTask rightTask = new MergeSortTask(array, temp, middle + 1, right, depth + 1, maxDepth);

                invokeAll(leftTask, rightTask);

                merge(array, temp, left, middle, right);
            }
        }
    }

    private static void sequentialMergeSort(int[] array, int[] temp, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;

            sequentialMergeSort(array, temp, left, middle);
            sequentialMergeSort(array, temp, middle + 1, right);

            merge(array, temp, left, middle, right);
        }
    }

    private static void merge(int[] array, int[] temp, int left, int middle, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = array[i];
        }

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (temp[i] <= temp[j]) {
                array[k++] = temp[i++];
            } else {
                array[k++] = temp[j++];
            }
        }

        while (i <= middle) {
            array[k++] = temp[i++];
        }
    }

    public static void parallelMergeSort(int[] array, int numCores) {
        if (array == null || array.length <= 1) {
            return;
        }

        ForkJoinPool pool = new ForkJoinPool(numCores);

        int[] temp = new int[array.length];

        int maxDepth = 31 - Integer.numberOfLeadingZeros(numCores);

        pool.invoke(new MergeSortTask(array, temp, 0, array.length - 1, 0, maxDepth));

        pool.shutdown();
    }

    public static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int size = 1_000_000;
        int numCores = Runtime.getRuntime().availableProcessors();

        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * size);
        }

        System.out.println("Array original (primeiros 10 elementos):");
        printArray(Arrays.copyOf(array, 10));

        long startTime = System.currentTimeMillis();

        parallelMergeSort(array, numCores);

        long endTime = System.currentTimeMillis();

        System.out.println("Array ordenado (primeiros 10 elementos):");
        printArray(Arrays.copyOf(array, 10));

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Usando " + numCores + " núcleos");

        boolean sorted = true;
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Array está ordenado? " + sorted);
    }
}