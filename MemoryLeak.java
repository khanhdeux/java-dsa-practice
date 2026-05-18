import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemoryLeak {

    public class MemoryLeakDemo {
        // HÄSSLICH: Diese statische Liste hält alle Objekte für IMMER im RAM fest.
        // Selbst wenn der User sich ausloggt, bleibt die Referenz hier drin bestehen!
        private static final List<User> userCache = new ArrayList<>();

        public static void main(String[] args) throws InterruptedException {
            System.out.println("Simuliere App-Laufzeit mit Memory Leak...");

            for (int i = 0; i < 1_000_000; i++) {
                User user = new User("User_" + i);
                userCache.add(user); // User wird im Cache geparkt

                // Hier im Code wird der User eigentlich nicht mehr gebraucht.
                // Die Variable 'user' verlässt gleich den Scope, ABBER:
                // Er "lebt" in der userCache-Liste unbemerkt weiter!

                if (i % 100_000 == 0) {
                    System.out.println(i + " User im Speicher angehäuft...");
                    Thread.sleep(100); // Simuliert Zeitverlauf
                }
            }

            // Der GC läuft hier komplett ins Leere – er darf nichts löschen!
            System.gc();
            System.out.println("Crash naht: Speicher ist voll mit ungenutzten Usern.");
        }
    }

    class User {
        private String name;
        private byte[] heavyData = new byte[1024 * 10]; // 10 KB Daten pro User

        public User(String name) {
            this.name = name;
        }
    }

    public class MemoryLeakFixed {
        // SAUBER: Eine WeakHashMap hält Keys nur so lange, wie sie woanders aktiv
        // gebraucht werden.
        // Sobald die Session des Users vorbei ist, löscht der GC den Eintrag
        // automatisch!
        private static final Map<UserFixed, String> userCache = new WeakHashMap<>();

        public static void main(String[] args) {
            System.out.println("Simuliere sauberes Speichermanagement...");

            UserFixed user1 = new UserFixed("Kevin");
            userCache.put(user1, "ACTIVE_SESSION");

            System.out.println("Cache Größe vor GC: " + userCache.size()); // 1

            // Wir simulieren das Ende der Nutzung: Wir setzen die Haupt-Referenz auf null
            user1 = null;

            // Wir bitten den Garbage Collector explizit zu kommen (nur für diese Demo!)
            System.gc();

            // Der GC hat gemerkt: Niemand außer der WeakHashMap nutzt 'user1'.
            // Also wurde er gelöscht und der Cache wurde automatisch aufgeräumt!
            System.out.println("Cache Größe nach GC: " + userCache.size()); // Garantiert 0!
        }
    }

    class UserFixed {
        private String name;

        public UserFixed(String name) {
            this.name = name;
        }
    }

    /**
     * Nicht geschlossene Resourcen
     */
    public class ResourceLeakProblem {
        public static void main(String[] args) {
            try {
                // Wir öffnen einen Stream zur Datei
                BufferedReader reader = new BufferedReader(new FileReader("config.txt"));

                String line = reader.readLine();
                System.out.println("Inhalt: " + line);

                // Wenn hier drüber ein Fehler passiert, wird dieser Aufruf übersprungen!
                // Das ist ein klassisches Resource Leak.
                reader.close();

            } catch (IOException e) {
                System.out.println("Fehler beim Lesen, Datei wurde NICHT geschlossen!");
            }
        }
    }

    /**
     * LÖSUNG: try-with-resources
     */
    public class ResourceLeakFixed {
        public static void main(String[] args) {
            // SAUBER: Die Ressource wird in den try-Klammern definiert
            try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {

                String line = reader.readLine();
                System.out.println("Inhalt sicher gelesen: " + line);

            } catch (IOException e) {
                System.out.println("Selbst wenn ein Fehler passiert: Die Datei ist JETZT SCHON geschlossen!");
            }
            // Kein manuelles .close() mehr nötig. Java hat das im Hintergrund erledigt!
        }
    }

    public class InnerClassLeakProblem {
        private static final ExecutorService scheduler = Executors.newScheduledThreadPool(1);

        public static void main(String[] args) throws InterruptedException {
            System.out.println("Lade schweren Screen...");
            HeavyScreen screen = new HeavyScreen();
            screen.startAnimation();

            // Wir werfen den Screen weg (denken wir zumindest!)
            screen = null;

            // Wir zwingen den GC zum Aufräumen
            System.gc();
            Thread.sleep(1000);

            // PROBLEM: Obwohl screen = null ist, wurde "HeavyScreen zerstört" NIE gedruckt!
            // Der geplante Hintergrund-Task hält das gesamte Riesen-Objekt im Speicher.
        }
    }

    class HeavyScreen {
        // Simuliert 20 Megabyte Bilddaten im RAM
        private byte[] memoryLoad = new byte[1024 * 1024 * 20];

        public void startAnimation() {
            // HÄSSLICH: Dieses anonyme Runnable hält im Hintergrund
            // die Referenz "HeavyScreen.this" gefangen!
            InnerClassLeakProblem.scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Animation läuft im Hintergrund...");
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        // Wird vom GC gerufen, wenn das Objekt wirklich aus dem RAM fliegt
        @Override
        protected void finalize() {
            System.out.println("SUCCESS: HeavyScreen wurde aus dem RAM gelöscht!");
        }
    }

    public class InnerClassLeakFixed {
        private static final ExecutorService scheduler = Executors.newScheduledThreadPool(1);

        public static void main(String[] args) throws InterruptedException {
            System.out.println("Lade sauberen Screen...");
            HeavyScreenFixed screen = new HeavyScreenFixed();
            screen.startAnimation();

            // Wir lassen den Screen los
            screen = null;

            // GC triggern
            System.gc();
            Thread.sleep(1000);

            // ERGEBNIS: "SUCCESS: HeavyScreenFixed wurde aus dem RAM gelöscht!" wird
            // gedruckt!
            // Der Speicher ist frei, obwohl der Task im Hintergrund ungeniert weiterläuft.
        }
    }

    class HeavyScreenFixed {
        private byte[] memoryLoad = new byte[1024 * 1024 * 20];

        public void startAnimation() {
            // SAUBER: Wir übergeben eine statische Klasse.
            // Diese weiß absolut nichts von den 20 MB Daten der äußeren Klasse.
            InnerClassLeakFixed.scheduler.scheduleAtFixedRate(new SafeTask(), 0, 1, TimeUnit.SECONDS);
        }

        // static entkoppelt die innere Klasse komplett von der äußeren Instanz!
        private static class SafeTask implements Runnable {
            @Override
            public void run() {
                System.out.println("Sicherer Task läuft ohne Speicherleck...");
            }
        }

        @Override
        protected void finalize() {
            System.out.println("SUCCESS: HeavyScreenFixed wurde aus dem RAM gelöscht!");
        }
    }

    public class MemoryAllocationDemo {

        public void calculate() {
            // 'localValue' liegt direkt auf dem STACK.
            // Wenn die Methode endet, ist diese 42 SOFORT weg.
            int localValue = 42;

            // 'data' (die Referenz/der Zettel) liegt auf dem STACK.
            // Das echte 'new HeavyObject()' liegt aber im großen HEAP!
            HeavyObject data = new HeavyObject();

            System.out.println("Methode wird gleich verlassen...");
        } // <-- STOPP! Hier endet die Methode.

        // WAS PASSIERT JETZT IM SPEICHER?
        // 1. Der Stack-Bereich dieser Methode wird gelöscht. 'localValue' und 'data'
        // sind WEG.
        // 2. PROBLEM: Das 'HeavyObject' liegt immer noch unberührt im HEAP!
        // Es ist jetzt "Müll", weil wir den Zettel ('data') verloren haben.
        // Es blockiert so lange den RAM, bis die Garbage Collection irgendwann
        // vorbeikommt.
    }

    class HeavyObject {
        private byte[] load = new byte[1024 * 1024]; // 1 MB Größe
    }

    public static void main(String[] args) {

    }
}