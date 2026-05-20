package it.unina.hackhub.boundary;

import it.unina.hackhub.control.IscriviTeamControl;
import it.unina.hackhub.model.HackHub;
import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.model.Team;
import it.unina.hackhub.model.Utente;

import java.util.List;
import java.util.Scanner;

/**
 * UC03 — Iscrivi Team a Hackathon (CLI Boundary).
 */
public class IscriviTeamBoundary {

    private final Scanner in;
    private final IscriviTeamControl control;
    private final HackHub hackHub;

    public IscriviTeamBoundary(Scanner in, IscriviTeamControl control, HackHub hackHub) {
        this.in = in;
        this.control = control;
        this.hackHub = hackHub;
    }

    public void esegui() {
        System.out.println();
        System.out.println("============= UC03 - Iscrivi Team a Hackathon =============");

        try {
            Utente utente = selezionaUtente();
            if (utente == null) return;

            List<Hackathon> disponibili = hackHub.hackathonInIscrizione();
            if (disponibili.isEmpty()) {
                System.out.println("[!] Nessun hackathon in iscrizione.");
                return;
            }
            System.out.println("Hackathon in iscrizione:");
            for (int i = 0; i < disponibili.size(); i++) {
                Hackathon h = disponibili.get(i);
                System.out.printf("  [%d] %s @ %s (scadenza %s)%n",
                        i + 1, h.getNome(), h.getLuogo(), h.getScadenzaIscrizioni());
            }
            System.out.print("Scegli hackathon: ");
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idx < 0 || idx >= disponibili.size()) {
                System.out.println("[!] Scelta non valida.");
                return;
            }
            Hackathon h = disponibili.get(idx);

            Team t = control.iscrivi(h.getId(), utente);
            System.out.println("✓ Team " + t.getNome() + " iscritto all'hackathon " + h.getNome());
        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private Utente selezionaUtente() {
        List<Utente> utenti = hackHub.tuttiUtenti();
        System.out.println("Login (Membro Team):");
        for (int i = 0; i < utenti.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, utenti.get(i));
        }
        System.out.print("Indice: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        return (idx >= 0 && idx < utenti.size()) ? utenti.get(idx) : null;
    }
}
