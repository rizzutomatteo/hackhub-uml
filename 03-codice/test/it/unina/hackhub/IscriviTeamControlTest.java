package it.unina.hackhub;

import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.control.IscriviTeamControl;
import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.repository.HackathonRepository;
import it.unina.hackhub.repository.TeamRepository;
import it.unina.hackhub.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC03 — IscriviTeamControl")
class IscriviTeamControlTest {

    private HackHub hub;
    private IscriviTeamControl control;
    private Hackathon h;
    private Utente paolo;
    private Team team;

    @BeforeEach
    void setup() {
        hub = new HackHub("HackHub",
                new HackathonRepository(),
                new TeamRepository(),
                new UtenteRepository());
        control = new IscriviTeamControl(hub);

        Organizzatore org = new Organizzatore("O", "R", "o@x.it", "pwd1");
        Giudice giu = new Giudice("G", "V", "g@x.it", "pwd2");
        Mentore men = new Mentore("M", "B", "m@x.it", "pwd3");
        hub.registraUtente(org); hub.registraUtente(giu); hub.registraUtente(men);

        h = new CreaHackathonControl(hub).crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1000.0, 4, org, giu, List.of(men));

        paolo = new Utente("Paolo", "Esposito", "paolo@x.it", "pwd4");
        Utente sara = new Utente("Sara", "Romano", "sara@x.it", "pwd5");
        hub.registraUtente(paolo); hub.registraUtente(sara);
        team = new Team("Alpha", paolo);
        team.aggiungiMembro(sara);
        hub.registraTeam(team);
    }

    @Test
    @DisplayName("Happy path: team viene iscritto e l'hackathon lo riconosce")
    void happyPath() {
        Team t = control.iscrivi(h.getId(), paolo);
        assertEquals(team.getId(), t.getId());
        assertTrue(h.èIscritto(team));
        assertEquals(1, h.getTeamIscritti().size());
    }

    @Test
    @DisplayName("Stato non InIscrizione → StatoNonValidoException")
    void statoNonValido() {
        h.avvia();
        assertThrows(StatoNonValidoException.class, () -> control.iscrivi(h.getId(), paolo));
    }

    @Test
    @DisplayName("Team troppo grande → ViolazioneInvarianteException")
    void teamTroppoGrande() {
        Utente a = new Utente("A","A","a@x.it","pwd1");
        Utente b = new Utente("B","B","b@x.it","pwd2");
        Utente c = new Utente("C","C","c@x.it","pwd3");
        hub.registraUtente(a); hub.registraUtente(b); hub.registraUtente(c);
        team.aggiungiMembro(a); team.aggiungiMembro(b); team.aggiungiMembro(c);
        // ora team ha 5 membri ma maxTeam=4
        assertEquals(5, team.numeroMembri());
        assertThrows(ViolazioneInvarianteException.class, () -> control.iscrivi(h.getId(), paolo));
    }

    @Test
    @DisplayName("Iscrizione duplicata → ViolazioneInvarianteException")
    void iscrizioneDuplicata() {
        control.iscrivi(h.getId(), paolo);
        assertThrows(ViolazioneInvarianteException.class, () -> control.iscrivi(h.getId(), paolo));
    }

    @Test
    @DisplayName("Utente senza team → ViolazioneInvarianteException")
    void utenteSenzaTeam() {
        Utente solo = new Utente("Solo","S","solo@x.it","pwd1");
        hub.registraUtente(solo);
        assertThrows(ViolazioneInvarianteException.class, () -> control.iscrivi(h.getId(), solo));
    }
}
