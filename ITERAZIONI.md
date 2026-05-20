# HackHub — Piano delle Iterazioni e Porting Spring Boot

> Documento di pianificazione che descrive l'intero arco di sviluppo del progetto **HackHub** (piattaforma per la gestione di hackathon — vedi traccia in `00-knowledge-base/00-traccia.md`).
> Serve sia come **memo per riprendere il lavoro** sia come **mappa per la commissione**.

---

## 1. Contesto e metodologia

Il progetto è organizzato secondo il **Unified Process iterativo** insegnato nel corso (rif. `docs/02_Processo.pdf`, slide _UP, attività e iterazioni_). Lo sviluppo procede in **4 iterazioni** seguite da una **fase di porting Spring Boot**.

Principio guida: ogni iterazione è **scelta per ridurre il rischio architetturale residuo più alto**. Non scegliamo le iterazioni per attore o per "fetta di funzionalità a tema": le scegliamo per **massimizzare l'apprendimento sul design** quanto prima.

**Stato della 1ª iterazione (questa repository):** ✅ completata, 27/27 test JUnit verdi, CLI end-to-end funzionante.

**Vincoli di traccia che strutturano l'intero piano:**

- Java puro, poi porting a Spring Boot.
- ≥ 2 design pattern diversi dal Singleton (questo progetto ne usa **4**: State, Strategy, Observer, Factory Method — vedi sezione 2).
- Strato di presentazione libero: in 1ª iter è CLI; dalla 2ª si valuterà se introdurre già un'API REST anticipando il porting.

---

## 2. Iterazione 1 — Vertical slice del ciclo di vita ✅ COMPLETATA

### Cosa è stato fatto

- **24 casi d'uso mappati**: 4 schedati in formato Cockburn _fully-dressed_ + 20 in formato _brief_ (vedi `01-analisi/01-casi-uso/`).
- **4 UC implementati** in codice + diagrammi:
  - UC02 [Crea Hackathon](01-analisi/01-casi-uso/UC02-CreaHackathon.md) (Organizzatore)
  - UC03 [Iscrivi Team a Hackathon](01-analisi/01-casi-uso/UC03-IscriviTeam.md) (Membro Team)
  - UC04 [Invia Sottomissione](01-analisi/01-casi-uso/UC04-InviaSottomissione.md) (Membro Team)
  - UC05 [Valuta Sottomissione](01-analisi/01-casi-uso/UC05-ValutaSottomissione.md) (Giudice)
- **Diagrammi UML** (PlantUML, importabili in Visual Paradigm):
  - 1 diagramma dei casi d'uso (`01-analisi/01-casi-uso/diagramma-use-case.puml`).
  - 1 diagramma classi di analisi BCE (`01-analisi/02-classi-analisi/classi-analisi.puml`).
  - 4 sequence diagram, uno per UC implementato (`01-analisi/03-sequenze/`).
  - 1 diagramma classi di progetto a 3 strati con i 4 pattern (`02-progetto/classi-progetto.puml`).
- **Codice Java 17**: 43 sorgenti + 6 classi di test in `03-codice/` (vedi `03-codice/README.md`).
- **4 design pattern**:
  - **State** — ciclo di vita Hackathon (`state/`)
  - **Strategy** — calcolo punteggio aggregato (`strategy/`)
  - **Observer** — propagazione eventi (`observer/`)
  - **Factory Method** — creazione polimorfa di notifiche (`factory/`)
- **CLI testuale** che esegue end-to-end UC02 → UC03 → UC04 → UC05 con dati demo precaricati.

### Perché questo scope (e non un altro)

> Il **rischio architetturale principale** del progetto è il **ciclo di vita dell'Hackathon** (4 stati con regole di accesso che variano per stato). Affrontarlo subito significa che tutti gli altri UC potranno **poggiare su un pattern State validato** invece di scoprirne i limiti a metà progetto.

- Lo slice attraversa **tutti e 4 gli stati** (InIscrizione → InCorso → InValutazione → Conclusa) e **tutti e 3 gli strati** dell'architettura (Boundary → Control → Model + Repository): è un _vertical slice_ in senso stretto.
- Mostra **l'integrazione tra i 4 pattern**: Observer+Factory Method propagano gli eventi che lo State genera al cambio di stato; Strategy si attiva quando lo State permette di valutare. Questa interazione è ciò che la commissione vorrà vedere.
- I 4 UC scelti coprono **3 dei 4 attori principali** (Organizzatore, Membro Team, Giudice) e **non** richiedono i sistemi esterni Calendar/Pagamento — rinviati a iter successive.

### Cosa è stato deliberatamente lasciato fuori

- **Autenticazione**, **registrazione**, **gestione team** (UC01, UC06, UC11–14): ortogonali al ciclo di vita → iter 2.
- **Workflow Mentore** (UC16–19) + integrazione **Calendar**: stress-test del Observer su eventi non legati al lifecycle → iter 3.
- **Proclamazione vincitore**, **pagamento**, **transizioni automatiche di stato** (UC09, UC10, UC22–24): chiusura del cerchio + secondo sistema esterno + scheduler → iter 4.

### Risultati misurabili

| Metrica                 | Valore                      |
| ----------------------- | --------------------------- |
| Casi d'uso definiti     | 24 (di cui 4 fully-dressed) |
| Casi d'uso implementati | 4                           |
| Sorgenti Java           | 43                          |
| Test JUnit              | 27 (passati: 27 / 27)       |
| Design pattern usati    | 4 (≠ Singleton)             |
| Diagrammi UML           | 7 `.puml`                   |

---

## 3. Iterazione 2 — Self-service del partecipante

### Obiettivo

Eliminare i "dati demo pre-caricati" e rendere il sistema **utilizzabile in autonomia** da un utente reale: registrazione → login → creazione team → inviti → iscrizione → sottomissione.

### Casi d'uso da implementare (8)

| ID   | Nome                                          | Attore                 |
| ---- | --------------------------------------------- | ---------------------- |
| UC01 | Registrazione                                 | Visitatore             |
| UC06 | Autenticazione                                | Utente / Membro Staff  |
| UC07 | Consulta Hackathon Pubblici                   | Visitatore             |
| UC11 | Crea Team                                     | Utente                 |
| UC12 | Invita Membri al Team                         | Membro Team (creatore) |
| UC13 | Accetta Invito Team                           | Utente                 |
| UC14 | Rifiuta Invito Team                           | Utente                 |
| UC15 | Aggiorna Sottomissione _(estensione di UC04)_ | Membro Team            |
| UC20 | Visualizza Sottomissioni                      | Membro Staff           |
| UC21 | Visualizza Tutti gli Hackathon                | Membro Staff           |

> Nota: gli UC sono 10 in tabella ma raggruppati in 8 unità funzionali (UC13/14 sono speculari, UC20/21 sono entrambi viewer).

### Perché in 2ª iterazione

- L'**autenticazione (UC06)** è inclusa da quasi tutti gli altri UC (`<<include>>`). Introdurla qui significa migliorare la fiducia nel sistema di accesso **prima** di toccare flussi più complessi (mentore, pagamento). Anticiparla è anche un investimento sul porting Spring Boot, dove diventerà Spring Security.
- La **gestione team con inviti** introduce un **invariante non banale**: _"un utente può appartenere a un solo team"_ + _"un invito accettato cancella tutti gli altri pendenti per quell'utente"_. Va validato presto perché ne dipende UC03 (già implementato, ma con team pre-caricati).
- **UC15** chiude un'estensione lasciata aperta in iter 1 (era un `// TODO` in `InviaSottomissioneControl`).

### Nuovi pattern probabili

- **Builder** (GoF Creazionale): costruzione di un Team con membri + inviti pendenti. L'invariante è complesso, una factory statica diventa illeggibile.
- **Chain of Responsibility** (GoF Comportamentale, opzionale): pipeline di validazioni in registrazione (email valida → password forte → email non già registrata → captcha).

### Nuove classi (anteprima)

```
model/Invito.java                      (entity nuova)
model/StatoInvito.java                 (enum: PENDENTE/ACCETTATO/RIFIUTATO/SCADUTO)
control/RegistrazioneControl.java
control/LoginControl.java
control/CreaTeamControl.java
control/InvitaMembroControl.java
control/RispondiInvitoControl.java
control/AggiornaSottomissioneControl.java
boundary/*.java                        (uno per ogni nuovo control)
repository/InvitoRepository.java
```

### Definition of Done iter 2

- I 4 UC della iter 1 funzionano **senza chiamare `caricaDatiDemo`**: l'utente registra, fa login, crea team, invita, viene accettato, si iscrive, sottomette, e il Giudice valuta.
- Test JUnit ≥ 40 totali (sui ~13 nuovi: registrazione happy-path + invariante "una sola appartenenza" + invariante "invito accettato cancella gli altri" + login con credenziali errate + ecc.).
- Nessun pattern di iter 1 è stato modificato (verifica di compatibilità).

---

## 4. Iterazione 3 — Workflow Mentore + primo sistema esterno

### Obiettivo

Integrare il **primo sistema esterno** (Calendar) e completare il workflow di supporto del Mentore — il pezzo di dominio che finora non avevamo nemmeno sfiorato.

### Casi d'uso da implementare (5)

| ID   | Nome                              | Attore primario | Sistema esterno |
| ---- | --------------------------------- | --------------- | --------------- |
| UC08 | Aggiungi Mentore (post-creazione) | Organizzatore   | —               |
| UC16 | Richiedi Supporto al Mentore      | Membro Team     | —               |
| UC17 | Visualizza Richieste di Supporto  | Mentore         | —               |
| UC18 | Proponi Call al Team              | Mentore         | **Calendar**    |
| UC19 | Segnala Team                      | Mentore         | —               |

### Perché in 3ª iterazione

- **Calendar è il primo sistema esterno** del progetto: va isolato dietro un'interfaccia per non contaminare la business logic. Affrontarlo qui (e non in iter 4 insieme al Pagamento) significa avere **due iterazioni** in cui usare l'**Adapter** prima del porting Spring Boot — quindi il pattern entra nei test e si stabilizza prima di diventare un client REST reale.
- Il workflow Mentore tocca **eventi non innescati da UC02–05** (richieste di supporto, segnalazioni di violazione del regolamento): valida che il pattern **Observer** regga anche per eventi "trasversali" e non solo per cambi di stato lifecycle.
- **UC08** verifica un assunto della 1ª iter ("i Mentori si possono aggiungere anche dopo la creazione dell'hackathon"): separa il caso d'uso di creazione iniziale (UC02) dalla manutenzione, evitando di rigonfiare UC02.

### Nuovi pattern

- **Adapter** (Strutturale): wrappa la libreria/SDK Calendar dietro un'interfaccia `CalendarService { prenotaSlot(...) ; cancellaSlot(...) }`. In iter 3 l'implementazione è uno stub (logga + ritorna ID slot finto); diventerà reale nel porting Spring Boot.
- **Command** (Comportamentale, opzionale): modellare la _richiesta di supporto pendente_ come Command consente al Mentore di rispondervi più tardi o di rifiutarla, mantenendo la storia.

### Nuove classi

```
model/RichiestaSupporto.java
model/Call.java
model/Segnalazione.java
infrastructure/calendar/CalendarService.java       (interface)
infrastructure/calendar/CalendarServiceStub.java   (implementazione iter 3)
control/AggiungiMentoreControl.java
control/RichiediSupportoControl.java
control/VisualizzaRichiesteControl.java
control/ProponiCallControl.java
control/SegnalaTeamControl.java
boundary/*.java
```

### Definition of Done iter 3

- Un team può chiedere supporto → il Mentore vede la richiesta → propone una call → viene prenotato uno slot su Calendar (stub) → il Team riceve la notifica via Observer/Factory Method già esistenti.
- Test ≥ 55 totali, almeno 3 sul confine `CalendarService` (slot prenotato OK, slot non disponibile, errore di rete simulato).
- L'interfaccia `CalendarService` è **pure domain** (niente import di SDK): pronto per il porting.

---

## 5. Iterazione 4 — Chiusura del ciclo + Pagamento + Tempo

### Obiettivo

Chiudere il ciclo di vita end-to-end (vincitore + premio) e introdurre il **terzo asse di complessità** del progetto: il **tempo** (transizioni automatiche di stato).

### Casi d'uso da implementare (5)

| ID   | Nome               | Tipo                   | Sistema esterno          |
| ---- | ------------------ | ---------------------- | ------------------------ |
| UC09 | Proclama Vincitore | UC normale             | —                        |
| UC10 | Eroga Premio       | UC normale             | **Sistema di Pagamento** |
| UC22 | Avvia Hackathon    | Transizione automatica | —                        |
| UC23 | Chiudi Iscrizioni  | Transizione automatica | —                        |
| UC24 | Termina Hackathon  | Transizione automatica | —                        |

### Perché in 4ª iterazione (e ultima Java puro)

- **Le transizioni automatiche (UC22–24) richiedono uno scheduler**: introdurre uno `ScheduledExecutorService` (o equivalente) è infrastruttura aggiuntiva che non vogliamo mescolare al business logic delle iterazioni precedenti.
- **Il pagamento (UC10) chiude il ciclo end-to-end**: senza di esso il sistema sa solo "decidere il vincitore", non "premiarlo". È il caso d'uso più "pesante" (transazione monetaria, ack del sistema esterno, gestione fallimenti, retry). Lasciarlo all'ultima iter consente di avere alle spalle l'esperienza dell'Adapter Calendar.
- **UC09** dipende da `tutteSottomissioniValutate()` + evento `VALUTAZIONI_COMPLETE` — entrambi **già pronti dalla 1ª iter**. Chiuderlo qui è naturale.

### Nuovi pattern

- **Adapter** per `PaymentGateway` (analogo a Calendar — la commissione apprezzerà la **coerenza**: stesso pattern per due integrazioni esterne).
- **Scheduler** (non un pattern GoF in senso stretto): un thread separato che a intervalli configurabili scansiona gli hackathon e invoca `avvia()` / `chiudiIscrizioni()` / `concludi()` sugli oggetti `StatoHackathon`. È il _test definitivo_ del pattern State: nessuna modifica al codice State, solo un nuovo trigger.

### Nuove classi

```
infrastructure/payment/PaymentGateway.java         (interface)
infrastructure/payment/PaymentGatewayStub.java     (impl. iter 4)
infrastructure/scheduler/StateTransitionScheduler.java
control/ProclamaVincitoreControl.java
control/ErogaPremioControl.java
boundary/*.java
```

### Definition of Done iter 4

- Un hackathon può andare da "creato" a "concluso con premio erogato" **senza intervento manuale**, salvo la valutazione del Giudice (UC05) e la proclamazione del Vincitore (UC09).
- Tutti i **24 UC della traccia** sono coperti (implementati o stub esplicitamente motivati).
- Test ≥ 70 totali. Smoke test sullo scheduler: in 5 secondi accelerati (`Clock` mockato), l'hackathon transita tutti gli stati.

---

## 6. Porting Spring Boot

### Quando

Dopo iter 4, come **iterazione separata di trasformazione tecnologica** — _non_ introduce nuova funzionalità. Manteniamo `03-codice/` intatto come riferimento; il porting va in un nuovo modulo (es. `04-springboot/`).

### Cosa cambia per package

| Package                   | Prima (Java puro) | Dopo (Spring Boot)                                               |
| ------------------------- | ----------------- | ---------------------------------------------------------------- |
| `boundary/`               | CLI con `Scanner` | `@RestController` + DTO + Bean Validation                        |
| `control/`                | classi semplici   | `@Service` (DI via costruttore)                                  |
| `model/`                  | POJO              | `@Entity` + `@Id` + `@OneToMany`/`@ManyToOne`/`@ManyToMany`      |
| `state/`                  | **invariato**     | **invariato** (dominio puro)                                     |
| `strategy/`               | **invariato**     | **invariato** _(eventualmente `@Component` per autowiring)_      |
| `observer/`               | **invariato**     | **invariato** _(o sostituibile con `ApplicationEventPublisher`)_ |
| `factory/`                | **invariato**     | **invariato** _(o sostituibile con `@Configuration`+`@Bean`)_    |
| `repository/`             | DAO in-memory     | `extends JpaRepository<T, ID>`                                   |
| `exception/`              | invariato         | + `@ControllerAdvice` per mappare a status HTTP                  |
| `infrastructure/calendar` | stub              | client reale (Google Calendar SDK o REST)                        |
| `infrastructure/payment`  | stub              | client reale di un PSP                                           |

### Cosa **non** cambia (e perché è importante per la commissione)

> Tutti e 4 i pattern di dominio (**State, Strategy, Observer, Factory Method**) sopravvivono al porting senza modifiche. È una **proprietà di design** non un caso: l'aver tenuto i pattern in `model/`, non in `control/`, li rende **framework-agnostic**.

Questo è il punto da evidenziare in commissione per giustificare la scelta dei 4 pattern e l'architettura a strati.

### Aggiunte tecniche

- `pom.xml` (Maven) con starter: `spring-boot-starter-web`, `-data-jpa`, `-security`, `-validation`, `-test`, `springdoc-openapi-starter-webmvc-ui`, driver DB.
- `application.yml` con profili `dev` (H2 in-memory) e `prod` (PostgreSQL).
- **Spring Security** per UC06 (form login + sessione, oppure JWT).
- **Bean Validation** sulle DTO (`@Valid`, `@NotNull`, `@Min`, `@Max`) — sostituisce parte delle validazioni manuali fatte in iter 1–4.
- **`@Transactional`** sui Service per operazioni multi-aggregato.
- **OpenAPI/Swagger** per documentare l'API.
- **`@SpringBootTest`** + **`MockMvc`** per test end-to-end HTTP (i test di dominio JUnit pre-esistenti restano validi senza modifiche).

### Strategia di migrazione (passo per passo)

1. Nuovo modulo Maven `04-springboot/`. `03-codice/` resta intatto come reference.
2. Copia _cartelle-by-cartelle_ dei pattern (`state/`, `strategy/`, `observer/`, `factory/`) — sono già pronti.
3. Migra le entity aggiungendo annotation JPA + costruttori no-arg richiesti da Hibernate.
4. Sostituisci i repository in-memory con interfacce `JpaRepository`.
5. Trasforma ogni `*Control` in `@Service`. Crea i corrispondenti `@RestController` _thin_ (solo serializzazione DTO ↔ dominio).
6. Wire Spring Security per le rotte autenticate (tutte tranne UC01, UC07).
7. Implementa gli `Adapter` reali per Calendar e PaymentGateway.
8. Migra i test JUnit a `@SpringBootTest` (le asserzioni di dominio rimangono identiche).

### Definition of Done porting

- API REST funzionante per i 24 UC, documentata via Swagger.
- Tutti i 70+ test esistenti continuano a passare, più ≥ 24 nuovi `MockMvc` (uno per UC).
- Build Maven verde (`mvn clean verify`).
- Deploy locale via `mvn spring-boot:run` o `docker compose up`.

---

## 7. Tabella riepilogativa (riferimento rapido)

| Iter     | Focus                         | Rischio principale risolto          | UC nuovi     | Pattern aggiunti                               | LOC stimati    |
| -------- | ----------------------------- | ----------------------------------- | ------------ | ---------------------------------------------- | -------------- |
| **1** ✅ | Ciclo di vita + 4 pattern     | State machine + interazione pattern | 4            | State, Strategy, Observer, Factory Method      | ~1500          |
| 2        | Self-service + auth           | Invariante team + autenticazione    | 8            | Builder _(opz. Chain of Responsibility)_       | ~1200          |
| 3        | Mentore + Calendar            | Integrazione sistema esterno (1°)   | 5            | Adapter _(opz. Command)_                       | ~900           |
| 4        | Vincitore + Pagamento + tempo | Sistema esterno (2°) + scheduler    | 5            | Adapter (per Payment)                          | ~800           |
| SB       | Porting Spring Boot           | Migrazione tecnologica              | 0 (refactor) | — _(Template Method opz. nei controller base)_ | ±0 LOC dominio |

**Totale a regime:** 22 UC implementati su 24 mappati. I 2 mancanti sono UC22/UC23/UC24 che sono _transizioni automatiche_; in iter 4 vengono coperti da un singolo Scheduler che invoca i metodi del pattern State già esistenti — non sono "UC" nel senso classico ma comportamenti del sistema.

---

## 8. Pattern aggiuntivi possibili (per arricchire la commissione)

Oltre ai **4 obbligatori** della 1ª iterazione, lungo il percorso ci sono **naturali opportunità** di introdurre altri pattern senza forzarli:

| Pattern             | Iterazione | Motivazione di dominio                                                       |
| ------------------- | ---------- | ---------------------------------------------------------------------------- |
| **Adapter**         | 3, 4       | Wrappare SDK/API esterne (Calendar, PaymentGateway).                         |
| **Builder**         | 2          | Costruzione di `Team` con membri + inviti pendenti — invariante complesso.   |
| **Command**         | 3          | Richiesta di supporto pendente che può essere accettata/rifiutata più tardi. |
| **Template Method** | SB (opz.)  | Controller REST base con hook overridabili nei sotto-controller.             |

Sono tutti pattern **giustificati dal dominio**, non scelti per fare numero: aggiungerli mostra padronanza del catalogo GoF al di là dei 4 di base.

---

## Riferimenti

- **Traccia originale**: `00-knowledge-base/00-traccia.md` _(symlink a `../hackhub_traccia.md`)_
- **Reference patterns**: `00-knowledge-base/02-design-patterns.md` _(symlink al catalogo completo di 24 pattern GoF in italiano)_
- **PDF del corso**: `docs/02_Processo.pdf`, `docs/UML_02_CasiUso.pdf`, `docs/UML_05_Interazioni.pdf`, `docs/PatternsGRASP.pdf`, `docs/SpringBoot.pdf`
- **Codice e test iter 1**: `03-codice/README.md`
