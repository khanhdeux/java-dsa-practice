import java.util.Arrays;

/**
 * Kategorie: Sorting / Efficient Sorts
 * * Strategie: Divide and Conquer (Pivot-basiert)
 * - Wählt ein Pivot-Element (hier das letzte).
 * - Partitionierung: Alle kleineren Elemente links vom Pivot, alle größeren rechts.
 * - Wiederholt dies rekursiv für die Teilbereiche.
 */
public class QuickSort {

    // Zeitkomplexität: O(n log n) im Durchschnitt, O(n²) im Worst-Case.
    // Platzkomplexität: O(log n) aufgrund des Rekursions-Stacks.
    public void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sort(arr, low, pivotIndex - 1);  // Linke Hälfte
            sort(arr, pivotIndex + 1, high); // Rechte Hälfte
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Letztes Element als Pivot
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Ziel: Elemente kleiner/gleich Pivot nach vorne schieben
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        // Implementierung: Pivot an die richtige Stelle (i+1) setzen
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        QuickSort algo = new QuickSort();
        int[] data = {10, 80, 30, 90, 40, 50, 70};
        System.out.println("QuickSort Input: " + Arrays.toString(data));
        algo.sort(data, 0, data.length - 1);
        System.out.println("Erwartet: [10, 30, 40, 50, 70, 80, 90] -> Ergebnis: " + Arrays.toString(data));
    }
}