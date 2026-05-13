import java.util.Arrays;

/**
 * Kategorie: Sorting / Elementary Sorts
 * * Strategie: Nachbarschafts-Vergleich
 * - Geht wiederholt durch die Liste.
 * - Vergleicht benachbarte Elemente und tauscht sie, wenn sie in der falschen Reihenfolge sind.
 * - Die größte Zahl "bubbelt" am Ende jedes Durchgangs nach ganz rechts.
 */
public class BubbleSort {

    // Zeitkomplexität: O(n²) - Durch verschachtelte Schleifen.
    // Platzkomplexität: O(1) - In-place Sortierung.
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Ziel: Größeres Element nach rechts schieben
                if (arr[j] > arr[j + 1]) {
                    // Implementierung: Tausche arr[j] und arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        BubbleSort algo = new BubbleSort();
        int[] data = {5, 2, 8, 1, 9};
        System.out.println("BubbleSort Input: " + Arrays.toString(data));
        algo.sort(data);
        System.out.println("Erwartet: [1, 2, 5, 8, 9] -> Ergebnis: " + Arrays.toString(data));
    }
}