# Schede Brief — UC non implementati nella 1ª iterazione

> Per **in quale iterazione** ciascuno di questi UC sarà implementato (e perché) vedi [`../../ITERAZIONI.md`](../../ITERAZIONI.md): UC01/06/07/11-15/20/21 → iter 2; UC08/16-19 → iter 3; UC09/10/22-24 → iter 4.

Per ognuno dei 20 UC qui sotto è fornita la sola descrizione brief secondo il formato del corso (UML_02 pag. 9): "riepilogo conciso di un solo paragrafo, relativo al solo scenario principale di successo". L'analisi dettagliata, le precondizioni e le sequenze alternative saranno sviluppate in iterazioni successive.

---

## UC01 — Registrazione

**Attore primario:** Visitatore

Il Visitatore accede al sistema e fornisce le proprie credenziali (email, password, nome, cognome). Il sistema valida i dati, registra un nuovo Utente e lo informa dell'avvenuta registrazione. Da questo momento il Visitatore può autenticarsi come Utente.

---

## UC06 — Autenticazione

**Attore primario:** Utente / Membro Staff

L'Utente fornisce email e password. Il sistema le verifica contro la base dati e, se valide, crea una sessione associando il ruolo dell'utente (Membro Team, Organizzatore, Giudice o Mentore). In caso di credenziali errate il sistema mostra un messaggio di errore. _Caso d'uso incluso da quasi tutti gli altri._

---

## UC07 — Consulta Hackathon Pubblici

**Attore primario:** Visitatore

Il Visitatore richiede l'elenco degli hackathon pubblici. Il sistema mostra nome, regolamento, scadenza iscrizioni, date di inizio/fine e luogo di tutti gli hackathon non ancora conclusi. Il Visitatore non può iscrivere team né accedere a sottomissioni.

---

## UC08 — Aggiungi Mentore

**Attore primario:** Organizzatore

L'Organizzatore, dopo la creazione dell'hackathon, decide di aggiungere uno o più Mentori. Seleziona gli Utenti con ruolo Mentore disponibili nel sistema e li associa all'hackathon. Il sistema notifica i Mentori dell'avvenuta assegnazione.

---

## UC09 — Proclama Vincitore

**Attore primario:** Organizzatore

Quando tutte le sottomissioni dell'hackathon sono state valutate dal Giudice, l'Organizzatore consulta la classifica dei team in base al punteggio aggregato e proclama un singolo team vincitore. Il sistema registra il vincitore, transita l'hackathon in stato `Conclusa` e include UC10 Eroga Premio.

---

## UC10 — Eroga Premio

**Attore primario:** Organizzatore

L'Organizzatore richiede l'erogazione del premio in denaro al team vincitore. Il sistema delega la transazione al **Sistema di Pagamento** (attore esterno) fornendo i dati del team, l'importo e i riferimenti bancari. Il sistema registra l'esito (riuscito/fallito) e notifica gli Osservatori.

---

## UC11 — Crea Team

**Attore primario:** Utente

L'Utente fornisce un nome per il team. Il sistema verifica che l'utente non appartenga già a un altro team e crea un nuovo Team con l'utente come unico membro iniziale. Include UC12 Invita Membri al Team per popolarlo.

---

## UC12 — Invita Membri al Team

**Attore primario:** Membro Team (creatore)

Il Membro del Team che ha creato il team seleziona altri Utenti registrati e invia loro un invito. Il sistema registra gli inviti pendenti e notifica gli Utenti destinatari. Un Utente può ricevere più inviti ma può accettarne uno solo.

---

## UC13 — Accetta Invito Team

**Attore primario:** Utente

L'Utente visualizza i propri inviti pendenti e ne accetta uno. Il sistema verifica che l'Utente non appartenga ad altri team, lo aggiunge come membro del team scelto, cancella tutti gli altri inviti pendenti per lo stesso Utente e notifica i membri del team.

---

## UC14 — Rifiuta Invito Team

**Attore primario:** Utente

L'Utente visualizza un invito pendente e lo rifiuta. Il sistema rimuove l'invito dalla lista pendenti e notifica il creatore del team del rifiuto.

---

## UC15 — Aggiorna Sottomissione _(estensione di UC04)_

**Attore primario:** Membro Team

Finché la scadenza dell'hackathon non è stata superata e l'hackathon è ancora in stato `InCorso`, un Membro del Team che ha già inviato una sottomissione può richiederne la modifica. Il sistema permette di sostituire titolo, descrizione e link; il timestamp di invio viene aggiornato.

---

## UC16 — Richiedi Supporto al Mentore

**Attore primario:** Membro Team

Durante un hackathon in stato `InCorso`, un Membro del Team formula una richiesta di supporto (testo libero) ai Mentori dell'hackathon. Il sistema registra la richiesta e la notifica a tutti i Mentori assegnati.

---

## UC17 — Visualizza Richieste di Supporto

**Attore primario:** Mentore

Il Mentore consulta la lista delle richieste di supporto pendenti per gli hackathon a cui è assegnato. Il sistema mostra mittente (team), timestamp, testo della richiesta e stato (aperta/risposta/chiusa).

---

## UC18 — Proponi Call al Team

**Attore primario:** Mentore

Il Mentore propone a un Team una call per discutere una richiesta di supporto. Il sistema delega la prenotazione dello slot al **Sistema Calendar** (attore esterno) fornendo data, orario e partecipanti. Il sistema registra il riferimento alla call confermata e notifica il Team.

---

## UC19 — Segnala Team

**Attore primario:** Mentore

Il Mentore, notata una violazione del regolamento da parte di un Team, compila una segnalazione (motivazione testuale) e la invia all'Organizzatore. Il sistema registra la segnalazione e la notifica all'Organizzatore per le decisioni del caso.

---

## UC20 — Visualizza Sottomissioni

**Attore primario:** Membro Staff (Organizzatore, Giudice, Mentore)

Il Membro Staff consulta le sottomissioni dei team relativamente agli hackathon di cui è staff. Il sistema mostra titolo, team, timestamp e link delle sottomissioni. Non è abilitato ad accedere alle sottomissioni di hackathon di cui non fa parte come staff.

---

## UC21 — Visualizza Tutti gli Hackathon

**Attore primario:** Membro Staff

Il Membro Staff consulta l'elenco completo di tutti gli hackathon registrati nel sistema, indipendentemente dal fatto che ne sia o meno staff. Il sistema mostra nome, stato corrente, date, luogo e premio di tutti gli hackathon.

---

## UC22 — Avvia Hackathon _(transizione interna)_

**Attore primario:** Sistema _(innescato dalla scadenza delle iscrizioni)_

Quando la data corrente raggiunge la data di inizio dell'hackathon e l'hackathon è in stato `InIscrizione`, il sistema transita lo stato in `InCorso`. L'evento è notificato agli Osservatori (team iscritti + staff).

---

## UC23 — Chiudi Iscrizioni _(transizione interna)_

**Attore primario:** Sistema _(innescato dalla scadenza iscrizioni)_

Quando la data corrente supera la scadenza iscrizioni, il sistema impedisce ulteriori iscrizioni di team (`puoiIscrivereTeam()` ritorna `false`). Lo stato dell'hackathon può rimanere `InIscrizione` finché la data di inizio non è stata raggiunta — è la pre-condizione di UC22.

---

## UC24 — Termina Hackathon _(transizione interna)_

**Attore primario:** Sistema _(innescato dalla data di fine)_

Quando la data corrente raggiunge la data di fine dell'hackathon e l'hackathon è in stato `InCorso`, il sistema transita lo stato in `InValutazione`. Da questo momento i team non possono più inviare sottomissioni; il Giudice può iniziare a valutare.

---

> **Nota**: UC22, UC23 e UC24 modellano transizioni di stato innescate da eventi temporali, non da attori umani. Saranno realizzati nel codice come metodi del pattern **State** (`avvia()`, `apriValutazione()`, `concludi()`) e per la prima iterazione saranno invocabili dalla CLI come "tick manuale" del tempo, per consentire di esercitare l'intero ciclo di vita senza dover aspettare scadenze reali.
