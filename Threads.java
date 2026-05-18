import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Threads {
    // Problem: Thread A -> Wert in Hautspeicher ändern. Thread B: weiss nicht +
    // private Kopie arbeiet
    public class VisibilityProblem {
        // Ohne volatile: Der Thread könnte diesen Wert im Cache "einfrieren"
        private static boolean stopRequested = false;

        public static void main(String[] args) throws InterruptedException {
            Thread worker = new Thread(() -> {
                while (!stopRequested) {
                    // Der Thread liest stopRequested evtl. nur einmal am Anfang
                    // und schaut nie wieder in den echten RAM.
                }
                System.out.println("Worker: Ich habe den Stop-Befehl endlich gesehen!");
            });

            worker.start();
            Thread.sleep(1000); // Wir warten 1 Sekunde

            System.out.println("Main: Ich setze stopRequested jetzt auf true...");
            stopRequested = true;

            // PROBLEM: In vielen Fällen wird das Programm hier nie "Worker gestoppt"
            // drucken,
            // weil der Worker-Thread in seiner eigenen Welt (Cache) bleibt.
        }
    }

    // Speicher diese variable: niemals im privaten Cache + in RAM
    public class VisibilityFixed {
        // volatile garantiert: Jede Änderung ist SOFORT für alle Threads sichtbar.
        private static volatile boolean stopRequested = false;

        public static void main(String[] args) throws InterruptedException {
            Thread worker = new Thread(() -> {
                while (!stopRequested) {
                    // Dank volatile schaut der Thread bei jedem Durchlauf in den RAM.
                }
                System.out.println("Worker: Stop erhalten! Ich beende mich.");
            });

            worker.start();
            Thread.sleep(1000);

            System.out.println("Main: Setze Stop...");
            stopRequested = true; // Diese Änderung wird sofort "durchgereicht".
        }
    }

    // Race Condition: Thread A -> liest 0, Thread B -> liest 0, Thread A ->
    // schreibt 1, ThreadB -> schreib 1
    // -> Ergebnis: 1. Erwartet: 2: zweimal erhöht
    public class CounterProblem {
        private static int counter = 0; // i++ Problem

        public static void main(String[] args) throws InterruptedException {
            Runnable task = () -> {
                for (int i = 0; i < 1000; i++) {
                    counter++; // Nicht atomar!
                }
            };

            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // ERGEBNIS: Oft 1542 oder 1890 statt 2000.
            System.out.println("Unerwarteter Wert: " + counter);
        }
    }

    // LÖSUNG: Technique der CPU: Compare & Swap
    public class CounterFixed {
        // AtomicInteger macht das Hochzählen "atomar" (als ein Schritt)
        private static AtomicInteger counter = new AtomicInteger(0);

        public static void main(String[] args) throws InterruptedException {
            Runnable task = () -> {
                for (int i = 0; i < 1000; i++) {
                    counter.incrementAndGet(); // Sicherer, atomarer Schritt
                }
            };

            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // ERGEBNIS: Garantiert 2000!
            System.out.println("Korrektes Ergebnis: " + counter.get());
        }
    }

    /**
     * Eine Methode: Prüf Platz frei + wenn ja: buch Platz
     * Thread A: frei -> bucht
     * THread B: frei -> bucht
     * => überbucht -> Abfolge von Schritten
     * 
     * Syncrhonized: Tür: ein Thread eintritt -> verriegelt
     * Andere Thread: warten -> Tür aufgeht
     * PROBLEM: Ein thread schläft + ewig braucht -> warten alle anderen
     */
    public class BookingSystem {
        private int freeSeats = 1;

        // synchronized riegelt die ganze Methode ab
        public synchronized void bookSeat() {
            if (freeSeats > 0) {
                // Simuliere Bearbeitungszeit
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                freeSeats--;
                System.out.println("Platz gebucht! Rest: " + freeSeats);
            } else {
                System.out.println("Leider voll!");
            }
        }
    }

    /**
     * ReentrantLock: mehr Kontroller als synchronized
     * - Timetout: 5 sek -> brich ab
     * - Fairness: thread einstellen: langstenste Thread -> als Nächstes drankommt
     */
    public class SmartBooking {
        private final ReentrantLock lock = new ReentrantLock();
        private int freeSeats = 1;

        public void bookSeat() {
            try {
                // Versuche den Schlüssel zu bekommen, aber warte maximal 1 Sekunde
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        if (freeSeats > 0) {
                            freeSeats--;
                            System.out.println("Erfolgreich mit Lock gebucht!");
                        }
                    } finally {
                        // WICHTIG: Den Schlüssel immer zurückgeben, sonst ist die Tür für immer zu!
                        lock.unlock();
                    }
                } else {
                    System.out.println("System überlastet, bitte später versuchen.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread A: schnappt schlüssel 1 -> schlüssel 2
     * Thread B: schnappt schlüssel 2 -> schlüssel 1
     * 
     * LÖSUNG.
     * - Alle threads: gleiche Reihenfolge
     * - Timeout: x ewig warten -> wenn schlüssel nicht kommt -> schüssel loslassen
     * + versuchen später neu
     * - Shared state minimieren
     */
    public class DeadlockProblem {
        public static void main(String[] args) {
            Object lock1 = "Schlüssel 1";
            Object lock2 = "Schlüssel 2";

            // Thread A
            new Thread(() -> {
                synchronized (lock1) {
                    System.out.println("Thread A: Habe Schlüssel 1, warte auf 2...");
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                    synchronized (lock2) {
                        System.out.println("Thread A: Habe beide!");
                    }
                }
            }).start();

            // Thread B - DAS PROBLEM: Andere Reihenfolge!
            new Thread(() -> {
                synchronized (lock2) {
                    System.out.println("Thread B: Habe Schlüssel 2, warte auf 1...");
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                    synchronized (lock1) {
                        System.out.println("Thread B: Habe beide!");
                    }
                }
            }).start();
        }
    }

    /**
     * Thread in einer vorschleifer
     * Bank: Kunde -> einen Mitarbeier => 1000 Kunden -> 1000 Mitarbeite ->
     * OutOfMemory (System abstürtz)
     */
    public class ThreadCrashDemo {
        public static void main(String[] args) {
            // HÄSSLICH: Wir erstellen unkontrolliert neue Threads
            for (int i = 0; i < 10000; i++) {
                new Thread(() -> {
                    try {
                        Thread.sleep(10000); // Simuliert Arbeit
                    } catch (InterruptedException e) {
                    }
                }).start();
                // PROBLEM: Irgendwann sagt das Betriebssystem "Stopp!",
                // weil der RAM oder die CPU-Limits erreicht sind.
            }
        }
    }

    /**
     * Thread pool - ExecutorService
     * zb 10 Mitarbeite. wenn 10 Kunde -> 10 Mitarbeier
     * 10 Mitarbeite beschäftigt: Aufgabe -> aus der Warteschalnge.
     * Ein Mitarbeiter fertig -> nimmt näschte Aufgabe
     * 
     */
    public class ThreadPoolSolution {
        public static void main(String[] args) {
            // SAUBER: Ein Pool mit genau 10 Threads
            ExecutorService pool = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 10000; i++) {
                pool.submit(() -> {
                    // Aufgabe wird hier sicher verarbeitet
                    // Wenn alle 10 Threads voll sind, wartet diese Aufgabe in der Queue
                });
            }

            // WICHTIG: Den Pool am Ende schließen
            pool.shutdown();
        }
    }

    /**
     * Auto. 3 verschiedene Roboter
     * Haupt thread: weiss nicht -> Roboter fertig
     */
    public class LatchProblem {
        // Ein unfairer, globaler Zähler
        private static int finishedWorkers = 0;

        public static void main(String[] args) throws InterruptedException {
            for (int i = 0; i < 3; i++) {
                new Thread(() -> {
                    // Simuliere Arbeit
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }

                    finishedWorkers++; // Problem: ++ ist nicht atomar (siehe Punkt 2!)
                    System.out.println("Roboter fertig.");
                }).start();
            }

            // HÄSSLICH: Busy Waiting (Die CPU glüht, während wir blockieren)
            while (finishedWorkers < 3) {
                // Endlosschleife läuft, bis der Zähler 3 erreicht
            }

            System.out.println("Qualitätsprüfung: Alles bereit!");
        }
    }

    /**
     * Digitale Countdown-Zähler
     * am Anfang einstellen -> 3
     * jeder Thread fertig -> Knopf drücken
     * Hauptthread: await aufrufen. Schlafe bis Zähler -> 0 steht
     */
    public class LatchSolution {
        public static void main(String[] args) throws InterruptedException {
            // Wir starten den Countdown bei 3
            CountDownLatch latch = new CountDownLatch(3);

            for (int i = 0; i < 3; i++) {
                new Thread(() -> {
                    try {
                        System.out.println("Roboter arbeitet...");
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    System.out.println("Roboter finished");
                    latch.countDown(); // Zähler sinkt um 1
                }).start();
            }

            // Hauptthread wartet elegant, ohne CPU zu verschwenden
            latch.await();

            // Dieser Text kommt GARANTIERT erst, wenn alle 3 fertig sind
            System.out.println("All workers done. Qualitätsprüfung startet!");
        }
    }

    /**
     * Haupt-thread: langsame DB abfrage || externe API calls -> keine andere
     * Requests
     */
    public class FutureProblem {
        public static void main(String[] args) throws Exception {
            ExecutorService executor = Executors.newFixedThreadPool(1);

            // Der Kellner schickt den Auftrag in die Küche...
            Future<String> future = executor.submit(() -> {
                Thread.sleep(2000); // Simuliert langsamen API-Call
                return "Daten vom User-Service";
            });

            System.out.println("Main: Ich brauche das Ergebnis jetzt...");

            // HÄSSLICH: .get() blockiert den Haupt-Thread komplett!
            // Der Main-Thread schläft jetzt für 2 Sekunden und kann nichts anderes tun.
            String result = future.get();

            System.out.println("Main: Ergebnis erhalten: " + result);
            executor.shutdown();
        }
    }

    /**
     * CompleteableFuture: verketten Pipe
     * Haupt-Thread: niemals blockiert
     */
    public class FutureSolution {
        public static void main(String[] args) throws InterruptedException {
            System.out.println("Main: Ich starte die Pipeline und blockiere NICHT.");

            // SAUBER: Asynchron starten
            CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                return "Hello Future"; // Schritt 1: Daten laden
            })
                    .thenApply(data -> {
                        return data + " & verarbeitet"; // Schritt 2: Daten transformieren
                    })
                    .thenAccept(finalResult -> {
                        // Schritt 3: Was passiert, wenn alles fertig ist? (Callback)
                        System.out.println("Pipeline fertig! Ergebnis: " + finalResult);
                    });

            // Der Haupt-Thread kann hier sofort andere Dinge tun!
            System.out.println("Main: Ich bin frei und kann parallel andere Aufgaben machen...");

            // Nur damit das Konsolenprogramm nicht sofort beendet wird, bevor der async
            // Call fertig ist
            Thread.sleep(3000);
        }
    }

    public class ProducerConsumerProblem {
        private static final List<String> table = new ArrayList<>();

        public static void main(String[] args) {
            // Consumer (Kassierer)
            new Thread(() -> {
                while (true) {
                    // HÄSSLICH: Busy Waiting! Die CPU glüht in dieser Endlosschleife,
                    // weil ununterbrochen geprüft wird, ob die Liste leer ist.
                    if (!table.isEmpty()) {
                        String burger = table.remove(0);
                        System.out.println("Consumer: Burger gegessen: " + burger);
                    }
                }
            }).start();

            // Producer (Koch)
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Koch braucht 2 Sekunden
                    table.add("Cheeseburger");
                    System.out.println("Producer: Burger fertig!");
                } catch (Exception e) {
                }
            }).start();
        }
    }

    public class ProducerConsumerSolution {
        public static void main(String[] args) {
            // Eine Rutsche, die maximal 5 Burger halten kann
            BlockingQueue<String> table = new LinkedBlockingQueue<>(5);

            // Consumer (Kassierer)
            new Thread(() -> {
                try {
                    while (true) {
                        // SAUBER: Wenn die Queue leer ist, schläft dieser Thread
                        // völlig ressourcenschonend, bis .put() gerufen wird.
                        String burger = table.take();
                        System.out.println("Consumer: Burger abgeholt: " + burger);
                    }
                } catch (InterruptedException e) {
                }
            }).start();

            // Producer (Koch)
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    // Legt den Burger auf die Rutsche. Wenn voll, wartet er hier.
                    table.put("Smarter Burger");
                    System.out.println("Producer: Burger serviert!");
                } catch (InterruptedException e) {
                }
            }).start();
        }
    }

    public class ParallelProblem {
        public static void main(String[] args) throws InterruptedException {
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

            // HÄSSLICH: Manuelles Erstellen von Threads für eine einfache Liste
            Thread t1 = new Thread(() -> {
                // Verarbeite erste Hälfte...
            });
            Thread t2 = new Thread(() -> {
                // Verarbeite zweite Hälfte...
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            System.out.println("Manuell parallelisiert – anstrengend zu schreiben!");
        }
    }

    /**
     * Aufgaben -> alle verfügbare Kern CPU
     * Mathemetische Berechung || Sortierung größe Datenmengen im Speichern
     */
    public class ParallelSolution {
        public static void main(String[] args) {
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

            System.out.println("--- Starte parallele Verarbeitung ---");

            // SAUBER: Ein einziger Aufruf nutzt deine komplette CPU-Power!
            numbers.parallelStream()
                    .map(n -> {
                        System.out.println(Thread.currentThread().getName() + " verarbeitet: " + n);
                        return n * 2;
                    })
                    .forEach(System.out::println);
            // Hinweis: Die Ausgabe ist ungeordnet, da die Threads parallel arbeiten!
        }
    }

    public static void main(String[] args) {

    }
}