import java.util.*;

/**
 * Kategorie: Object-Oriented Design / SOLID
 * Strategie: Wartbarer, testbarer und erweiterbarer Code.
 */
public class SOLID {

    // ============================================================
    // S - Single Responsibility Principle (SRP)
    // Fehler: Eine Klasse macht alles (Logik + Persistenz + E-Mail).
    // Fix: Trennung der Zuständigkeiten.
    // ============================================================
    static class UserSettings {
        // Gut: Nur für Benutzerdaten zuständig
        void changePassword(String pass) { System.out.println("Passwort geändert"); }
        
        // FALSCH: Würde diese Klasse auch die DB speichern oder E-Mails senden,
        // müsste sie bei jeder DB-Änderung angepasst werden.
    }

    // ============================================================
    // O - Open/Closed Principle (OCP)
    // Fehler: "if-else" oder "switch" für jeden neuen Typ (muss bei Erweiterung geändert werden).
    // Fix: Interfaces nutzen. Offen für neue Klassen, geschlossen für Code-Änderung im Kern.
    // ============================================================
    interface Shape { double area(); }

    static class Rectangle implements Shape {
        double w, h;
        public double area() { return w * h; }
    }

    static class Circle implements Shape {
        double r;
        public double area() { return Math.PI * r * r; }
    }
    // Erklärung: Neue Formen können hinzugefügt werden, ohne die Flächenberechnungs-Logik zu ändern.

    // ============================================================
    // L - Liskov Substitution Principle (LSP)
    // Fehler: Eine Unterklasse bricht das Verhalten der Oberklasse (z.B. ein Pinguin, der fliegen soll).
    // Fix: Unterklassen müssen sich so verhalten, dass sie die Oberklasse ohne Fehler ersetzen können.
    // ============================================================
    static abstract class Vogel { abstract void fressen(); }
    static abstract class FlugVogel extends Vogel { abstract void fliegen(); }

    static class Adler extends FlugVogel {
        void fressen() { System.out.println("Adler frisst"); }
        void fliegen() { System.out.println("Adler fliegt"); }
    }
    
    static class Pinguin extends Vogel {
        void fressen() { System.out.println("Pinguin frisst"); }
        // Erbt nicht von FlugVogel, daher kein Absturz beim Aufruf von fliegen().
    }

    // ============================================================
    // I - Interface Segregation Principle (ISP)
    // Fehler: Ein Riesen-Interface zwingt Klassen, Methoden zu implementieren, die sie nicht brauchen.
    // Fix: Viele kleine, spezifische Interfaces.
    // ============================================================
    interface Druckbar { void drucken(); }
    interface Scannbar { void scannen(); }

    static class NurDrucker implements Druckbar {
        public void drucken() { System.out.println("Drucke..."); }
        // Muss nicht scannen() implementieren!
    }

    // ============================================================
    // D - Dependency Inversion Principle (DIP)
    // Fehler: Eine High-Level Klasse hängt direkt von einer Low-Level Klasse ab (harte Kopplung).
    // Fix: Beide hängen von einer Abstraktion (Interface) ab.
    // ============================================================
    interface Tastatur { void tippen(); }

    static class MechanischeTastatur implements Tastatur {
        public void tippen() { System.out.println("Klick-Klack"); }
    }

    static class Computer {
        private final Tastatur tastatur; // Hängt vom Interface ab, nicht vom Modell!
        
        Computer(Tastatur t) { this.tastatur = t; }
        
        void nutzen() { tastatur.tippen(); }
    }

    public static void main(String[] args) {
        // DRY - Don't Repeat Yourself
        // Beispiel: Statt Code zu kopieren, nutzen wir Methoden oder Hilfsklassen.
        System.out.println("SOLID Prinzipien Demonstration");
        
        // DIP Beispiel
        Tastatur meineTastatur = new MechanischeTastatur();
        Computer meinPC = new Computer(meineTastatur);
        meinPC.nutzen();
        
        // OCP Beispiel
        List<Shape> shapes = Arrays.asList(new Rectangle(), new Circle());
        // Hier könnte man einfach durchschleifen, egal welche Form.
    }
}
