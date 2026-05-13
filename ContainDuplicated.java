import java.util.HashSet;
import java.util.Set;

/**
 * Kategorie: Array Algorithms / Hash Set Pattern
 * * Strategie: Set-Gedächtnis
 * - Wir durchlaufen das Array ein einziges Mal.
 * - Jedes Element wird in einem HashSet gespeichert.
 * - Da ein Set keine Duplikate erlaubt, wissen wir sofort, dass ein Duplikat vorliegt, 
 * wenn 'contains' vor dem Hinzufügen 'true' ergibt.
 */
public class ContainDuplicated {

    // Zeitkomplexität: O(n) - Das Array wird genau einmal durchlaufen.
    // Platzkomplexität: O(n) - Im schlechtesten Fall (keine Duplikate) speichert das Set alle n Elemente.
    public boolean containDuplicated(int[] nums) {
        // Unser Gedächtnis für bereits gesehene Zahlen
        Set<Integer> gesehen = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            // Ziel: Prüfen, ob die aktuelle Zahl schon einmal vorkam -> Erwartet: true
            if (gesehen.contains(nums[i])) {
                return true;
            }
            
            // Implementierung: Zahl für den weiteren Verlauf merken
            gesehen.add(nums[i]);
        }

        return false;
    }

    public static void main(String[] args) {
        ContainDuplicated algo = new ContainDuplicated();

        // Testfall 1
        // Ziel: Duplikat '2' finden -> Erwarteter Output: true
        int[] nums1 = {2, 5, 2, 11};
        System.out.println("Input {2, 5, 2, 11}: " + algo.containDuplicated(nums1));

        // Testfall 2
        // Ziel: Keine Duplikate -> Erwarteter Output: false
        int[] nums2 = {1, 2, 3, 4};
        System.out.println("Input {1, 2, 3, 4}: " + algo.containDuplicated(nums2));

        // Testfall 3
        // Ziel: Leeres Array -> Erwarteter Output: false
        int[] nums3 = {};
        System.out.println("Leeres Array: " + algo.containDuplicated(nums3));
    }
}