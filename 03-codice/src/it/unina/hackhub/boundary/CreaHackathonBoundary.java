package it.unina.hackhub.boundary;

import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * UC02 — Crea Hackathon (CLI Boundary).
 * <p>Responsabilità BCE: presentare il form all'attore, raccogliere input,
 * invocare il Control e formattare la risposta. <b>Non</b> contiene logica
 * di business né accesso diretto agli Entity.
 */
public class CreaHackathonBoundary {

    private final Scanner in;
    private final CreaHackathonControl control;
    private final HackHub hackHub;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CreaHackathonBoundary(Scanner in, CreaHackathonControl control, HackHub hackHub) {
        this.in = in;
        this.control = control;
        this.hackHub = hackHub;
    }

    public void esegui() {
        System.out.println();
        System.out.println("==================== UC02 - Crea Hackathon ====================");

        try {
            System.out.print("Nome hackathon: ");
            String nome = in.nextLine().trim();

            System.out.print("Regolamento (breve): ");
            String regolamento = in.nextLine().trim();

            System.out.print("Scadenza iscrizioni (YYYY-MM-DD): ");
            LocalDate scadenza = LocalDate.parse(in.nextLine().trim(), FMT);

            System.out.print("Data inizio (YYYY-MM-DD): ");
            LocalDate inizio = LocalDate.parse(in.nextLine().trim(), FMT);

            System.out.print("Data fine (YYYY-MM-DD): ");
            LocalDate fine = LocalDate.parse(in.nextLine().trim(), FMT);

            System.out.print("Luogo: ");
            String luogo = in.nextLine().trim();

            System.out.print("Premio in denaro (es. 1000.0): ");
            double premio = Double.parseDouble(in.nextLine().trim());

            System.out.print("Dimensione massima team: ");
            int maxTeam = Integer.parseInt(in.nextLine().trim());

            // Selezione organizzatore
            Organizzatore org = (Organizzatore) selezionaPerRuolo("Organizzatore");
            if (org == null) { System.out.println("[!] Nessun Organizzatore disponibile."); return; }

            // Selezione giudice
            Giudice giu = (Giudice) selezionaPerRuolo("Giudice");
            if (giu == null) { System.out.println("[!] Nessun Giudice disponibile."); return; }

            // Selezione mentori (almeno 1)
            List<Mentore> mentori = new ArrayList<>();
            while (mentori.isEmpty()) {
                Mentore m = (Mentore) selezionaPerRuolo("Mentore");
                if (m == null) { System.out.println("[!] Nessun Mentore disponibile."); return; }
                if (mentori.contains(m)) {
                    System.out.println("[!] Mentore già aggiunto.");
                    continue;
                }
                mentori.add(m);
                System.out.print("Aggiungere un altro Mentore? (y/n): ");
                if (!in.nextLine().trim().equalsIgnoreCase("y")) break;
            }

            Hackathon h = control.crea(nome, regolamento, scadenza, inizio, fine,
                    luogo, premio, maxTeam, org, giu, mentori);

            System.out.println();
            System.out.println("✓ Hackathon creato con successo:");
            System.out.println("  id    = " + h.getId());
            System.out.println("  nome  = " + h.getNome());
            System.out.println("  stato = " + h.getStato().nome());
            System.out.println("  staff = Org=" + org.getEmail()
                    + ", Giu=" + giu.getEmail()
                    + ", Mentori=" + mentori.size());

        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private MembroStaff selezionaPerRuolo(String ruolo) {
        List<MembroStaff> candidati = new ArrayList<>();
        for (Utente u : hackHub.tuttiUtenti()) {
            if (u instanceof MembroStaff ms && ms.ruolo().equals(ruolo)) candidati.add(ms);
        }
        if (candidati.isEmpty()) return null;

        System.out.println("Scegli un " + ruolo + ":");
        for (int i = 0; i < candidati.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, candidati.get(i));
        }
        System.out.print("Indice: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        if (idx < 0 || idx >= candidati.size()) return null;
        return candidati.get(idx);
    }
}
