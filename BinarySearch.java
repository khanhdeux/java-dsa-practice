/**
 * Kategorie: Search Algorithms / Divide and Conquer
 * * Strategie: Binary Search (Iterativ)
 * - Funktioniert NUR auf sortierten Arrays.
 * - Nutzt die Intervall-Halbierung, um die Zielzahl zu finden.
 * - Senior-Tipp: Nutzt 'left + (right - left) / 2', um einen Integer Overflow bei 
 * sehr großen Arrays zu verhindern, falls (left + right) > Integer.MAX_VALUE wäre.
 */
public class BinarySearch {

    // Zeitkomplexität: O(log n) - Die Suchmenge halbiert sich in jedem Schritt.
    // Platzkomplexität: O(1) - Es wird kein zusätzlicher Speicher (außer Variablen) benötigt.
    public int binarySearch(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            // Berechnung der Mitte (sicher gegen Overflow)
            int mid = left + (right - left) / 2;

            // Ziel: Das Element direkt in der Mitte prüfen
            if (nums[mid] == target) {
                return mid; // Index gefunden
            }

            // Implementierung: Bereich eingrenzen
            if (nums[mid] < target) {
                left = mid + 1; // Suche in der rechten Hälfte weiter
            } else {
                right = mid - 1; // Suche in der linken Hälfte weiter
            }
        }

        return -1; // Zielwert existiert nicht im Array
    }

    public static void main(String[] args) {
        BinarySearch algo = new BinarySearch();

        // Testfall 1
        // Ziel: Die Zahl 7 in {2, 5, 7, 11} finden -> Erwarteter Output: 2
        int[] nums1 = {2, 5, 7, 11};
        System.out.println("Suche 7 in {2,5,7,11}: Index " + algo.binarySearch(nums1, 7));

        // Testfall 2
        // Ziel: Eine Zahl suchen, die nicht existiert -> Erwarteter Output: -1
        System.out.println("Suche 9 in {2,5,7,11}: Index " + algo.binarySearch(nums1, 9));

        // Testfall 3
        // Ziel: Suche im Ein-Element-Array -> Erwarteter Output: 0
        int[] nums2 = {5};
        System.out.println("Suche 5 in {5}: Index " + algo.binarySearch(nums2, 5));
    }
}