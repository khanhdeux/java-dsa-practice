import java.util.Arrays;

/**
 * Kategorie: Sorting / Elementary Sorts
 * * Strategie: Minimum-Suche
 * - Teilt das Array in einen sortierten und einen unsortierten Teil.
 * - Sucht das kleinste Element im unsortierten Teil und tauscht es an die erste freie Stelle.
 */
public class SelectionSort {

    // Zeitkomplexität: O(n²) - Muss immer das restliche Array scannen.
    // Platzkomplexität: O(1) - In-place Sortierung.
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                // Ziel: Index des kleinsten Elements finden
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            // Implementierung: Tausche kleinstes Element an die Position i
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }

    public static void main(String[] args) {
        SelectionSort algo = new SelectionSort();
        int[] data = {64, 25, 12, 22, 11};
        System.out.println("SelectionSort Input: " + Arrays.toString(data));
        algo.sort(data);
        System.out.println("Erwartet: [11, 12, 22, 25, 64] -> Ergebnis: " + Arrays.toString(data));
    }
}