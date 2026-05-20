# UC05 — Valuta Sottomissione

| Campo                 | Valore                                                                                                                                                                                                                                                      |
| --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                | UC05                                                                                                                                                                                                                                                        |
| **Nome**              | Valuta Sottomissione                                                                                                                                                                                                                                        |
| **Breve descrizione** | Il Giudice assegnato a un hackathon, una volta che questo è in stato `InValutazione`, esamina ogni sottomissione dei team partecipanti e rilascia per ciascuna una valutazione composta da un giudizio scritto e un punteggio numerico compreso tra 0 e 10. |
| **Attore primario**   | Giudice                                                                                                                                                                                                                                                     |
| **Attori secondari**  | —                                                                                                                                                                                                                                                           |
| **Livello**           | Obiettivo utente                                                                                                                                                                                                                                            |
| **Frequenza d'uso**   | Bassa (una volta per sottomissione per giudice)                                                                                                                                                                                                             |
| **Priorità**          | Alta — abilita la proclamazione del vincitore (UC09).                                                                                                                                                                                                       |

## Pre-condizioni

1. Il Giudice è autenticato (`<<include>>` UC06).
2. Il Giudice è assegnato all'Hackathon su cui vuole valutare.
3. L'Hackathon è in stato `InValutazione`.
4. Esiste almeno una Sottomissione non ancora valutata per quell'hackathon.

## Post-condizioni

- **Garanzia di successo:** La Sottomissione selezionata risulta valutata: ad essa è associata una Valutazione con punteggio numerico e giudizio scritto, firmata dal Giudice corrente, con timestamp.
- **Garanzia minima:** Se la valutazione fallisce, la Sottomissione resta in stato "non valutata".

## Sequenza degli eventi principale

1. Il Giudice richiede di visualizzare le sottomissioni dell'hackathon che gli è stato assegnato.
2. Il sistema mostra la lista delle sottomissioni con indicazione di quali sono già state valutate.
3. Il Giudice seleziona una sottomissione non ancora valutata.
4. Il sistema mostra i dettagli della sottomissione (titolo, descrizione, link, team).
5. Il Giudice inserisce il punteggio (intero tra 0 e 10) e il giudizio scritto.
6. Il Giudice conferma la valutazione.
7. Il sistema verifica che l'Hackathon sia in stato `InValutazione`.
8. Il sistema verifica che il punteggio sia compreso tra 0 e 10 (estremi inclusi).
9. Il sistema crea la Valutazione, la associa alla Sottomissione e la firma con il Giudice corrente.
10. Il sistema aggiorna il punteggio aggregato della Sottomissione applicando la strategia di calcolo corrente dell'Hackathon (default: media semplice).
11. Se tutte le sottomissioni dell'hackathon risultano valutate, il sistema notifica gli Osservatori dell'evento `VALUTAZIONI_COMPLETE`.
12. Il sistema mostra al Giudice la conferma della valutazione registrata.

## Sequenze degli eventi alternative

**7a. Hackathon non in stato di valutazione**:

- 7a.1 Il sistema mostra l'errore "Non è il momento di valutare questo hackathon".
- 7a.2 Il caso d'uso termina senza modifiche.

**8a. Punteggio non valido** _(fuori range o non numerico)_:

- 8a.1 Il sistema mostra l'errore "Il punteggio deve essere un intero tra 0 e 10".
- 8a.2 La sequenza riprende dal passo 5.

**3a. Sottomissione già valutata dal Giudice**:

- 3a.1 Il sistema avvisa che è già presente una valutazione di questo Giudice per la sottomissione.
- 3a.2 Il sistema offre la possibilità di sovrascriverla.
- 3a.3 Se l'utente conferma, la sequenza prosegue dal passo 5; altrimenti il caso d'uso termina.

## Requisiti speciali

- Il giudizio scritto è obbligatorio e non vuoto (min 10 caratteri).
- Il punteggio è un `int` (non `double`) per evitare ambiguità in fase di confronto.
- L'invariante "punteggio ∈ [0,10]" è espresso nella classe `Valutazione` e verificato sia lato Boundary (form) sia lato Control (defense in depth).

## Casi d'uso correlati

- `<<include>>` **UC06 Autenticazione**.
- Presuppone **UC04 Invia Sottomissione**.
- Abilita **UC09 Proclama Vincitore** (quando tutte le sottomissioni sono valutate).

## Note di progettazione

- La verifica del passo 7 delega al pattern **State**: `hackathon.puoiValutare()` ritorna `true` solo se lo stato corrente è `InValutazione`.
- L'aggregazione del punteggio (passo 10) è realizzata con il pattern **Strategy** — l'interfaccia `StrategiaPunteggio` consente di sostituire `MediaSemplice` con `MediaPesata` (o altre formule) senza modificare il `Giudice` né la `Sottomissione`.
- L'evento `VALUTAZIONI_COMPLETE` è veicolato dal pattern **Observer** all'Organizzatore (lo abilita a procedere con UC09 Proclama Vincitore).
