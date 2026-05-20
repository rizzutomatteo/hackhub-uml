# UC03 — Iscrivi Team a Hackathon

| Campo                 | Valore                                                                                                                                                                                      |
| --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                | UC03                                                                                                                                                                                        |
| **Nome**              | Iscrivi Team a Hackathon                                                                                                                                                                    |
| **Breve descrizione** | Un Membro del Team iscrive il proprio team a un hackathon che si trova in stato `InIscrizione`, prima della scadenza delle iscrizioni e nel limite di dimensione massima del team previsto. |
| **Attore primario**   | Membro Team                                                                                                                                                                                 |
| **Attori secondari**  | —                                                                                                                                                                                           |
| **Livello**           | Obiettivo utente                                                                                                                                                                            |
| **Frequenza d'uso**   | Media (una volta per team per hackathon a cui si vuole partecipare)                                                                                                                         |
| **Priorità**          | Alta — è il punto di ingresso per i partecipanti.                                                                                                                                           |

## Pre-condizioni

1. Il Membro del Team è autenticato (`<<include>>` UC06).
2. Il Membro appartiene a un Team che non è già iscritto all'hackathon scelto.
3. Esiste almeno un Hackathon in stato `InIscrizione`.

## Post-condizioni

- **Garanzia di successo:** Il Team è registrato come partecipante dell'Hackathon scelto.
- **Garanzia minima:** Nessuno stato parziale: se l'iscrizione fallisce, il Team non risulta tra i partecipanti.

## Sequenza degli eventi principale

1. Il Membro del Team richiede di visualizzare gli hackathon disponibili.
2. Il sistema mostra l'elenco degli hackathon in stato `InIscrizione`.
3. Il Membro del Team seleziona l'hackathon a cui vuole iscrivere il proprio team.
4. Il sistema mostra i dettagli dell'hackathon e i requisiti di iscrizione (scadenza, dimensione massima del team).
5. Il Membro del Team conferma l'iscrizione.
6. Il sistema verifica che l'hackathon accetti ancora iscrizioni (stato `InIscrizione` e scadenza non superata).
7. Il sistema verifica che la dimensione del Team rispetti il limite massimo dell'hackathon.
8. Il sistema verifica che il Team non sia già iscritto a quell'hackathon.
9. Il sistema registra l'iscrizione del Team all'Hackathon.
10. Il sistema notifica gli Osservatori dell'evento `TEAM_ISCRITTO`.
11. Il sistema mostra al Membro del Team la conferma dell'avvenuta iscrizione.

## Sequenze degli eventi alternative

**6a. Hackathon non più in iscrizione** _(stato `InCorso`, `InValutazione` o `Conclusa`, oppure scadenza iscrizioni superata)_:

- 6a.1 Il sistema mostra l'errore "Iscrizioni chiuse per questo hackathon".
- 6a.2 Il caso d'uso termina senza iscrizione.

**7a. Team troppo grande**:

- 7a.1 Il sistema mostra l'errore con il limite massimo previsto e il numero attuale di membri del team.
- 7a.2 Il caso d'uso termina; il Membro del Team può successivamente ridurre il team e ripetere.

**8a. Team già iscritto**:

- 8a.1 Il sistema mostra l'errore "Team già iscritto a questo hackathon".
- 8a.2 Il caso d'uso termina senza modifiche.

## Requisiti speciali

- La verifica delle precondizioni (passi 6-8) deve essere atomica per evitare race condition tra iscrizioni concorrenti.
- L'utente deve ricevere un feedback immediato (≤ 1s) nel caso di errore di precondizione.

## Casi d'uso correlati

- `<<include>>` **UC06 Autenticazione**.
- Presuppone **UC11 Crea Team** (esistenza del team).
- Abilita **UC04 Invia Sottomissione** (una volta che l'hackathon transita in `InCorso`).

## Note di progettazione

- La verifica del passo 6 delega al pattern **State** dell'Hackathon: `hackathon.puoiIscrivereTeam()` ritorna `true` solo se lo stato corrente è `InIscrizione`.
- L'evento `TEAM_ISCRITTO` viene pubblicato attraverso il pattern **Observer** allo `Staff` dell'hackathon (Organizzatore + Mentori) per consentire un monitoraggio in tempo reale dei partecipanti.
