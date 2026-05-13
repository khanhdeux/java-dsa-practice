import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * Kategorie: Array Algorithms / Hash Table Pattern
 * * Strategie: One-Pass Hash Map
 * - Während wir durch das Array gehen, berechnen wir das 'Complement' (Ziel - aktueller Wert).
 * - Wir prüfen in O(1), ob dieses Complement bereits in unserer Map (unserem Gedächtnis) existiert.
 * - Falls ja, haben wir das Paar gefunden. Falls nein, speichern wir den aktuellen Wert mit seinem Index.
 */
public class TwoSum {

    // Zeitkomplexität: O(n) - Wir durchlaufen das Array genau einmal.
    // Platzkomplexität: O(n) - Im schlechtesten Fall speichern wir n Elemente in der Map.
    public int[] twoSum(int[] nums, int target) {
        // Unser Gedächtnis: Key = Zahl, Value = Index
        Map<Integer, Integer> gesehen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            // Ziel: Prüfen, ob das Gegenstück bereits gesehen wurde -> Erwartet: Index des Complements
            if (gesehen.containsKey(complement)) {
                return new int[]{gesehen.get(complement), i};
            }

            // Implementierung: Aktuelle Zahl und Index für später merken
            gesehen.put(nums[i], i);
        }

        // Falls kein Paar gefunden wurde
        return new int[]{};
    }

    public static void main(String[] args) {
        TwoSum algo = new TwoSum();

        // Testfall 1
        // Ziel: Pair für Target 9 finden -> Erwartet: [0, 1]
        int[] nums1 = {2, 7, 11, 15};
        System.out.println("Input {2,7,11,15}, Target 9: " + Arrays.toString(algo.twoSum(nums1, 9)));

        // Testfall 2
        // Ziel: Pair für Target 6 finden -> Erwartet: [1, 2]
        int[] nums2 = {3, 2, 4};
        System.out.println("Input {3,2,4}, Target 6: " + Arrays.toString(algo.twoSum(nums2, 6)));

        // Testfall 3
        // Ziel: Kein Paar vorhanden -> Erwartet: []
        int[] nums3 = {1, 2, 3};
        System.out.println("Input {1,2,3}, Target 7: " + Arrays.toString(algo.twoSum(nums3, 7)));
    }
}