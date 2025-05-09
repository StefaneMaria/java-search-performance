package AtvExtra.parallel;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class ParallelBubbleSort {

    private static final int SEQUENTIAL_THRESHOLD = 1000;

    public static void parallelBubbleSort(int[] array, int numCores) {
        int n = array.length;
        ForkJoinPool pool = new ForkJoinPool(numCores);


        for (int i = 0; i < n - 1; i++) {

            final ParallelBubbleSortTask evenTask = new ParallelBubbleSortTask(array, 0, n - 1, true);
            pool.invoke(evenTask);

            final ParallelBubbleSortTask oddTask = new ParallelBubbleSortTask(array, 0, n - 1, false);
            pool.invoke(oddTask);
        }

        boolean needsMoreSorting = true;
        while (needsMoreSorting) {
            AtomicBoolean swapped = new AtomicBoolean(false);
            SequentialCheckTask checkTask = new SequentialCheckTask(array, swapped);
            pool.invoke(checkTask);

            needsMoreSorting = swapped.get();
        }
    }

    private static class ParallelBubbleSortTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;
        private final boolean evenPhase;

        public ParallelBubbleSortTask(int[] array, int start, int end, boolean evenPhase) {
            this.array = array;
            this.start = start;
            this.end = end;
            this.evenPhase = evenPhase;
        }

        @Override
        protected void compute() {
            int length = end - start + 1;

            if (length <= SEQUENTIAL_THRESHOLD) {
                processSequentially();
            } else {
                int mid = start + length / 2;
                invokeAll(
                        new ParallelBubbleSortTask(array, start, mid, evenPhase),
                        new ParallelBubbleSortTask(array, mid + 1, end, evenPhase)
                );
            }
        }

        private void processSequentially() {
            int startIdx = evenPhase ? start + (start % 2) : start + ((start + 1) % 2);

            for (int i = startIdx; i < end; i += 2) {
                if (i + 1 <= end && array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                }
            }
        }
    }

    private static class SequentialCheckTask extends RecursiveAction {
        private final int[] array;
        private final AtomicBoolean swapped;

        public SequentialCheckTask(int[] array, AtomicBoolean swapped) {
            this.array = array;
            this.swapped = swapped;
        }

        @Override
        protected void compute() {
            for (int i = 0; i < array.length - 1; i++) {
                if (array[i] > array[i + 1]) {
                    // Troca os elementos
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    swapped.set(true);
                }
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
        int size = 10000;
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = (int)(Math.random() * 100000);
        }
        System.out.println("Iniciando ordenação paralela...");
        long startTime = System.currentTimeMillis();
        parallelBubbleSort(array, 2);
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo para ordenação paralela: " + (endTime - startTime) + "ms");

        boolean sorted = true;
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                sorted = false;
                break;
            }
        }
        System.out.println("Array está ordenado: " + sorted);

    }

}