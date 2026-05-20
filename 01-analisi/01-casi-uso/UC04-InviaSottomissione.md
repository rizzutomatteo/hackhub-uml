# UC04 — Invia Sottomissione

| Campo                 | Valore                                                                                                                                                                      |
| --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                | UC04                                                                                                                                                                        |
| **Nome**              | Invia Sottomissione                                                                                                                                                         |
| **Breve descrizione** | Un Membro del Team invia, per conto del proprio team, la sottomissione del lavoro all'hackathon a cui è iscritto, entro la scadenza prevista (data di fine dell'hackathon). |
| **Attore primario**   | Membro Team                                                                                                                                                                 |
| **Attori secondari**  | —                                                                                                                                                                           |
| **Livello**           | Obiettivo utente                                                                                                                                                            |
| **Frequenza d'uso**   | Bassa (una volta per team per hackathon)                                                                                                                                    |
| **Priorità**          | Alta — senza sottomissione il team non può essere valutato.                                                                                                                 |

## Pre-condizioni

1. Il Membro del Team è autenticato (`<<include>>` UC06).
2. Il Team del Membro è iscritto a un Hackathon (vedi UC03).
3. L'Hackathon è in stato `InCorso`.
4. La data corrente non ha superato la data di fine dell'hackathon.

## Post-condizioni

- **Garanzia di successo:** La Sottomissione è registrata nell'Hackathon, associata al Team, con timestamp di invio. Se ne esisteva già una per il Team, è stata sostituita (vedi UC15 Aggiorna Sottomissione).
- **Garanzia minima:** Lo stato del sistema è invariato se l'invio fallisce per pre-condizione non soddisfatta.

## Sequenza degli eventi principale

1. Il Membro del Team richiede di inviare la sottomissione per il proprio team a uno specifico hackathon.
2. Il sistema mostra il modulo di sottomissione con i dati del team e dell'hackathon.
3. Il Membro del Team inserisce titolo, descrizione e link al lavoro (repository, documento, video, ecc.).
4. Il Membro del Team conferma l'invio.
5. Il sistema verifica che l'Hackathon sia in stato `InCorso` e che la data corrente non abbia superato la scadenza.
6. Il sistema verifica che il Team sia iscritto all'Hackathon.
7. Il sistema crea la Sottomissione associandola al Team e all'Hackathon, con timestamp corrente.
8. Il sistema notifica gli Osservatori dell'evento `SOTTOMISSIONE_RICEVUTA` (in particolare il Giudice e l'Organizzatore).
9. Il sistema mostra al Membro del Team la conferma dell'avvenuto invio.

## Sequenze degli eventi alternative

**5a. Hackathon non in corso o scadenza superata**:

- 5a.1 Il sistema mostra l'errore "Sottomissioni non più accettate per questo hackathon".
- 5a.2 Il caso d'uso termina senza creare la sottomissione.

**6a. Team non iscritto all'hackathon**:

- 6a.1 Il sistema mostra l'errore "Il tuo team non risulta iscritto a questo hackathon".
- 6a.2 Il caso d'uso termina.

**7a. Sottomissione già presente per il team** _(estensione UC15 Aggiorna Sottomissione)_:

- 7a.1 Il sistema mostra all'utente la sottomissione esistente e chiede conferma di sostituzione.
- 7a.2 Se l'utente conferma, la vecchia sottomissione viene sostituita; altrimenti il caso d'uso termina senza modifiche.

## Requisiti speciali

- L'URL del lavoro deve essere validato sintatticamente (schema `http` o `https`).
- Titolo non vuoto (min 3 caratteri), descrizione max 2000 caratteri.
- Il timestamp di invio è registrato lato server, non lato client (per impedire manipolazioni).

## Casi d'uso correlati

- `<<include>>` **UC06 Autenticazione**.
- Presuppone **UC03 Iscrivi Team a Hackathon**.
- È esteso da **UC15 Aggiorna Sottomissione** (entro la scadenza).
- Abilita **UC05 Valuta Sottomissione** (una volta che l'hackathon transita in `InValutazione`).

## Note di progettazione

- La verifica del passo 5 delega al pattern **State**: `hackathon.puoiSottomettere()` ritorna `true` solo se lo stato corrente è `InCorso`.
- L'evento `SOTTOMISSIONE_RICEVUTA` è veicolato dal pattern **Observer** ai Giudici e all'Organizzatore. Il `NotificaPublisher` ne crea la rappresentazione concreta tramite **Factory Method**.
