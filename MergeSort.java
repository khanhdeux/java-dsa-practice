import java.util.Arrays;

/**
 * Kategorie: Sorting / Efficient Sorts
 * * Strategie: Divide and Conquer (Zusammenführen)
 * - Teilt das Array rekursiv in zwei Hälften, bis nur noch Einzel-Elemente übrig sind.
 * - Fügt (Merged) diese sortiert wieder zusammen.
 */
public class MergeSort {

    // Zeitkomplexität: O(n log n) - Stabil in allen Fällen.
    // Platzkomplexität: O(n) - Benötigt temporäre Arrays zum Mergen.
    public void sort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            sort(arr, left, mid);      // Linke Hälfte sortieren
            sort(arr, mid + 1, right); // Rechte Hälfte sortieren
            merge(arr, left, mid, right);
        }
    }

    private void merge(int[] arr, int left, int mid, int right) {
        // Ziel: Zwei sortierte Teil-Arrays erstellen
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        // Implementierung: Zusammenführen (Reißverschluss-Prinzip)
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i]; i++;
            } else {
                arr[k] = R[j]; j++;
            }
            k++;
        }

        // Reste kopieren
        while (i < n1) { arr[k] = L[i]; i++; k++; }
        while (j < n2) { arr[k] = R[j]; j++; k++; }
    }

    public static void main(String[] args) {
        MergeSort algo = new MergeSort();
        int[] data = {38, 27, 43, 3, 9, 82, 10};
        System.out.println("MergeSort Input: " + Arrays.toString(data));
        algo.sort(data, 0, data.length - 1);
        System.out.println("Erwartet: [3, 9, 10, 27, 38, 43, 82] -> Ergebnis: " + Arrays.toString(data));
    }
}