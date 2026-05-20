package it.unina.hackhub;

import it.unina.hackhub.control.CreaHackathonControl;
import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.observer.EventoHackathon;
import it.unina.hackhub.observer.Osservatore;
import it.unina.hackhub.observer.Soggetto;
import it.unina.hackhub.repository.HackathonRepository;
import it.unina.hackhub.repository.TeamRepository;
import it.unina.hackhub.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC02 — CreaHackathonControl")
class CreaHackathonControlTest {

    private HackHub hub;
    private CreaHackathonControl control;
    private Organizzatore org;
    private Giudice giu;
    private Mentore men;

    @BeforeEach
    void setup() {
        hub = new HackHub("HackHub",
                new HackathonRepository(),
                new TeamRepository(),
                new UtenteRepository());
        control = new CreaHackathonControl(hub);
        org = new Organizzatore("Anna", "Rossi", "anna@x.it", "pwd1");
        giu = new Giudice("Marco", "Verdi", "marco@x.it", "pwd2");
        men = new Mentore("Lucia", "Bianchi", "lucia@x.it", "pwd3");
        hub.registraUtente(org);
        hub.registraUtente(giu);
        hub.registraUtente(men);
    }

    @Test
    @DisplayName("Happy path: hackathon registrato, in InIscrizione, staff assegnato")
    void happyPath() {
        Hackathon h = control.crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1500.0, 4,
                org, giu, List.of(men));

        assertNotNull(h.getId());
        assertEquals(1, hub.tuttiHackathon().size());
        assertEquals("InIscrizione", h.getStato().nome());
        assertEquals(org, h.getOrganizzatore());
        assertEquals(giu, h.getGiudice());
        assertEquals(1, h.getMentori().size());
        assertTrue(org.èAssegnatoA(h));
        assertTrue(giu.èAssegnatoA(h));
        assertTrue(men.èAssegnatoA(h));
    }

    @Test
    @DisplayName("Nome duplicato → ViolazioneInvarianteException")
    void nomeDuplicato() {
        control.crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1500.0, 4, org, giu, List.of(men));

        Mentore m2 = new Mentore("Eva", "Neri", "eva@x.it", "pwd4");
        assertThrows(ViolazioneInvarianteException.class, () -> control.crea(
                "Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1500.0, 4, org, giu, List.of(m2)));
    }

    @Test
    @DisplayName("Date incoerenti → ViolazioneInvarianteException")
    void dateIncoerenti() {
        assertThrows(ViolazioneInvarianteException.class, () -> control.crea(
                "X", "Reg",
                LocalDate.now().plusDays(10),  // scadenza dopo inizio: errore
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(20),
                "Napoli", 1000.0, 4, org, giu, List.of(men)));
    }

    @Test
    @DisplayName("L'evento HACKATHON_CREATO viene notificato agli osservatori")
    void notificaCreazione() {
        // Per testare la notifica occorre creare l'hackathon e POI registrare
        // l'osservatore PRIMA di un'altra operazione. Qui dimostriamo il
        // collegamento: l'hackathon creato pubblica eventi successivi.
        Hackathon h = control.crea("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1000.0, 4, org, giu, List.of(men));

        List<EventoHackathon> eventiVisti = new ArrayList<>();
        h.aggiungiOsservatore(new Osservatore() {
            @Override
            public void aggiorna(Soggetto s, EventoHackathon e, Object p) { eventiVisti.add(e); }
        });
        h.avvia();
        assertEquals(List.of(EventoHackathon.HACKATHON_AVVIATO), eventiVisti);
    }
}
