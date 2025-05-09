package AtvExtra.serial;

public class InsertSort {
    public static void main(String[] args) {
        int[] array = {8, 4, 6, 2, 9, 1};

        System.out.println("Antes da ordenação:");
        printArray(array);

        insertionSort(array);

        System.out.println("Depois da ordenação:");
        printArray(array);
    }

    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int chave = arr[i];
            int j = i - 1;

            // Move os elementos maiores que a chave uma posição à frente
            while (j >= 0 && arr[j] > chave) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = chave;
        }
    }

    public static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}