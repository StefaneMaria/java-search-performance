package AtvExtra.serial;

public class BubbleSort {
    public static void bubbleSort(int[] array) {
        int n = array.length;
        boolean trocou;

        for (int i = 0; i < n - 1; i++) {
            trocou = false;

            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    trocou = true;
                }
            }

            if (!trocou) break;
        }
    }

    public static void main(String[] args) {
        int[] numeros = {64, 34, 25, 12, 22, 11, 90};

        System.out.println("Array original:");
        for (int num : numeros) {
            System.out.print(num + " ");
        }

        bubbleSort(numeros);

        System.out.println("\nArray ordenado com Bubble Sort:");
        for (int num : numeros) {
            System.out.print(num + " ");
        }
    }
}
