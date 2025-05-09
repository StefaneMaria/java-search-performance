package AtvExtra.parallel;

import java.util.Arrays;
import java.util.concurrent.*;

public class ParallelInsertSort {

    private static final int THRESHOLD = 128;

    public static void parallelInsertionSort(int[] array, int numCores) {
        ForkJoinPool pool = new ForkJoinPool(numCores);
        pool.invoke(new InsertionSortTask(array, 0, array.length - 1));
    }

    private static class InsertionSortTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;

        public InsertionSortTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                sequentialInsertionSort(array, start, end);
                return;
            }

            int mid = start + (end - start) / 2;

            invokeAll(
                    new InsertionSortTask(array, start, mid),
                    new InsertionSortTask(array, mid + 1, end)
            );

            merge(array, start, mid, end);
        }

        private void sequentialInsertionSort(int[] arr, int start, int end) {
            for (int i = start + 1; i <= end; i++) {
                int key = arr[i];
                int j = i - 1;

                while (j >= start && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }

                arr[j + 1] = key;
            }
        }
        private void merge(int[] arr, int start, int mid, int end) {
            int n1 = mid - start + 1;
            int n2 = end - mid;

            int[] leftArray = new int[n1];
            int[] rightArray = new int[n2];

            for (int i = 0; i < n1; i++) {
                leftArray[i] = arr[start + i];
            }
            for (int j = 0; j < n2; j++) {
                rightArray[j] = arr[mid + 1 + j];
            }

            int i = 0, j = 0;

            int k = start;

            while (i < n1 && j < n2) {
                if (leftArray[i] <= rightArray[j]) {
                    arr[k] = leftArray[i];
                    i++;
                } else {
                    arr[k] = rightArray[j];
                    j++;
                }
                k++;
            }

            while (i < n1) {
                arr[k] = leftArray[i];
                i++;
                k++;
            }

            while (j < n2) {
                arr[k] = rightArray[j];
                j++;
                k++;
            }
        }
    }

    public static void imprimirArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }


    public static void main(String[] args) {
        int size = 100000;

        int[] arrayOriginal = new int[size];
        for (int i = 0; i < size; i++) {
            arrayOriginal[i] = (int)(Math.random() * 1000000);
        }

        int[] arrayParallel = Arrays.copyOf(arrayOriginal, arrayOriginal.length);

        System.out.println("Iniciando ordenação paralela...");
        long startTimeParallel = System.currentTimeMillis();
        parallelInsertionSort(arrayParallel, 2);
        long endTimeParallel = System.currentTimeMillis();
        System.out.println("Tempo para ordenação paralela: " + (endTimeParallel - startTimeParallel) + "ms");

        // Verificar se o array está ordenado
        boolean isParallelSorted = true;
        for (int i = 0; i < arrayParallel.length - 1; i++) {
            if (arrayParallel[i] > arrayParallel[i + 1]) {
                isParallelSorted = false;
                break;
            }
        }
        System.out.println("Array está ordenado (paralelo): " + isParallelSorted);
    }
}
