import java.util.Arrays;

/**
 * Kategorie: Sorting / Elementary Sorts
 * * Strategie: Einfügen (Karten-Prinzip)
 * - Nimmt ein Element ("key") und schiebt es an die richtige Stelle im bereits sortierten linken Teil.
 * - Sehr effizient für fast sortierte Listen.
 */
public class InsertionSort {

    // Zeitkomplexität: O(n²) - Im Best-Case (schon sortiert) jedoch O(n).
    // Platzkomplexität: O(1) - In-place Sortierung.
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            // Ziel: Schiebe Elemente, die größer als der key sind, nach rechts
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            // Implementierung: Key an der gefundenen Lücke einfügen
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        InsertionSort algo = new InsertionSort();
        int[] data = {12, 11, 13, 5, 6};
        System.out.println("InsertionSort Input: " + Arrays.toString(data));
        algo.sort(data);
        System.out.println("Erwartet: [5, 6, 11, 12, 13] -> Ergebnis: " + Arrays.toString(data));
    }
}