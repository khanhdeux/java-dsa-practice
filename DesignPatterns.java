import java.util.*;

/**
 * Design patterns
 * - CREATION: object: SINGLETON, FACTORY, BUILDER
 * - STRUCTURE: klasse/object: ADAPTER, DECORATOR, PROXY
 * - BEHAVIOUR: kommunikation: STRATEGY, OBSERVER, STATE, TEMPLATE, COMMAND
 */
public class DesignPatterns {

    // ============================================================
    // 1. CREATIONAL: Objekterzeugung
    // ============================================================

    /**
     * SINGLETON
     * - nur ein Instanz
     * - E.g ConfigManager, Logger, Connection Pool, Cache
     * - (-): unittest, hidden dependency, like-global variable
     */

    // WRONG
    // redundante objekte
    class _ConfigService {
    }

    _ConfigService config1 = new _ConfigService();
    _ConfigService config2 = new _ConfigService();

    // CORRECT
    class ConfigService {
        private static ConfigService instance;

        private ConfigService() {
        }

        private static ConfigService getInstance() {
            if (instance == null) {
                instance = new ConfigService();
            }

            return instance;
        }
    }

    // USAGE
    ConfigService config = ConfigService.getInstance();

    /***************/

    /**
     * FACTORY
     * - Problem: "new" konkrete Klassen überall
     * - Factory: obj generien
     */

    // WRONG
    if(type.equals("PDF"))
    {
        return new PdfExporter();
    }if(type.equals("EXCEL"))
    {
        return new ExcelExporter();
    }

    // CORRECT
    interface Exporter {
        void export();
    }

    class PdfExporter implements Exporter {
        public void export() {
        }
    }

    class ExcelExporter implements Exporter {
        public void export() {
        }
    }

    class ExporterFactory {
        public static Exporter create(String type) {
            switch (type) {
                case "PDF":
                    return new PdfExporter();
                case "EXCEL":
                    return new ExcelExporter();
                default:
                    throw new RuntimeException();
            }
        }
    }

    // USAGE
    PdfExporter pdfExporter = ExporterFactory.create("PDF");

    /**
     * BUILDER
     * - Problem: obj -> viele params im Constructor
     * - Schrit für Schritt aufbauen
     */

    // WRONG
    User user = new User("Khanh", 30, "Germany", true, false, "ADMIN");

    // CORRECT
    class User {
        private String name;
        private int age;

        public static class Builder {

            private User user = new User();

            public Builder name(String name) {
                user.name = name;
                return this;
            }

            public Builder age(int age) {
                user.age = age;
                return this;
            }

            public User build() {
                return user;
            }
        }
    }

    // USAGE
    User user = new User.Builder()
            .name("Khanh")
            .age(30)
            .build();

    // ============================================================
    // 2. STRUCTURAL: Struktur
    // ============================================================

    /**
     * ADAPTER
     * - 2 interfaces: nicht kompatible
     */

    // Current: OldPaymentApi
    // Neues System: NewPaymentProcessor
    class PaymentAdapter implements NewPaymentProcessor {
        private OldPaymentAPi oldPaymentAPi;

        public PaymentAdapter(OldPaymentAPi oldPaymentAPi) {
            this.oldPaymentAPi = oldPaymentAPi;
        }

        public void pay() {
            oldPaymentAPi.makePayment();
        }
    }

    /**
     * DECORATOR
     * - mehr verhalten ohne originale Änderung im aktuellen Code
     * - E.g Coffee: basic coffee, + Milch, + Zucker
     */
    interface Coffee {
        String make();
    }

    class BasicCoffee implements Coffee {
        public String make() {
            return "Coffee";
        }
    }

    class MilkDecorator implements Coffee {
        private Coffee coffee;

        public MilkDecorator(Coffee coffee) {
            this.coffee = coffee;
        }
        public String make() {
            return coffee.make() + "+ Milk";
        }
    }

    // USAGE
    Coffee coffee = new MilkDecorator(new BasicCoffee());
    System.out.println(coffee.make());


    /**
     * PROXY
     * - Man in middle: Zugriff kontrolliern
     * - Obj vertrete
     * E.g: lazy loading, security, logging, transaction, caching, rate limit
     * Spring @Transactional: Spring generiert UserServiceProxy: open transaktion -> call UserService -> commit()
     * 
     */
    // WRONG
    class _VideoService {
        public void loadVideo() {
            System.out.println("Loading huge video...");
        }
    }

    // CORRECT
    interface VideoService {
        void loadVideo();
    }
    class RealVideoService implements VideoService {
        public void loadVideo() {
            System.out.println("Loading huge video...");
        }
    }
    class VideoProxy implements VideoService {
        private RealVideoService realVideoService;

        public void loadVideo() {
            if (realVideoService == null) {
                realVideoService = new RealVideoService();
            }

            System.out.println("Check permission");
            realVideoService.loadVideo();
        }
    }

    // USAGE
    VideoService videoService = new VideoProxy();
    videoService.loadVideo();

    // ============================================================
    // 3. BEHAVIOURAL: Kommunikation
    // ============================================================

    /**
     * STRATEGY
     * - Problem: algorithmen im laufzeit ändern
     * - if-else hell
     * - Verhalten Änderung im Laufzeit
     * - E.g payment, sorting, datasource processing, discount, validation
     */

    // WRONG
    class _PaymentService {
        public void pay(String type) {
            if (type.equals("PAYPAL")) {
            } else if (type.equals("VISA")) {
            } else if (type.equals("CRYPTO")) {
            }
        }
    }

    // CORRECT
    interface PaymentStrategy {
        void pay();
    }

    class PaypalStrategy implements PaymentStrategy {
        public void pay() {
            System.out.println("Paypal");
        }
    }

    class VisaStrategy implements PaymentStrategy {
        public void pay() {
            System.out.println("Visa");
        }
    }

    class PaymentService {
        private PaymentStrategy strategy;

        public PaymentService(PaymentStrategy paymentStrategy) {
            this.strategy = paymentStrategy;
        }

        public void process() {
            this.strategy.pay();
        }
    }

    // USAGE;
    PaymentService paymentService = new PaymentService(new PaypalStrategy());paymentService.process();

    /**
     * OBSERVER
     * - ein Obj ändert -> viele andere Obj benachrichtigt
     * - E.g Websocket, Event system, Notifikation, Angular RxJS, Kafka Consummer
     */
    interface Observer {
        void update(String message);
    }

    class UserClient implements Observer {
        private String name;

        public UserClient(String name) {
            this.name = name;
        }

        public void update(String message) {
            System.out.println(name + ":" + message);
        }
    }

    class LayoutSubject {
        List<Observer> observers = new ArrayList<>();

        public void subscribe(Observer observer) {
            observers.add(observer);
        }

        public void notifyUsers(String msg) {
            for (Observer obs : observers) {
                obs.update(msg);
            }
        }
    }

    // USAGE
    LayoutSubject layoutSubject = new LayoutSubject();
    layoutSubject.subscribe(new UserClient("A"));
    layoutSubject.subscribe(new UserClient("B"));
    layoutSubject.notifyUsers("Layout updated");

    /**
     * STATE
     *  - Problem: OrderProcessing: State: CREATED, PAID, ...if(order.status == PAID)
     * - State ändert -> Obj Verhalten ändert
     * - Jede State: ein seperates Obj
     * 
     */

    // WRONG
    // Zukünft: zusäzliche: maintaince mode, refund, multiple payments, error handling => if-else explosion
    class _VendingMachine {
        private String state;

        public void insertCoin() {
            if(state.equals("NO_COIN")) {
                System.out.println("Coin inserted");
                state = "HAS_COIN";
            }
            else if (state.equals("HAS_COIN")) {
                System.out.println("Already has coin");
            }
        }

        public void pressButton() {
            if(state.equals("HAS_COIN")) {
                System.out.println("Dispensing");
                state = "NO_COIN";
            }
        }
    }

    // CORRECT
    interface VendingState {
        void insertCoin();
        void pressButton();
    }
    class NoCoinState implements VendingState {
        private VendingMachine machine;

        public NoCoinState(VendingMachine machine) {
            this.machine = machine;
        }
        public void insertCoin() {
            System.out.println("Coin inserted");
            machine.setState(machine.getHasCoinState());
        }
        public void pressButton() {
            System.out.println("Insert coin first");
        }
    }
    class HasCoinState implements VendingState {
        private VendingMachine machine;

        public HasCoinState(VendingMachine machine) {
            this.machine = machine;
        }
        public void insertCoin() {
            System.out.println("Already has coin");
        }
        public void pressButton() {
            System.out.println("Dispensing item");
            machine.setState(machine.getNoCoinState());
        }        
    }
    class VendingMachine {
        private VendingState state;
        private VendingState noCoinState;
        private VendingState hasCoinState;

        public VendingMachine() {
            noCoinState = new NoCoinState(this);
            hasCoinState = new HasCoinState(this);
            state = noCoinState;
        }

        public void setState(VendingState state) {
            this.state = state;
        }

        public VendingState getNoCoinState() {
            return noCoinState;
        }

        public VendingState getHasCoinState() {
            return hasCoinState;
        }

        public void insertCoin() {
            state.insertCoin();
        }

        public void pressButton() {
            state.pressButton();
        }
    }

    // USAGE
    VendingMachine vendingMachine = new VendingMachine();
    vendingMachine.insertCoin();
    vendingMachine.pressButton();

    /**
     * TEMPLATE METHOD
     * - Problem: Code dupplikat
     * - Skeleton definieren, subklasse: paar schritte Unterschied
     * - gefixte Workflow
     */

    // WRONG
    class _TeaMaker {
        void make() {
            void boilWater();
            void addTea();
            void pour();
        }
    }

    class _CoffeeMaker {
        void make() {
            void boilWater();
            void addCoffee();
            void pour();
        }
    }

    // CORRECT
    abstract class BeverageMaker {
        public final void make() {
            boilWater();
            addIngredients();
            pour();
        }

        private void boilWater() {
            System.out.println("Boil water");
        }

        private void pour() {
            System.out.println("pour");
        }

        protected abstract void addIngredients();
    }

    class TeaMaker extends BeverageMaker{
        protected void addIngredients() {
            System.out.println("Add Tea");
        }        
    }
    class CoffeeMaker extends BeverageMaker {
        protected void addIngredients() {
            System.out.println("Add coffee");
        }
    }    

    // USAGE
    BeverageMaker teaMaker = new TeaMaker();
    teaMaker.make();

    /**
     * COMMAND
     * - Aktion -> Obj umgewaldet
     * - E.g Queue System: SendEmailCommand, GeneratePdfCommand, PushNotificationCommand
     *       Push -> Kafka. Worker: später verarbeiten
     *       Undo/Redo: DeleteTextCommand, InsertTextCommand
     *       Jobscheduler: BackupDatabaseCommand, CleanupCommand   
     */

    // WRONG
    // Button: braucht nicht zu wissen wie TV funktioniert
    // Remote: eng gekoppelt
    class _RemoteControl {
        private Tv tv;

        public void pressButton() {
            tv.turnOn();
        }
    }

    // CORRECT
    interface Command {
        void execute();
    }
    // Receiver
    class Tv {
        public void turnOn() {
            System.out.println("Tv ON");
        }
    }
    // Concrete command
    class TurnOnCommand implements Command {
        private Tv tv;
        public TurnOnCommand(Tv tv) {
            this.tv = tv;
        }
        public void execute() {
            tv.turnOn();
        }
    }
    // Invoker
    class RemoteControl {
        private Command command;
        public RemoteControl(Command command) {
            this.command = command;
        }
        private void pressButton() {
            command.execute();
        }
    }

    // USAGE
    Tv tv = new Tv();
    RemoteControl remoteControl = new RemoteControl(new TurnOnCommand(tv));
    remoteControl.pressButton();
}