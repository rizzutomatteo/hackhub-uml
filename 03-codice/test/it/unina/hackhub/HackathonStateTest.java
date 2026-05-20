package it.unina.hackhub;

import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern State — ciclo di vita Hackathon")
class HackathonStateTest {

    private Hackathon h;

    @BeforeEach
    void setup() {
        Organizzatore org = new Organizzatore("A", "B", "org@x.it", "pwd1");
        Giudice giu = new Giudice("C", "D", "giu@x.it", "pwd2");
        Mentore men = new Mentore("E", "F", "men@x.it", "pwd3");
        h = new Hackathon("Demo", "Reg",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "Napoli", 1000.0, 4, org, giu, List.of(men));
    }

    @Test
    @DisplayName("Stato iniziale è InIscrizione")
    void statoInizialeInIscrizione() {
        assertInstanceOf(InIscrizione.class, h.getStato());
        assertEquals("InIscrizione", h.getStato().nome());
        assertTrue(h.puoiIscrivereTeam());
        assertFalse(h.puoiSottomettere());
        assertFalse(h.puoiValutare());
        assertFalse(h.puoiProclamare());
    }

    @Test
    @DisplayName("Transizione completa: InIscrizione → InCorso → InValutazione → Conclusa")
    void transizioneCompleta() {
        h.avvia();
        assertInstanceOf(InCorso.class, h.getStato());
        assertTrue(h.puoiSottomettere());
        assertFalse(h.puoiIscrivereTeam());

        h.apriValutazione();
        assertInstanceOf(InValutazione.class, h.getStato());
        assertTrue(h.puoiValutare());
        assertTrue(h.puoiProclamare());

        h.concludi();
        assertInstanceOf(Conclusa.class, h.getStato());
        assertFalse(h.puoiIscrivereTeam());
        assertFalse(h.puoiSottomettere());
        assertFalse(h.puoiValutare());
    }

    @Test
    @DisplayName("Transizioni non ammesse sollevano StatoNonValidoException")
    void transizioniNonAmmesse() {
        assertThrows(StatoNonValidoException.class, () -> h.apriValutazione());
        assertThrows(StatoNonValidoException.class, () -> h.concludi());

        h.avvia();
        assertThrows(StatoNonValidoException.class, () -> h.avvia());
        assertThrows(StatoNonValidoException.class, () -> h.concludi());

        h.apriValutazione();
        assertThrows(StatoNonValidoException.class, () -> h.avvia());
        assertThrows(StatoNonValidoException.class, () -> h.apriValutazione());

        h.concludi();
        assertThrows(StatoNonValidoException.class, () -> h.avvia());
        assertThrows(StatoNonValidoException.class, () -> h.apriValutazione());
        assertThrows(StatoNonValidoException.class, () -> h.concludi());
    }

    @Test
    @DisplayName("In Conclusa nessuna operazione 'puoi*' è ammessa")
    void conclusaTerminale() {
        h.avvia();
        h.apriValutazione();
        h.concludi();
        assertEquals("Conclusa", h.getStato().nome());
        assertFalse(h.puoiIscrivereTeam());
        assertFalse(h.puoiSottomettere());
        assertFalse(h.puoiValutare());
        assertFalse(h.puoiProclamare());
    }
}
