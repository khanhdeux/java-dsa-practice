import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// z.stream() // Start 
//    .XYZ() // Intermediate (beliebig viele) 
//    .ABC(); // Terminal (1x, immer letztes!)
// 1. stream() → immer zuerst -> 2. Intermediate → beliebig viele -> 3. Terminal → 1x, immer letztes -> 4. Lazy → startet erst bei Terminal
public class Stream {
public static void main(String[] args) {
        List<Integer> z = Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6);
        List<Produkt> produkte = Arrays.asList(
            new Produkt("iPhone", 999.0, "Elektronik"),
            new Produkt("Samsung", 799.0, "Elektronik"),
            new Produkt("Tisch", 199.0, "Möbel"),
            new Produkt("Stuhl", 49.0, "Möbel")
        );

        System.out.println("=== INTERMEDIATE OPERATIONS ===");

        // Ziel: nur n > 5 -> Erwartet: [8, 9, 7, 6]
        System.out.println("Filter > 5: " + z.stream().filter(n -> n > 5).collect(Collectors.toList()));

        // Ziel: nur gerade -> Erwartet: [2, 8, 4, 6]
        System.out.println("Filter gerade: " + z.stream().filter(n -> n % 2 == 0).collect(Collectors.toList()));

        // Ziel: nur ungerade -> Erwartet: [5, 1, 9, 3, 7]
        System.out.println("Filter ungerade: " + z.stream().filter(n -> n % 2 != 0).collect(Collectors.toList()));

        // Ziel: jeden mal 2 -> Erwartet: [10, 4, 16, 2, 18, 6, 14, 8, 12]
        System.out.println("Map * 2: " + z.stream().map(n -> n * 2).collect(Collectors.toList()));

        // Ziel: Quadrieren -> Erwartet: [25, 4, 64, 1, 81, 9, 49, 16, 36]
        System.out.println("Map Quadrat: " + z.stream().map(n -> n * n).collect(Collectors.toList()));

        // Ziel: aufsteigend sortiert -> Erwartet: [1, 2, 3, 4, 5, 6, 7, 8, 9]
        System.out.println("Sorted: " + z.stream().sorted().collect(Collectors.toList()));

        // Ziel: absteigend sortiert -> Erwartet: [9, 8, 7, 6, 5, 4, 3, 2, 1]
        System.out.println("Sorted Reverse: " + z.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));

        // Ziel: Duplikate weg (Beispiel mit Duplikaten)
        List<Integer> dups = Arrays.asList(1, 2, 2, 3, 3, 3);
        System.out.println("Distinct: " + dups.stream().distinct().collect(Collectors.toList()));

        // Ziel: erste 3 Elemente -> Erwartet: [5, 2, 8]
        System.out.println("Limit 3: " + z.stream().limit(3).collect(Collectors.toList()));

        // Ziel: erste 3 weg -> Erwartet: [1, 9, 3, 7, 4, 6]
        System.out.println("Skip 3: " + z.stream().skip(3).collect(Collectors.toList()));


        System.out.println("\n=== TERMINAL OPERATIONS ===");

        // Ziel: Anzahl -> Erwartet: 9
        System.out.println("Count: " + z.stream().count());

        // Ziel: Erstes Element finden -> Erwartet: Optional[5]
        System.out.println("FindFirst: " + z.stream().findFirst());

        // Ziel: Maximum finden -> Erwartet: Optional[9]
        System.out.println("Max: " + z.stream().max(Comparator.naturalOrder()));

        // Ziel: Alle als String verbunden -> Erwartet: "5, 2, 8, 1, 9, 3, 7, 4, 6"
        System.out.println("Joining: " + z.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        // Ziel: Irgendeine Zahl > 8? -> Erwartet: true
        System.out.println("AnyMatch > 8: " + z.stream().anyMatch(n -> n > 8));

        // Ziel: Summe via reduce -> Erwartet: 45
        System.out.println("Reduce Summe: " + z.stream().reduce(0, (a, b) -> a + b));

        // Ziel: Summe via mapToInt -> Erwartet: 45
        System.out.println("Sum via mapToInt: " + z.stream().mapToInt(Integer::intValue).sum());


        System.out.println("\n=== GROUPING ===");

        // Ziel: Gerade / Ungerade gruppieren -> Erwartet: {gerade=[2,8,4,6], ungerade=[5,1,9,3,7]}
        System.out.println("GroupingBy: " + z.stream().collect(Collectors.groupingBy(n -> n % 2 == 0 ? "gerade" : "ungerade")));

        // Ziel: True / False aufteilen (> 5) -> Erwartet: {false=[5, 2, 1, 3, 4], true=[8, 9, 7, 6]}
        System.out.println("PartitioningBy: " + z.stream().collect(Collectors.partitioningBy(n -> n > 5)));


        System.out.println("\n=== MIT OBJEKTEN ===");

        // Ziel: Nach Preis sortieren -> Erwartet: [Stuhl(49.0), Tisch(199.0)...]
        System.out.println("Sort nach Preis: " + produkte.stream().sorted(Comparator.comparingDouble(p -> p.preis)).collect(Collectors.toList()));

        // Ziel: Teuerste pro Kategorie -> Erwartet: {Elektronik=Optional[iPhone(999.0)], Möbel=Optional[Tisch(199.0)]}
        System.out.println("MaxBy Kategorie: " + produkte.stream().collect(Collectors.groupingBy(p -> p.kategorie, Collectors.maxBy(Comparator.comparingDouble(p -> p.preis)))));


        System.out.println("\n=== INTERVIEW AUFGABEN ===");

        // Ziel: Zweithöchste Zahl -> Erwartet: 8
        System.out.println("Zweithöchste Zahl: " + z.stream().distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst().orElse(-1));

        // Ziel: Summe gerader Zahlen -> Erwartet: 20
        System.out.println("Summe gerader Zahlen: " + z.stream().filter(n -> n % 2 == 0).mapToInt(Integer::intValue).sum());

        // Ziel: Top 3 absteigend -> Erwartet: [9, 8, 7]
        System.out.println("Top 3 absteigend: " + z.stream().sorted(Comparator.reverseOrder()).limit(3).collect(Collectors.toList()));
    }

    // Hilfsklasse für Objekt-Beispiele
    static class Produkt {
        String name;
        double preis;
        String kategorie;

        Produkt(String name, double preis, String kategorie) {
            this.name = name;
            this.preis = preis;
            this.kategorie = kategorie;
        }

        @Override
        public String toString() {
            return name + "(" + preis + ")";
        }
    }
}