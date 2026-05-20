# UC02 — Crea Hackathon

| Campo                 | Valore                                                                                                                                                                            |
| --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                | UC02                                                                                                                                                                              |
| **Nome**              | Crea Hackathon                                                                                                                                                                    |
| **Breve descrizione** | L'Organizzatore crea un nuovo hackathon definendone le informazioni essenziali e assegnando lo staff (un Giudice e almeno un Mentore). L'hackathon nasce in stato `InIscrizione`. |
| **Attore primario**   | Organizzatore                                                                                                                                                                     |
| **Attori secondari**  | —                                                                                                                                                                                 |
| **Livello**           | Obiettivo utente                                                                                                                                                                  |
| **Frequenza d'uso**   | Bassa (qualche volta al mese per organizzatore)                                                                                                                                   |
| **Priorità**          | Alta — è il caso d'uso che innesca l'intero ciclo di vita.                                                                                                                        |

## Pre-condizioni

1. L'Organizzatore è autenticato nel sistema (`<<include>>` UC06).
2. Esistono almeno un Utente con ruolo Giudice e un Utente con ruolo Mentore registrati nel sistema.

## Post-condizioni

- **Garanzia di successo:** Un nuovo Hackathon è registrato in HackHub, in stato `InIscrizione`, con Organizzatore, Giudice e Mentori associati.
- **Garanzia minima:** Se la creazione fallisce, lo stato del sistema rimane invariato (nessun Hackathon parzialmente registrato).

## Sequenza degli eventi principale

1. L'Organizzatore richiede al sistema di creare un nuovo hackathon.
2. Il sistema mostra il modulo di creazione hackathon.
3. L'Organizzatore inserisce nome, regolamento, scadenza iscrizioni, data inizio, data fine, luogo, premio in denaro e dimensione massima del team.
4. L'Organizzatore seleziona un Giudice tra gli Utenti disponibili.
5. L'Organizzatore seleziona uno o più Mentori tra gli Utenti disponibili.
6. L'Organizzatore conferma la creazione.
7. Il sistema valida i dati inseriti.
8. Il sistema crea l'Hackathon, lo registra in HackHub e lo pone in stato `InIscrizione`.
9. Il sistema notifica gli Osservatori dell'evento `HACKATHON_CREATO`.
10. Il sistema mostra all'Organizzatore la conferma con i dati riepilogativi dell'hackathon creato.

## Sequenze degli eventi alternative

**7a. Dati incompleti o non validi** _(la data di scadenza iscrizioni è successiva alla data di inizio, o il premio è negativo, o non è stato selezionato alcun Giudice)_:

- 7a.1 Il sistema rileva la violazione e mostra all'Organizzatore il messaggio di errore con il campo non valido.
- 7a.2 La sequenza riprende dal passo 3.

**5a. Nessun Mentore disponibile**:

- 5a.1 Il sistema avvisa l'Organizzatore che è necessario almeno un Mentore.
- 5a.2 L'Organizzatore annulla la creazione, oppure registra prima un nuovo Mentore _(fuori scope di questo UC)_.
- 5a.3 Il caso d'uso termina senza creare l'Hackathon.

**7b. Nome hackathon già esistente in HackHub**:

- 7b.1 Il sistema avvisa l'Organizzatore della collisione.
- 7b.2 La sequenza riprende dal passo 3 con possibilità di modificare il nome.

## Requisiti speciali (extra-funzionali)

- L'operazione deve completare entro 2 secondi (esclusa rete).
- La data di scadenza iscrizioni deve essere strettamente anteriore alla data di inizio; la data di fine deve essere posteriore alla data di inizio.
- Il premio in denaro deve essere non negativo; la dimensione massima del team deve essere maggiore o uguale a 1.

## Casi d'uso correlati

- `<<include>>` **UC06 Autenticazione** (prerequisito).
- Correlato a **UC08 Aggiungi Mentore** (l'Organizzatore può aggiungere altri Mentori successivamente).
- Innesca, nel tempo, le transizioni di stato **UC22 Avvia Hackathon**, **UC23 Chiudi Iscrizioni**, **UC24 Termina Hackathon**.

## Note di progettazione

- L'Hackathon nasce con il pattern **State** inizializzato a `InIscrizione`.
- L'evento `HACKATHON_CREATO` è pubblicato tramite il pattern **Observer** dal Soggetto `Hackathon` (passo 9). I publisher di notifica usano **Factory Method**.
