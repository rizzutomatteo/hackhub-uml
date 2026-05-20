package it.unina.hackhub;

import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.control.InviaSottomissioneControl;
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

@DisplayName("UC04 — InviaSottomissioneControl")
class InviaSottomissioneControlTest {

    private HackHub hub;
    private InviaSottomissioneControl control;
    private Hackathon h;
    private Utente paolo;

    @BeforeEach
    void setup() {
        hub = new HackHub("HackHub",
                new HackathonRepository(),
                new TeamRepository(),
                new UtenteRepository());
        control = new InviaSottomissioneControl(hub);

        Organizzatore org = new Organizzatore("O","R","o@x.it","pwd1");
        Giudice giu = new Giudice("G","V","g@x.it","pwd2");
        Mentore men = new Mentore("M","B","m@x.it","pwd3");
        hub.registraUtente(org); hub.registraUtente(giu); hub.registraUtente(men);

        h = new CreaHackathonControl(hub).crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1000.0, 4, org, giu, List.of(men));

        paolo = new Utente("Paolo","Esposito","paolo@x.it","pwd4");
        hub.registraUtente(paolo);
        Team t = new Team("Alpha", paolo);
        hub.registraTeam(t);

        new IscriviTeamControl(hub).iscrivi(h.getId(), paolo);
        // Avanza lo stato per consentire le sottomissioni
        h.avvia();
    }

    @Test
    @DisplayName("Happy path: sottomissione registrata e collegata al team")
    void happyPath() {
        Sottomissione s = control.invia(h.getId(), paolo,
                "Idea", "Una descrizione", "https://github.com/team/x");
        assertNotNull(s.getId());
        assertEquals(1, h.getSottomissioni().size());
        assertFalse(s.èSostituita());
    }

    @Test
    @DisplayName("Stato non InCorso → StatoNonValidoException")
    void statoNonValido() {
        h.apriValutazione();
        assertThrows(StatoNonValidoException.class, () -> control.invia(h.getId(), paolo,
                "T", "D", "https://x"));
    }

    @Test
    @DisplayName("URL non valido → ViolazioneInvarianteException")
    void linkInvalido() {
        assertThrows(ViolazioneInvarianteException.class, () -> control.invia(
                h.getId(), paolo, "Idea", "Desc", "non-un-url"));
    }

    @Test
    @DisplayName("Sostituzione: la sottomissione precedente è marcata come sostituita")
    void sostituzione() {
        Sottomissione s1 = control.invia(h.getId(), paolo,
                "Titolo v1", "Desc v1", "https://x.it/v1");
        Sottomissione s2 = control.invia(h.getId(), paolo,
                "Titolo v2", "Desc v2", "https://x.it/v2");
        assertNotEquals(s1.getId(), s2.getId());
        assertTrue(s1.èSostituita(), "la prima sottomissione deve essere marcata sostituita");
        assertFalse(s2.èSostituita());
        assertEquals(2, h.getSottomissioni().size());
        assertEquals(s2, h.sottomissioneDi(s1.getTeam()),
                "la sottomissione attiva del team deve essere quella nuova");
    }
}
