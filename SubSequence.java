
/**
 * Kategorie: String Algorithms / Two Pointer Pattern
 * * Strategie: 2-Pointer
 * - Pointer 'i' scannt den potenziellen Subsequence-String 's'.
 * - Pointer 'j' scannt den Ziel-String 't'.
 * - 'i' wird nur bewegt, wenn ein Treffer gefunden wurde.
 * - 'j' wird bei jedem Schritt bewegt, um den Ziel-String zu durchlaufen.
 */
public class SubSequence {

    // Zeitkomplexität: O(n) - wobei n die Länge von t ist, da wir t genau einmal durchlaufen.
    // Platzkomplexität: O(1) - wir nutzen nur zwei zusätzliche Integer-Variablen.
    public boolean isSubsequence(String s, String t) {
        // Ziel: Leerer String ist immer eine Subsequence -> Erwartet: true
        if (s.isEmpty()) return true;

        int i = 0; // Pointer für s
        int j = 0; // Pointer für t

        // Implementierung
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++; // Zeichen gefunden, nächstes Zeichen in s suchen
            }
            j++; // Immer im Ziel-String weitergehen
        }

        // Wenn i am Ende von s angekommen ist, wurden alle Zeichen in der richtigen Reihenfolge gefunden
        return i == s.length();
    }

    public static void main(String[] args) {
        SubSequence algo = new SubSequence();

        // Testfall 1
        // Ziel: ace in abcde finden -> Erwartet: true
        System.out.println("ace in abcde: " + algo.isSubsequence("ace", "abcde"));

        // Testfall 2
        // Ziel: aec in abcde finden -> Erwartet: false (falsche Reihenfolge)
        System.out.println("aec in abcde: " + algo.isSubsequence("aec", "abcde"));
        
        // Testfall 3
        // Ziel: leerer String -> Erwartet: true
        System.out.println("Leerer String: " + algo.isSubsequence("", "ahbgdc"));
    }
}