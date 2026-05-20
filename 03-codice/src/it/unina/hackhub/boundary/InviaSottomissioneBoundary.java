package it.unina.hackhub.boundary;

import it.unina.hackhub.control.InviaSottomissioneControl;
import it.unina.hackhub.model.HackHub;
import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.model.Sottomissione;
import it.unina.hackhub.model.Utente;

import java.util.List;
import java.util.Scanner;

public class InviaSottomissioneBoundary {

    private final Scanner in;
    private final InviaSottomissioneControl control;
    private final HackHub hackHub;

    public InviaSottomissioneBoundary(Scanner in, InviaSottomissioneControl control, HackHub hackHub) {
        this.in = in;
        this.control = control;
        this.hackHub = hackHub;
    }

    public void esegui() {
        System.out.println();
        System.out.println("================ UC04 - Invia Sottomissione ================");
        try {
            Utente utente = selezionaUtente();
            if (utente == null) return;

            List<Hackathon> tutti = hackHub.tuttiHackathon();
            System.out.println("Tutti gli hackathon:");
            for (int i = 0; i < tutti.size(); i++) {
                Hackathon h = tutti.get(i);
                System.out.printf("  [%d] %s [stato=%s]%n",
                        i + 1, h.getNome(), h.getStato().nome());
            }
            System.out.print("Scegli hackathon: ");
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idx < 0 || idx >= tutti.size()) {
                System.out.println("[!] Scelta non valida.");
                return;
            }
            Hackathon h = tutti.get(idx);

            System.out.print("Titolo: ");
            String titolo = in.nextLine().trim();
            System.out.print("Descrizione: ");
            String descr = in.nextLine().trim();
            System.out.print("Link (http(s)://...): ");
            String link = in.nextLine().trim();

            Sottomissione s = control.invia(h.getId(), utente, titolo, descr, link);
            System.out.println("✓ Sottomissione inviata:");
            System.out.println("  id          = " + s.getId());
            System.out.println("  team        = " + s.getTeam().getNome());
            System.out.println("  titolo      = " + s.getTitolo());
            System.out.println("  timestamp   = " + s.getTimestampInvio());
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
