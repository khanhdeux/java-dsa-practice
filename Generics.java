import java.util.*;

/**
 * Kategorie: Java Language / Generics
 * * Strategie: PECS (Producer Extends, Consumer Super)
 * * - 'extends' (Upper Bound): Wenn du Daten aus einer Struktur LIEST (Producer).
 * - 'super' (Lower Bound): Wenn du Daten in eine Struktur SCHREIBST (Consumer).
 * - Grund der Einführung: Typsicherheit bei gleichzeitiger Flexibilität (Kovarianz/Kontravarianz).
 *  In Java ist List<String> keine Unterklasse von List<Object>
 *  Kovarianz (extends): Erlaubt es, Unterklassen zu akzeptieren. Nützlich für Read-Only Operationen.
 *  Kontravarianz (super): Erlaubt es, Oberklassen zu akzeptieren. Nützlich, wenn man Objekte in eine Sammlung einfügen will
 * (z.B. einen Hund in eine List<Tier> oder List<Object> legen).
 */
public class Generics {

    static class Tier { void atmen() { System.out.println("Tier atmet"); } }
    static class Hund extends Tier { void bellen() { System.out.println("Wuff!"); } }
    static class Katze extends Tier { }

    // ============================================================
    // 1. EXTENDS (? extends T) -> Producer (LESEN)
    // Man kann alles lesen als "T", aber nichts sicher hinzufügen.
    // ============================================================
    public static void printTiere(List<? extends Tier> tiere) {
        // Ziel: Sicherstellen, dass wir jedes Element als 'Tier' behandeln können
        for (Tier t : tiere) {
            t.atmen(); // Funktioniert, da alles mindestens ein Tier ist
        }
        
        // tiere.add(new Hund()); // FEHLER! Java weiß nicht, ob es eine List<Katze> ist.
    }

    // ============================================================
    // 2. SUPER (? super T) -> Consumer (SCHREIBEN)
    // Man kann sicher "T" hinzufügen, aber nicht sicher als "T" lesen.
    // ============================================================
    public static void addHunde(List<? super Hund> hundeListe) {
        // Ziel: Sicherstellen, dass wir einen Hund in die Liste stecken können
        hundeListe.add(new Hund()); // Funktioniert sicher
        
        // Hund h = hundeListe.get(0); // FEHLER! Liste könnte List<Object> sein.
        Object o = hundeListe.get(0); // Nur Object ist sicher beim Lesen.
    }

    // ============================================================
    // 3. GENERISCHE METHODE (Typparameter <T>)
    // Wenn Eingabe und Ausgabe vom selben Typ sein müssen.
    // ============================================================
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    public static void main(String[] args) {
        List<Hund> meineHunde = new ArrayList<>(Arrays.asList(new Hund()));
        List<Tier> meineTierArche = new ArrayList<>();

        // Test extends: List<Hund> ist kompatibel mit List<? extends Tier>
        System.out.println("--- Test Extends (Lesen) ---");
        printTiere(meineHunde);

        // Test super: List<Tier> ist kompatibel mit List<? super Hund>
        System.out.println("\n--- Test Super (Schreiben) ---");
        addHunde(meineTierArche);
        addHunde(meineHunde);
        System.out.println("Hunde hinzugefügt.");

        // Test Generische Methode
        Hund erster = getFirst(meineHunde);
        System.out.println("\nErster Hund abgerufen: " + erster);
    }
}