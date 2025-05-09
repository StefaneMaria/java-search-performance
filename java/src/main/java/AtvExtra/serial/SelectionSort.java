package AtvExtra.serial;

public class SelectionSort {

    public static void selectionSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

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
        int[] array = {64, 25, 12, 22, 11, 90, 45, 33};


        long startTime = System.currentTimeMillis();
        selectionSort(array);
        long endTime = System.currentTimeMillis();


        System.out.println("\nTempo de execução: " + (endTime - startTime) + " ms");

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