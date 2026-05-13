/**
 * Kategorie: Array Algorithms / Two Pointer Pattern
 * * Strategie: Two Pointers (Left & Right)
 * - Da das Array sortiert ist, können wir einen Pointer am Anfang (i) und einen am Ende (j) setzen.
 * - Ist die Summe zu klein, bewegen wir den linken Pointer nach rechts (um die Summe zu erhöhen).
 * - Ist die Summe zu groß, bewegen wir den rechten Pointer nach links (um die Summe zu verringern).
 */
public class TwoSumSorted {

    // Zeitkomplexität: O(n) - Das Array wird maximal einmal durchlaufen.
    // Platzkomplexität: O(1) - Es werden nur zwei zusätzliche Variablen (i, j) genutzt.
    public int[] twoSumSorted(int[] nums, int target) {
        int i = 0;
        int j = nums.length - 1;

        while (i < j) {
            int sum = nums[i] + nums[j];

            // Ziel: Das Paar finden, das genau das Target ergibt
            if (sum == target) {
                return new int[]{i, j};
            }

            // Implementierung: Summe anpassen basierend auf Sortierung
            if (sum < target) {
                i++; // Summe erhöhen
            } else {
                j--; // Summe verringern
            }
        }

        return new int[]{};
    }

    public static void main(String[] args) {
        TwoSumSorted algo = new TwoSumSorted();

        // Testfall 1
        // Ziel: Target 9 in {2, 5, 7, 11} finden -> Erwartet: [0, 2] (2 + 7)
        int[] nums1 = {2, 5, 7, 11};
        int[] result1 = algo.twoSumSorted(nums1, 9);
        System.out.println("Input {2, 5, 7, 11}, Target 9: [" + result1[0] + ", " + result1[1] + "]");

        // Testfall 2
        // Ziel: Target 10 in {1, 2, 3, 4, 6} finden -> Erwartet: [3, 4] (4 + 6)
        int[] nums2 = {1, 2, 3, 4, 6};
        int[] result2 = algo.twoSumSorted(nums2, 10);
        System.out.println("Input {1, 2, 3, 4, 6}, Target 10: [" + result2[0] + ", " + result2[1] + "]");
    }
}