package it.unina.hackhub.control;

import it.unina.hackhub.exception.ScadenzaSuperataException;
import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.HackHub;
import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.model.Team;
import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

import java.time.LocalDate;

public class IscriviTeamControl {

    private final HackHub hackHub;

    public IscriviTeamControl(HackHub hackHub) {
        this.hackHub = hackHub;
    }

    public Team iscrivi(String idHackathon, Utente utente) {
        if (utente == null) throw new ViolazioneInvarianteException("utente nullo");

        Team t = hackHub.trovaTeamDi(utente);
        if (t == null) throw new ViolazioneInvarianteException(
            "L'utente non appartiene ad alcun team");

        Hackathon h = hackHub.trovaHackathon(idHackathon);
        if (h == null) throw new ViolazioneInvarianteException(
            "Hackathon non trovato: " + idHackathon);

        // Pattern State: la verifica di "ammissibilità" è delegata allo stato corrente
        if (!h.puoiIscrivereTeam())
            throw new StatoNonValidoException(
                "Iscrizioni chiuse per l'hackathon nello stato " + h.getStato().nome());

        if (LocalDate.now().isAfter(h.getScadenzaIscrizioni()))
            throw new ScadenzaSuperataException(
                "Scadenza iscrizioni superata (" + h.getScadenzaIscrizioni() + ")");

        if (t.numeroMembri() > h.getDimensioneMaxTeam())
            throw new ViolazioneInvarianteException(
                "Team troppo grande: " + t.numeroMembri()
                + " > limite " + h.getDimensioneMaxTeam());

        if (h.èIscritto(t))
            throw new ViolazioneInvarianteException("Team già iscritto a questo hackathon");

        // Eccezione interna (es. teams duplicati) gestita da Hackathon
        h.iscriviTeam(t);

        // Pattern Observer
        h.notifica(EventoHackathon.TEAM_ISCRITTO, t);

        return t;
    }
}
