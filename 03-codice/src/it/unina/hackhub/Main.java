package it.unina.hackhub;

import it.unina.hackhub.boundary.CreaHackathonBoundary;
import it.unina.hackhub.boundary.InviaSottomissioneBoundary;
import it.unina.hackhub.boundary.IscriviTeamBoundary;
import it.unina.hackhub.boundary.ValutaSottomissioneBoundary;
import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.control.InviaSottomissioneControl;
import it.unina.hackhub.control.IscriviTeamControl;
import it.unina.hackhub.control.ValutaSottomissioneControl;
import it.unina.hackhub.factory.NotificaConsole;
import it.unina.hackhub.model.*;
import it.unina.hackhub.observer.LoggerEventi;
import it.unina.hackhub.repository.HackathonRepository;
import it.unina.hackhub.repository.TeamRepository;
import it.unina.hackhub.repository.UtenteRepository;

import java.util.List;
import java.util.Scanner;

/**
 * CLI testuale per la 1ª iterazione di HackHub.
 * <p>Esercita end-to-end i 4 casi d'uso implementati
 * (UC02 → UC03 → UC04 → UC05) tramite menu a numeri,
 * più operazioni di "tick manuale" per le transizioni di stato
 * UC22/UC23/UC24 e operazioni di setup per la demo.
 */
public class Main {

    private final Scanner in = new Scanner(System.in);

    private final HackHub hackHub;
    private final CreaHackathonBoundary creaB;
    private final IscriviTeamBoundary iscriviB;
    private final InviaSottomissioneBoundary inviaB;
    private final ValutaSottomissioneBoundary valutaB;

    // Osservatori "globali" registrati su ogni hackathon creato in questa sessione
    private final LoggerEventi loggerEventi = new LoggerEventi();
    private final NotificaConsole notificaConsole = new NotificaConsole();

    public Main() {
        UtenteRepository utenteRepo = new UtenteRepository();
        TeamRepository teamRepo = new TeamRepository();
        HackathonRepository hackRepo = new HackathonRepository();
        this.hackHub = new HackHub("HackHub", hackRepo, teamRepo, utenteRepo);

        // Boundaries con i rispettivi Controls
        this.creaB = new CreaHackathonBoundary(in,
                new CreaHackathonControl(hackHub), hackHub) {
            @Override
            public void esegui() {
                super.esegui();
                registraOsservatoriSuTuttiGliHackathon();
            }
        };
        this.iscriviB = new IscriviTeamBoundary(in, new IscriviTeamControl(hackHub), hackHub);
        this.inviaB   = new InviaSottomissioneBoundary(in, new InviaSottomissioneControl(hackHub), hackHub);
        this.valutaB  = new ValutaSottomissioneBoundary(in, new ValutaSottomissioneControl(hackHub), hackHub);
    }

    public static void main(String[] args) {
        new Main().menuPrincipale();
    }

    private void menuPrincipale() {
        System.out.println("===============================================");
        System.out.println("        HACKHUB CLI — 1a iterazione");
        System.out.println("===============================================");

        while (true) {
            System.out.println();
            System.out.println("--- MENU PRINCIPALE ---");
            System.out.println("[1] Carica dati demo (utenti staff + team) ");
            System.out.println("[2] UC02 Crea Hackathon");
            System.out.println("[3] UC03 Iscrivi Team a Hackathon");
            System.out.println("[4] UC04 Invia Sottomissione");
            System.out.println("[5] UC05 Valuta Sottomissione");
            System.out.println("[6] Avanza stato hackathon (UC22/23/24)");
            System.out.println("[7] Mostra stato sistema");
            System.out.println("[0] Esci");
            System.out.print("> ");
            String scelta = in.nextLine().trim();

            switch (scelta) {
                case "1" -> caricaDatiDemo();
                case "2" -> creaB.esegui();
                case "3" -> iscriviB.esegui();
                case "4" -> inviaB.esegui();
                case "5" -> valutaB.esegui();
                case "6" -> avanzaStatoHackathon();
                case "7" -> mostraStato();
                case "0" -> { System.out.println("Arrivederci."); return; }
                default -> System.out.println("[!] Scelta non valida.");
            }
        }
    }

    // ==============================  DEMO DATA  ==========================================

    private void caricaDatiDemo() {
        if (!hackHub.tuttiUtenti().isEmpty()) {
            System.out.println("[!] Dati demo già caricati. Stato del sistema:");
            mostraStato();
            return;
        }

        Organizzatore org = new Organizzatore("Anna",   "Rossi",  "anna@hackhub.it",  "pwd1");
        Giudice       giu = new Giudice      ("Marco",  "Verdi",  "marco@hackhub.it", "pwd2");
        Mentore       men = new Mentore      ("Lucia",  "Bianchi","lucia@hackhub.it", "pwd3");

        hackHub.registraUtente(org);
        hackHub.registraUtente(giu);
        hackHub.registraUtente(men);

        // Team partecipanti
        Utente paolo = new Utente("Paolo", "Esposito", "paolo@mail.it", "pwd4");
        Utente sara  = new Utente("Sara",  "Romano",   "sara@mail.it",  "pwd5");
        Utente luca  = new Utente("Luca",  "Greco",    "luca@mail.it",  "pwd6");
        hackHub.registraUtente(paolo);
        hackHub.registraUtente(sara);
        hackHub.registraUtente(luca);

        Team alpha = new Team("Team Alpha", paolo);
        alpha.aggiungiMembro(sara);
        alpha.aggiungiMembro(luca);
        hackHub.registraTeam(alpha);

        // Secondo team (per esercitare l'aspetto "molti team iscritti")
        Utente giulia = new Utente("Giulia", "Conti",  "giulia@mail.it",  "pwd7");
        Utente diego  = new Utente("Diego",  "Marino", "diego@mail.it",   "pwd8");
        hackHub.registraUtente(giulia);
        hackHub.registraUtente(diego);
        Team beta = new Team("Team Beta", giulia);
        beta.aggiungiMembro(diego);
        hackHub.registraTeam(beta);

        System.out.println("✓ Caricati 8 utenti (1 Org, 1 Giu, 1 Men, 5 utenti normali) e 2 team.");
        mostraStato();
    }

    // ==============================  TICK MANUALE STATO  ================================

    private void avanzaStatoHackathon() {
        List<Hackathon> tutti = hackHub.tuttiHackathon();
        if (tutti.isEmpty()) {
            System.out.println("[!] Nessun hackathon registrato.");
            return;
        }
        System.out.println("Hackathon:");
        for (int i = 0; i < tutti.size(); i++) {
            Hackathon h = tutti.get(i);
            System.out.printf("  [%d] %s [stato=%s]%n", i + 1, h.getNome(), h.getStato().nome());
        }
        System.out.print("Scegli hackathon: ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idx < 0 || idx >= tutti.size()) return;
            Hackathon h = tutti.get(idx);

            System.out.println("Transizione corrente: " + h.getStato().nome());
            System.out.println("  [a] avvia (InIscrizione → InCorso)");
            System.out.println("  [v] apriValutazione (InCorso → InValutazione)");
            System.out.println("  [c] concludi (InValutazione → Conclusa)");
            System.out.print("> ");
            String azione = in.nextLine().trim().toLowerCase();
            switch (azione) {
                case "a" -> h.avvia();
                case "v" -> h.apriValutazione();
                case "c" -> h.concludi();
                default  -> System.out.println("[!] Azione sconosciuta.");
            }
            System.out.println("✓ Nuovo stato: " + h.getStato().nome());
        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    // ==============================  STATO SISTEMA  =====================================

    private void mostraStato() {
        System.out.println();
        System.out.println("---------------- STATO HACKHUB ----------------");
        System.out.println("Utenti registrati:");
        for (Utente u : hackHub.tuttiUtenti()) {
            String r = (u instanceof MembroStaff ms) ? ms.ruolo() : "Utente";
            System.out.printf("  - [%s] %s%n", r, u);
        }
        System.out.println("Team:");
        for (Team t : hackHub.tuttiTeam()) {
            System.out.printf("  - %s (membri: %d)%n", t.getNome(), t.numeroMembri());
        }
        System.out.println("Hackathon:");
        for (Hackathon h : hackHub.tuttiHackathon()) {
            System.out.printf("  - %s [%s], iscritti=%d, sottomissioni=%d%n",
                    h.getNome(), h.getStato().nome(),
                    h.getTeamIscritti().size(), h.getSottomissioni().size());
        }
        System.out.println("-----------------------------------------------");
    }

    private void registraOsservatoriSuTuttiGliHackathon() {
        // Wiring degli osservatori: 1 LoggerEventi + 1 NotificaConsole su tutti.
        for (Hackathon h : hackHub.tuttiHackathon()) {
            h.aggiungiOsservatore(loggerEventi);
            h.aggiungiOsservatore(notificaConsole);
        }
    }
}
