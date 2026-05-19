import java.math.BigDecimal;

@Service
public class PensionService {

    @Autowired
    private PensionRepository repo;         // ❌ Field injection
    @Autowired
    private EmailService emailService;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private KafkaTemplate kafka;
    @Autowired
    private AuditService auditService;

    public void process(Long id) {            // ❌ Zu viele Responsibilities
        Pension p = repo.findById(id).get();  // ❌ NoSuchElementException möglich
        p.setStatus("ACTIVE");                // ❌ Magic String
        repo.save(p);
        emailService.sendConfirmation(p);
        pdfService.generate(p);
        kafka.send("pension-events", p);
        auditService.log("PROCESSED", p.getId());
        // 150 weitere Zeilen...
    }
}

// ✅ 1. Constructor Injection (testbar, immutable)
// ✅ 2. Single Responsibility: process() macht zu viel → Events
// ✅ 3. Optional statt .get()
// ✅ 4. Enum oder Konstante statt Magic String
// ✅ 5. Kafka-Publish reicht → Email/PDF reagieren auf Event
// Single-Responsibility // Event-Driven entkopplen

@Service
@RequiredArgsConstructor
public class PensionService {

    private final PensionRepository repo;
    private final ApplicationEventPublisher events;

    @Transactional
    public Pension activate(Long id) {
        Pension p = repo.findById(id)
            .orElseThrow(() -> new PensionNotFoundException(id));
        p.activate();  // Status-Logik im Entity
        events.publishEvent(new PensionActivatedEvent(p));
        return p;
    }
}


public BigDecimal berechne(Vertrag v) {
    if (v.getProdukt().equals("bAV_DIREKT")) {      // ❌
        return v.getGehalt().multiply(new BigDecimal("0.04"));
    } else if (v.getProdukt().equals("bAV_ENTGELT")) {
        return v.getGehalt().multiply(new BigDecimal("0.06"));
    } else if (v.getProdukt().equals("bAV_RIESTER")) {
        return v.getGehalt().multiply(new BigDecimal("0.02"))
               .add(new BigDecimal("175"));  // Riester-Zulage
    } else if (v.getProdukt().equals("bAV_KOMBI")) {
        // 30 Zeilen Speziallogik...
    }
    throw new IllegalArgumentException("Unbekannt");
}

// Strategy Pattern -> Open/Closed Principle
// Strategy Interface
public interface BeitragStrategie {
    BigDecimal berechne(Vertrag vertrag);
    ProduktTyp getProduktTyp();
}

// Jede Implementierung in eigener Klasse → Open/Closed Principle
@Component
public class BavDirektStrategie implements BeitragStrategie {
    private static final BigDecimal SATZ = new BigDecimal("0.04");
    public BigDecimal berechne(Vertrag v) {
        return v.getGehalt().multiply(SATZ);
    }
    public ProduktTyp getProduktTyp() { return ProduktTyp.BAV_DIREKT; }
}

// Registry in Spring — kein weiteres if-else je wieder nötig
@Service
@RequiredArgsConstructor
public class BeitragService {
    private final Map<ProduktTyp, BeitragStrategie> strategien;

    @Autowired
    public BeitragService(List<BeitragStrategie> alle) {
        strategien = alle.stream().collect(
            Collectors.toMap(BeitragStrategie::getProduktTyp, s -> s));
    }

    public BigDecimal berechne(Vertrag v) {
        return strategien
            .getOrDefault(v.getProduktTyp(), this::fehler)
            .berechne(v);
    }
}

// Schlechte Variante (telescoping constructor anti-pattern):
new Vertrag(id, arbeitgeberId, vermittlerId, produkt, gehalt,
           beitragssatz, startDatum, endDatum, null, null, true, false);

// Builder-Pattern
// @Builder vom Lombok
// Senior-Lösung: Builder mit Validation
public class Vertrag {

    private final Long arbeitgeberId;
    private final ProduktTyp produkt;
    private final BigDecimal gehalt;
    private final BigDecimal beitragssatz;
    private final LocalDate startDatum;

    private Vertrag(Builder b) { /* alle Felder setzen */ }

    public static class Builder {
        private Long arbeitgeberId;
        private ProduktTyp produkt;
        private BigDecimal beitragssatz = new BigDecimal("0.04"); // Default

        public Builder arbeitgeber(Long id) {
            this.arbeitgeberId = id; return this;
        }
        public Builder produkt(ProduktTyp p) {
            this.produkt = p; return this;
        }
        public Vertrag build() {
            Objects.requireNonNull(arbeitgeberId, "Arbeitgeber fehlt");
            Objects.requireNonNull(produkt, "Produkt fehlt");
            return new Vertrag(this);
        }
    }
}

// Verwendung — selbsterklärend:
Vertrag v = new Vertrag.Builder()
    .arbeitgeber(42L)
    .produkt(ProduktTyp.BAV_DIREKT)
    .build();

// ❌ Service ruft Service direkt — enge Kopplung
public void aktiviere(Long vertragsId) {
    vertragService.aktiviere(vertragsId);
    emailService.sendeBestaetigung(vertragsId);     // sync
    smsService.sendeBenachrichtigung(vertragsId);   // sync
    pdfService.erstelleDokument(vertragsId);        // sync
    reportingService.aktualisiere(vertragsId);      // sync
}

// ✅ Schritt 1: Event publishen (einzige Responsibility)
@Transactional
public Vertrag aktiviere(Long id) {
    Vertrag v = repo.findById(id).orElseThrow(...);
    v.aktiviere();
    // Outbox Pattern: Event wird in DB gespeichert,
    // Kafka-Publish passiert nach Commit
    eventPublisher.publish(new VertragAktiviertEvent(v));
    return repo.save(v);
}

// ✅ Schritt 2: Listener reagieren unabhängig
@KafkaListener(topics = "vertrag-events")
public class EmailNotificationListener {
    public void onAktiviert(VertragAktiviertEvent e) {
        emailService.sende(e.getVertrag());
    }
}

@KafkaListener(topics = "vertrag-events")
public class PdfGenerationListener { /* ... */ }

// Vorteile: lose Kopplung, unabhängig skalierbar,
// neue Listener ohne Änderung am Core-Service