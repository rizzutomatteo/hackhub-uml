package it.unina.hackhub.control;

import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.observer.EventoHackathon;

public class ValutaSottomissioneControl {

    private final HackHub hackHub;

    public ValutaSottomissioneControl(HackHub hackHub) {
        this.hackHub = hackHub;
    }

    public Valutazione valuta(String idHackathon, String idSottomissione, Giudice giudice,
                              int punteggio, String giudizioScritto) {

        Hackathon h = hackHub.trovaHackathon(idHackathon);
        if (h == null) throw new ViolazioneInvarianteException(
            "Hackathon non trovato: " + idHackathon);

        // Solo il Giudice assegnato a quell'hackathon può valutare
        if (!giudice.equals(h.getGiudice()))
            throw new ViolazioneInvarianteException(
                "Solo il Giudice assegnato a questo hackathon può valutarne le sottomissioni");

        // Pattern State
        if (!h.puoiValutare())
            throw new StatoNonValidoException(
                "Valutazioni non aperte (stato " + h.getStato().nome() + ")");

        Sottomissione s = null;
        for (Sottomissione candidata : h.getSottomissioni()) {
            if (candidata.getId().equals(idSottomissione) && !candidata.èSostituita()) {
                s = candidata;
                break;
            }
        }
        if (s == null) throw new ViolazioneInvarianteException(
            "Sottomissione non trovata o sostituita: " + idSottomissione);

        if (s.èValutataDa(giudice))
            throw new ViolazioneInvarianteException(
                "Questa sottomissione è già stata valutata dal giudice corrente");

        // Costruzione + invariante punteggio sono nella Valutazione (defense in depth)
        Valutazione v = new Valutazione(giudice, punteggio, giudizioScritto);
        s.aggiungiValutazione(v);

        // Pattern Strategy: ricalcolo aggregato delegato alla strategia dell'hackathon
        s.ricalcolaPunteggio(h.getStrategiaPunteggio());

        // Pattern Observer: notifica se valutazioni complete
        if (h.tutteSottomissioniValutate()) {
            h.notifica(EventoHackathon.VALUTAZIONI_COMPLETE, h);
        }

        return v;
    }
}
