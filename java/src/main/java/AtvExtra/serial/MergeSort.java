package AtvExtra.serial;

public class MergeSort {

    public static void mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int[] temp = new int[array.length];
        mergeSort(array, temp, 0, array.length - 1);
    }

    private static void mergeSort(int[] array, int[] temp, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;

            mergeSort(array, temp, left, middle);
            mergeSort(array, temp, middle + 1, right);

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
                array[k] = temp[i];
                i++;
            } else {
                array[k] = temp[j];
                j++;
            }
            k++;
        }

        while (i <= middle) {
            array[k] = temp[i];
            i++;
            k++;
        }
    }

    public static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] array = {38, 27, 43, 3, 9, 82, 10};

        System.out.println("Array original:");
        printArray(array);

        mergeSort(array);

        System.out.println("Array ordenado:");
        printArray(array);
    }
}
