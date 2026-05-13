import java.util.*;

/**
 * DIE FINALE DESIGN-PATTERN-SAMMLUNG (Problem vs. Lösung)
 */
public class DesignPatterns {

    // ============================================================
    // 1. CREATIONAL: Objekterzeugung
    // ============================================================

    /**
     * SINGLETON
     */
    // HÄSSLICH: Jede Klasse macht "new Datenbank()". 100 Services = 100 offene Verbindungen.
    static class BadService { Datenbank db = new Datenbank(); }

    // SAUBER: Nur eine Instanz kontrolliert den Zugriff.
    static class Datenbank {
        private static Datenbank instanz;
        private Datenbank() {} 
        public static Datenbank getInstance() {
            if (instanz == null) {
                synchronized (Datenbank.class) {
                    if (instanz == null) instanz = new Datenbank();
                }
            }
            return instanz;
        }
    }

    /**
     * FACTORY
     */
    // HÄSSLICH: if-else Hölle. Wenn "WhatsApp" kommt, musst du den Service ändern.
    public void badVersenden(String typ) {
        if (typ.equals("email")) new Email().senden();
        else if (typ.equals("sms")) new Sms().senden();
    }

    // SAUBER: Factory übernimmt Entscheidung; Service bleibt stabil.
    interface Message { void senden(); }
    static class Email implements Message { public void senden() { System.out.println("Sende Email"); } }
    static class Sms implements Message { public void senden() { System.out.println("Sende SMS"); } }

    static class MessageFactory {
        public static Message erstellen(String typ) {
            if (typ.equals("email")) return new Email();
            if (typ.equals("sms")) return new Sms();
            return null;
        }
    }

    /**
     * BUILDER
     */
    // HÄSSLICH: "Telescoping Constructor" - Man weiß nicht, welcher String was ist.
    // User u = new User("Khanh", "k@mail.com", "Hanoi", 25, true);

    // SAUBER: Schrittweiser Aufbau.
    static class User {
        String name, email;
        static class Builder {
            private User u = new User();
            public Builder name(String n) { u.name = n; return this; }
            public Builder email(String e) { u.email = e; return this; }
            public User build() { return u; }
        }
    }

    // ============================================================
    // 2. STRUCTURAL: Struktur
    // ============================================================

    /**
     * ADAPTER
     */
    // HÄSSLICH: Stripe stripe = new Stripe(); stripe.executePayment();
    // Überall steht "Stripe". Wenn PayPal kommt, musst du 50 Dateien ändern.

    // SAUBER: Adapter "übersetzt" den Aufruf auf dein Interface.
    interface Zahlungsart { void bezahlen(); }
    static class Stripe { void executePayment() { System.out.println("Stripe bezahlt"); } }

    static class StripeAdapter implements Zahlungsart {
        private Stripe s = new Stripe();
        public void bezahlen() { s.executePayment(); }
    }

    /**
     * DECORATOR
     */
    // HÄSSLICH: Vererbungshölle (KaffeeMitMilch, KaffeeMitZucker, KaffeeMitMilchUndZucker...).
    // class MilchKaffee extends Kaffee { ... }

    // SAUBER: Dynamisch "umwickeln" (Composition over Inheritance).
    interface Kaffee { double preis(); }
    static class Einfach implements Kaffee { public double preis() { return 2.0; } }

    static class MilchDecorator implements Kaffee {
        private Kaffee k;
        public MilchDecorator(Kaffee k) { this.k = k; }
        public double preis() { return k.preis() + 0.5; }
    }

    /**
     * PROXY
     */
    // HÄSSLICH: Jedes Mal direkt in die langsame DB greifen.
    // method laden() { return db.query("SELECT..."); }

    // SAUBER: Proxy prüft erst den Cache.
    static class DatenbankProxy {
        private Map<Integer, String> cache = new HashMap<>();
        public String laden(int id) {
            if (cache.containsKey(id)) return "Cache: " + cache.get(id);
            return "Daten aus DB"; 
        }
    }

    // ============================================================
    // 3. BEHAVIOURAL: Kommunikation
    // ============================================================

    /**
     * STRATEGY
     */
    // HÄSSLICH: Algorithmus fest verbaut (Hardcoded if-else).
    // if (m.equals("express")) porto = 15; else porto = 5;

    // SAUBER: Austauschbare Strategien.
    interface Versand { double kosten(); }
    static class Express implements Versand { public double kosten() { return 15.0; } }

    static class Warenkorb {
        private Versand v;
        public void setVersand(Versand v) { this.v = v; }
        public void check() { System.out.println("Kosten: " + v.kosten()); }
    }

    /**
     * OBSERVER
     */
    // HÄSSLICH: Bestellung muss jeden Service händisch aufrufen.
    // void fertig() { emailService.send(); lager.update(); rechnung.create(); }

    // SAUBER: Lose Kopplung. Bestellung feuert Event, wer zuhört ist egal.
    interface Observer { void update(); }
    static class MailService implements Observer { public void update() { System.out.println("Mail raus!"); } }

    static class Bestellung {
        private List<Observer> subs = new ArrayList<>();
        public void add(Observer o) { subs.add(o); }
        public void abschliessen() { subs.forEach(Observer::update); }
    }

    /**
     * STATE
     */
    // HÄSSLICH: Riesige switch-Blöcke in jeder Methode prüfen den Status.
    // void handle() { switch(status) { case NEU: ... case BEZAHLT: ... } }

    // SAUBER: Jeder Zustand ist eine eigene Klasse.
    interface Status { void handle(); }
    static class NeuStatus implements Status { public void handle() { System.out.println("Warte auf Geld."); } }

    static class Context {
        private Status s = new NeuStatus();
        public void setStatus(Status s) { this.s = s; }
        public void request() { s.handle(); }
    }

    /**
     * TEMPLATE METHOD
     */
    // HÄSSLICH: CSV-Export und PDF-Export kopieren 90% des Codes (Loggen, Laden, Schließen).
    
    // SAUBER: Oberklasse definiert Ablauf, Unterklasse nur das Format-Detail.
    static abstract class Exporter {
        public final void export() { log(); write(); }
        private void log() { System.out.println("Start"); }
        protected abstract void write();
    }
    static class CsvExporter extends Exporter { protected void write() { System.out.println("CSV"); } }

    /**
     * COMMAND
     */
    // HÄSSLICH: Button führt Aktion direkt aus. Kein Undo möglich.
    // onClick() { user.delete(); }

    // SAUBER: Kapsle Aktion in Objekt.
    interface Command { void execute(); void undo(); }
    static class DeleteCommand implements Command {
        public void execute() { System.out.println("Gelöscht"); }
        public void undo() { System.out.println("Wieder da"); }
    }

    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {
        // Strategy Test
        Warenkorb w = new Warenkorb();
        w.setVersand(new Express());
        w.check();

        // Observer Test
        Bestellung b = new Bestellung();
        b.add(new MailService());
        b.abschliessen();
    }
}