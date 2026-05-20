# HackHub — Codice (1ª iterazione)

> Per la **roadmap completa del progetto** (cosa è stato fatto, perché, e cosa resta per iter 2/3/4 + porting Spring Boot) vedi [`../ITERAZIONI.md`](../ITERAZIONI.md).

Implementazione **Java puro** (Java 17+, niente Maven) dei 4 casi d'uso della prima iterazione:

- **UC02** Crea Hackathon
- **UC03** Iscrivi Team a Hackathon
- **UC04** Invia Sottomissione
- **UC05** Valuta Sottomissione

Pattern adottati (≥ 2 diversi da Singleton, come da traccia):

| Pattern            | Pacchetto  | Ruolo nel sistema                                                           |
| ------------------ | ---------- | --------------------------------------------------------------------------- |
| **State**          | `state`    | Ciclo di vita Hackathon (InIscrizione → InCorso → InValutazione → Conclusa) |
| **Strategy**       | `strategy` | Calcolo punteggio aggregato (MediaSemplice / MediaPesata)                   |
| **Observer**       | `observer` | Propagazione eventi (`HACKATHON_CREATO`, `TEAM_ISCRITTO`, …)                |
| **Factory Method** | `factory`  | Creazione polimorfa di Notifiche (Console / Email)                          |

## Struttura

```
src/it/unina/hackhub/
├── Main.java                 ← CLI testuale (entry point)
├── boundary/                 ← 4 *Boundary CLI
├── control/                  ← 4 *Control (use-case handler)
├── model/                    ← entity di dominio
├── state/                    ← Pattern State
├── strategy/                 ← Pattern Strategy
├── observer/                 ← Pattern Observer
├── factory/                  ← Pattern Factory Method
├── repository/               ← DAO in-memory
└── exception/                ← eccezioni di dominio

test/it/unina/hackhub/        ← 6 classi JUnit (27 test)
lib/
└── junit-platform-console-standalone.jar
```

## Prerequisiti

- Un JDK ≥ 17. Se non disponibile sul sistema:
  ```bash
  curl -fsSL -o /tmp/jdk21.tar.gz \
    "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz"
  tar xf /tmp/jdk21.tar.gz -C ~/
  mv ~/jdk-21* ~/jdk21
  export PATH=~/jdk21/bin:$PATH
  ```

## Compilazione

```bash
cd 03-codice
mkdir -p out
javac -d out -encoding UTF-8 -cp out $(find src -name "*.java")
javac -d out -encoding UTF-8 -cp out:lib/junit-platform-console-standalone.jar \
    $(find test -name "*.java")
```

## Esecuzione CLI

```bash
java -cp out it.unina.hackhub.Main
```

Menu:

```
[1] Carica dati demo (utenti staff + team)
[2] UC02 Crea Hackathon
[3] UC03 Iscrivi Team a Hackathon
[4] UC04 Invia Sottomissione
[5] UC05 Valuta Sottomissione
[6] Avanza stato hackathon (UC22/23/24)
[7] Mostra stato sistema
[0] Esci
```

## Scenario end-to-end consigliato

1. `[1]` per caricare 8 utenti + 2 team demo.
2. `[2]` per creare un hackathon (es. "Demo", staff scelto dai menu).
3. `[3]` per iscrivere "Team Alpha" all'hackathon (login con Paolo).
4. `[6]` per avanzare lo stato (`InIscrizione → InCorso`, opzione `a`).
5. `[4]` per inviare una sottomissione (login con Paolo).
6. `[6]` per avanzare (`InCorso → InValutazione`, opzione `v`).
7. `[5]` per valutare la sottomissione (login con Marco, punteggio 0–10).
8. `[7]` per ispezionare lo stato.

Durante ogni passaggio osserverai i log degli **Osservatori** e gli output delle **Notifiche** prodotte dal **Factory Method**.

## Esecuzione test JUnit 5

```bash
java -jar lib/junit-platform-console-standalone.jar execute \
  --class-path out --scan-classpath --details=tree
```

Atteso: **27 test, tutti passanti**.

## Cosa NON è implementato in questa iterazione

- Autenticazione reale (`UC06`): la CLI "logga" scegliendo un Utente dal menu.
- UC11 Crea Team / UC12-UC14 inviti: i team della demo sono pre-caricati.
- UC09 Proclama Vincitore / UC10 Eroga Premio: presenti come API su `Hackathon.proclamaVincitore(...)` ma non collegati alla CLI.
- Integrazione con Calendar e Sistema di Pagamento: previste come stub via `NotificaPublisher`.
- UC15 Aggiorna Sottomissione: implementato come "sostituzione automatica" durante un nuovo invio (vedi `InviaSottomissioneControl`).

Saranno coperti nelle iterazioni successive (e nel porting a Spring Boot).
