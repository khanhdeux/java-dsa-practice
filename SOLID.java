import java.util.*;

/**
 * Kategorie: Object-Oriented Design / SOLID
 * Strategie: Wartbarer, testbarer und erweiterbarer Code.
 */
public class SOLID {

    public static void main(String[] args) {

    }
}

/**
 * S - Single Responsibility Principle (SRP)
 * Eine klasse -> ein Grund zu ändern
 * Eine Klasse -> ein Job
 */

// WRONG
// viele Aufgaben: DB speicheirn, Email schicken, report generien => schwierige
// wartbarkeit
class _UserService {
    public void saveUser(User user) {
        // Save database
    }

    public void sendEmail(User user) {
        // Send email
    }

    public void generateReport() {
        // export pdf
    }
}

// CORRECT
class UserService {
    public void saveUser(User user) {
        // save DB
    }
}

class EmailService {
    public void saveEmail(User user) {
        // send Email
    }
}

class ReportService {
    public void generateReport() {
        // export pdf
    }
}

/**********************************/

/**
 * O - Open/Closed Principle (OCP)
 * Open: erweiterung // Closed: implementierung
 * -> neue Feature ohne alte code änderung
 * E.g Framework plugins
 */

// WRONG
// Neue Payment methode -> Klasse ändern
class _PaymentService {
    public void pay(String type) {
        if (type.equals("PAYPAL")) {
            System.out.println("Paypal");
        }

        if (type.equals("VISA")) {
            System.out.println("Visa");
        }
    }
}

// CORRECT
interface PaymentMethod {
    void pay();
}

class PaypalPayment implements PaymentMethod {
    @Override
    public void pay() {
        System.out.println("Paypal");
    }
}

class VisaPayment implements PaymentMethod {
    @Override
    public void pay() {
        System.out.println("Visa");
    }
}

class PaymentService {
    public void process(PaymentMethod paymentMethod) {
        paymentMethod.pay();
    }
}

// USAGE:
PaymentService paymentService = new PaymentService();
paymentService.process(new VisaPayment());

// Neue Methode
// PaymentService: keine Änderung
class CryptoPayment implements PaymentMethod {
    public void pay() {
        System.out.println("Crypto");
    }
}
/**********************************/

/**
 * L - Liskov Substitution Principle (LSP)
 * - Child Klasse muss Elternklasse ohne Problem ersezten
 * - Child muss desselbe verhalten wie Eltern
 */

//WRONG
// Penguin: nicht fliegen
class _Bird {
    void fly() {}
}
class _Penguin extends _Bird {
    void fly() {
        throw new UnsupportedOperationException();
    }
}

// CORRECT
interface Bird {}
interface FlyingBird extends Bird {
    void fly() {};
}

class Eagle implements FlyingBird {
    public void fly() {}
}

class Penguin implements Bird {}

/**********************************/
/**
 * I - Interface Segregation Principle (ISP)
 * Klasse: nicht gezwungen, unnötige Funktionen zu implementieren
 * kleine + gezielte Interfaces
 */

// WRONG
interface _Worker {
    void work();
    void eat();
}
// Robot: kein Eat()
class _RobotWorker implements _Worker{
    public void work() {}
    public void eat() {
        throw new UnsupportedOperationException();
    }
}

// CORRECT
interface Workable {
    void work();
}
interface Eatable {
    void eat();
}

class Human implements Workable, Eatable {
    public void work() {}
    public void eat() {}
}
class RobotWorker implements Workable {
    public void work() {}
}

/**********************************/
/**
 * D - Dependency Inversion Principle (DIP)
 * High-level module: konkrete Klasse unabhängig & abstraktion/interface abhängig
 */

// WRONG
// PosgresSQL Ändern
// Mock test
class _MySQLDatabase {
    void connect() {}
}
class _UserService {
    private _MySQLDatabase db = new _MySQLDatabase();
}

// CORRECT
interface Database {
    void connect();
}

class MySQLDatabase implements Database {
    public void connect() {}
}

class UserService {
    private Database db;
    public UserService(Database db) {
        this.db = db;
    }
}

// USAGE
UserService userService = new UserService(new MySQLDatabase());
userService.connect();