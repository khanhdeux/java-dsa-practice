

import java.util.HashSet;
import java.util.Set;

/**
 * Kategorie: String Algorithms / Sliding Window Pattern
 * * Strategie: Sliding Window (Dynamische Größe)
 * - Wir nutzen zwei Pointer (left und right), die ein "Fenster" (die Raupe) bilden.
 * - Der 'right'-Pointer erweitert das Fenster und fügt Zeichen in ein Set ein.
 * - Wenn ein Duplikat gefunden wird, zieht der 'left'-Pointer so lange nach,
 * bis das Fenster wieder nur einzigartige Zeichen enthält.
 */
public class LongestSubstring {

    // Zeitkomplexität: O(n) - Jedes Zeichen wird maximal zweimal besucht (einmal von left, einmal von right).
    // Platzkomplexität: O(min(m, n)) - Das HashSet speichert maximal die Anzahl der einzigartigen Zeichen.
    public int lengthOfLongestSubstring(String s) {
        int left = 0; // Der "Hintern" der Raupe
        int max = 0;
        Set<Character> seen = new HashSet<>(); // Unser Gedächtnis für das aktuelle Fenster

        for (int right = 0; right < s.length(); right++) { // Der "Kopf" der Raupe
            char current = s.charAt(right);

            // Falls wir ein Duplikat sehen, muss der Hintern nachziehen und Zeichen entfernen
            while (seen.contains(current)) {
                seen.remove(s.charAt(left));
                left++;
            }

            seen.add(current); // Aktuellen Buchstaben ins Gedächtnis aufnehmen
            
            // Ziel: Länge berechnen -> (Rechter Index - Linker Index + 1)
            max = Math.max(max, right - left + 1); 
        }
        
        return max;
    }

    public static void main(String[] args) {
        LongestSubstring algo = new LongestSubstring();

        // Testfall 1
        // Ziel: "abcabcbb" -> "abc" ist der längste Teil ohne doppelte Buchstaben
        // Erwarteter Output: 3
        System.out.println("Länge von 'abcabcbb': " + algo.lengthOfLongestSubstring("abcabcbb"));

        // Testfall 2
        // Ziel: "bbbbb" -> "b" ist der längste Teil
        // Erwarteter Output: 1
        System.out.println("Länge von 'bbbbb': " + algo.lengthOfLongestSubstring("bbbbb"));

        // Testfall 3
        // Ziel: "pwwkew" -> "wke" ist der längste Teil
        // Erwarteter Output: 3
        System.out.println("Länge von 'pwwkew': " + algo.lengthOfLongestSubstring("pwwkew"));
    }
}