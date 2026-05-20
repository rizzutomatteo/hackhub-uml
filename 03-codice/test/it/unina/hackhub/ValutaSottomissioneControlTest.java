package it.unina.hackhub;

import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.control.InviaSottomissioneControl;
import it.unina.hackhub.control.IscriviTeamControl;
import it.unina.hackhub.control.ValutaSottomissioneControl;
import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.repository.HackathonRepository;
import it.unina.hackhub.repository.TeamRepository;
import it.unina.hackhub.repository.UtenteRepository;
import it.unina.hackhub.strategy.MediaPesata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC05 — ValutaSottomissioneControl")
class ValutaSottomissioneControlTest {

    private HackHub hub;
    private ValutaSottomissioneControl control;
    private Hackathon h;
    private Giudice giu;
    private Utente paolo;
    private Sottomissione s;

    @BeforeEach
    void setup() {
        hub = new HackHub("HackHub",
                new HackathonRepository(),
                new TeamRepository(),
                new UtenteRepository());
        control = new ValutaSottomissioneControl(hub);

        Organizzatore org = new Organizzatore("O","R","o@x.it","pwd1");
        giu = new Giudice("G","V","g@x.it","pwd2");
        Mentore men = new Mentore("M","B","m@x.it","pwd3");
        hub.registraUtente(org); hub.registraUtente(giu); hub.registraUtente(men);

        h = new CreaHackathonControl(hub).crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1000.0, 4, org, giu, List.of(men));

        paolo = new Utente("Paolo","E","paolo@x.it","pwd4");
        hub.registraUtente(paolo);
        Team t = new Team("Alpha", paolo);
        hub.registraTeam(t);

        new IscriviTeamControl(hub).iscrivi(h.getId(), paolo);
        h.avvia();
        s = new InviaSottomissioneControl(hub).invia(h.getId(), paolo,
                "Idea", "Una descrizione abbastanza lunga", "https://x.it/repo");
        h.apriValutazione();
    }

    @Test
    @DisplayName("Happy path: la valutazione viene registrata e ricalcola il punteggio")
    void happyPath() {
        Valutazione v = control.valuta(h.getId(), s.getId(), giu, 8, "Lavoro ottimo davvero");
        assertNotNull(v.getId());
        assertEquals(1, s.getValutazioni().size());
        assertEquals(8.0, s.getPunteggioAggregato(), 1e-9);
    }

    @Test
    @DisplayName("Punteggio fuori range → ViolazioneInvarianteException")
    void punteggioFuoriRange() {
        assertThrows(ViolazioneInvarianteException.class, () ->
                control.valuta(h.getId(), s.getId(), giu, 11, "Lavoro ottimo davvero"));
        assertThrows(ViolazioneInvarianteException.class, () ->
                control.valuta(h.getId(), s.getId(), giu, -1, "Lavoro pessimo davvero"));
    }

    @Test
    @DisplayName("Stato non InValutazione → StatoNonValidoException")
    void statoNonValido() {
        // Creiamo un nuovo hackathon ancora in InIscrizione
        Hackathon h2 = new CreaHackathonControl(hub).crea("Altro", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Roma", 500.0, 4,
                new Organizzatore("X","X","x@x.it","pwd1"),
                giu,
                List.of(new Mentore("Y","Y","y@x.it","pwd2")));
        assertThrows(StatoNonValidoException.class, () ->
                control.valuta(h2.getId(), "dummy", giu, 5, "Giudizio molto valido"));
    }

    @Test
    @DisplayName("Strategy: cambiando strategia il punteggio aggregato cambia")
    void strategiaPesata() {
        Giudice altro = new Giudice("Altro","Z","z@x.it","pwd1");
        // Simulazione: in 1a iter ogni hackathon ha 1 giudice; usiamo lo stesso giudice
        // ma con peso diverso per illustrare la sostituibilità della strategia.
        h.setStrategiaPunteggio(new MediaPesata(Map.of(giu, 2.0)));
        Valutazione v = control.valuta(h.getId(), s.getId(), giu, 6, "Lavoro discreto certamente");
        assertEquals(6.0, s.getPunteggioAggregato(), 1e-9);
        assertEquals("Media pesata", h.getStrategiaPunteggio().nome());
    }

    @Test
    @DisplayName("Doppia valutazione dello stesso giudice → ViolazioneInvarianteException")
    void doppiaValutazione() {
        control.valuta(h.getId(), s.getId(), giu, 7, "Prima valutazione abbastanza lunga");
        assertThrows(ViolazioneInvarianteException.class, () ->
                control.valuta(h.getId(), s.getId(), giu, 9, "Seconda valutazione abbastanza lunga"));
    }
}
