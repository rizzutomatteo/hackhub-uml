package it.unina.hackhub.boundary;

import it.unina.hackhub.control.ValutaSottomissioneControl;
import it.unina.hackhub.model.*;

import java.util.List;
import java.util.Scanner;

public class ValutaSottomissioneBoundary {

    private final Scanner in;
    private final ValutaSottomissioneControl control;
    private final HackHub hackHub;

    public ValutaSottomissioneBoundary(Scanner in, ValutaSottomissioneControl control, HackHub hackHub) {
        this.in = in;
        this.control = control;
        this.hackHub = hackHub;
    }

    public void esegui() {
        System.out.println();
        System.out.println("=============== UC05 - Valuta Sottomissione ===============");
        try {
            Giudice g = selezionaGiudice();
            if (g == null) return;

            List<Hackathon> tutti = hackHub.tuttiHackathon();
            System.out.println("Hackathon a cui sei assegnato come Giudice:");
            int shown = 0;
            for (int i = 0; i < tutti.size(); i++) {
                Hackathon h = tutti.get(i);
                if (h.getGiudice().equals(g)) {
                    System.out.printf("  [%d] %s [stato=%s]%n",
                            i + 1, h.getNome(), h.getStato().nome());
                    shown++;
                }
            }
            if (shown == 0) {
                System.out.println("[!] Non sei assegnato come Giudice a nessun hackathon.");
                return;
            }
            System.out.print("Scegli hackathon: ");
            int idxH = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idxH < 0 || idxH >= tutti.size()) {
                System.out.println("[!] Scelta non valida.");
                return;
            }
            Hackathon h = tutti.get(idxH);

            List<Sottomissione> daValutare = h.sottomissioniNonValutateDa(g);
            if (daValutare.isEmpty()) {
                System.out.println("[!] Nessuna sottomissione da valutare per te in questo hackathon.");
                return;
            }
            System.out.println("Sottomissioni da valutare:");
            for (int i = 0; i < daValutare.size(); i++) {
                Sottomissione s = daValutare.get(i);
                System.out.printf("  [%d] %s — team=%s%n",
                        i + 1, s.getTitolo(), s.getTeam().getNome());
            }
            System.out.print("Scegli sottomissione: ");
            int idxS = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idxS < 0 || idxS >= daValutare.size()) {
                System.out.println("[!] Scelta non valida.");
                return;
            }
            Sottomissione s = daValutare.get(idxS);

            System.out.print("Punteggio [0..10]: ");
            int punteggio = Integer.parseInt(in.nextLine().trim());
            System.out.print("Giudizio scritto (≥10 caratteri): ");
            String giudizio = in.nextLine();

            Valutazione v = control.valuta(h.getId(), s.getId(), g, punteggio, giudizio);
            System.out.println("✓ Valutazione registrata:");
            System.out.println("  punteggio aggregato sottomissione = "
                    + s.getPunteggioAggregato() + " (strategia: "
                    + h.getStrategiaPunteggio().nome() + ")");
            System.out.println("  v.id = " + v.getId());
        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private Giudice selezionaGiudice() {
        List<Utente> giudici = hackHub.tuttiUtenti().stream()
                .filter(u -> u instanceof Giudice).toList();
        if (giudici.isEmpty()) {
            System.out.println("[!] Nessun Giudice registrato.");
            return null;
        }
        System.out.println("Login (Giudice):");
        for (int i = 0; i < giudici.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, giudici.get(i));
        }
        System.out.print("Indice: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        return (idx >= 0 && idx < giudici.size()) ? (Giudice) giudici.get(idx) : null;
    }
}
