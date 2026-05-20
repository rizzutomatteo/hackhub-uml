package it.unina.hackhub.control;

import it.unina.hackhub.exception.ScadenzaSuperataException;
import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.observer.EventoHackathon;

import java.time.LocalDate;

public class InviaSottomissioneControl {

    private final HackHub hackHub;

    public InviaSottomissioneControl(HackHub hackHub) {
        this.hackHub = hackHub;
    }

    public Sottomissione invia(String idHackathon, Utente utente,
                               String titolo, String descrizione, String link) {

        Team t = hackHub.trovaTeamDi(utente);
        if (t == null) throw new ViolazioneInvarianteException(
            "L'utente non appartiene ad alcun team");

        Hackathon h = hackHub.trovaHackathon(idHackathon);
        if (h == null) throw new ViolazioneInvarianteException(
            "Hackathon non trovato: " + idHackathon);

        // Pattern State
        if (!h.puoiSottomettere())
            throw new StatoNonValidoException(
                "Sottomissioni non aperte (stato " + h.getStato().nome() + ")");

        if (LocalDate.now().isAfter(h.getDataFine()))
            throw new ScadenzaSuperataException(
                "Data di fine hackathon superata (" + h.getDataFine() + ")");

        if (!h.èIscritto(t))
            throw new ViolazioneInvarianteException(
                "Il tuo team non è iscritto a questo hackathon");

        // UC15: se esiste già una sottomissione del team, la sostituiamo
        Sottomissione esistente = h.sottomissioneDi(t);
        EventoHackathon evento = EventoHackathon.SOTTOMISSIONE_RICEVUTA;
        if (esistente != null) {
            esistente.marcaSostituita();
            evento = EventoHackathon.SOTTOMISSIONE_AGGIORNATA;
        }

        Sottomissione s = new Sottomissione(t, h, titolo, descrizione, link);
        h.aggiungiSottomissione(s);

        // Pattern Observer (con Factory Method dentro il publisher)
        h.notifica(evento, s);

        return s;
    }
}
