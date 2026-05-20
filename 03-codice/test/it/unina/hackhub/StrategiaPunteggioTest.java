package it.unina.hackhub;

import it.unina.hackhub.model.Giudice;
import it.unina.hackhub.model.Valutazione;
import it.unina.hackhub.strategy.MediaPesata;
import it.unina.hackhub.strategy.MediaSemplice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern Strategy — calcolo punteggio")
class StrategiaPunteggioTest {

    private Giudice giudiceA() { return new Giudice("A", "X", "a@x.it", "pwd1"); }
    private Giudice giudiceB() { return new Giudice("B", "Y", "b@x.it", "pwd2"); }

    @Test
    @DisplayName("MediaSemplice su lista vuota → 0.0")
    void mediaSempliceVuota() {
        assertEquals(0.0, new MediaSemplice().calcola(List.of()));
    }

    @Test
    @DisplayName("MediaSemplice calcola correttamente la media aritmetica")
    void mediaSempliceCorretta() {
        Giudice g = giudiceA();
        Valutazione v1 = new Valutazione(g, 6, "giudizio dieci lettere1");
        Valutazione v2 = new Valutazione(g, 8, "giudizio dieci lettere2");
        Valutazione v3 = new Valutazione(g, 10, "giudizio dieci lettere3");
        double m = new MediaSemplice().calcola(List.of(v1, v2, v3));
        assertEquals(8.0, m, 1e-9);
    }

    @Test
    @DisplayName("MediaPesata con pesi assenti = MediaSemplice")
    void mediaPesataSenzaPesiCoincideConSemplice() {
        Giudice g = giudiceA();
        Valutazione v1 = new Valutazione(g, 4, "giudizio dieci lettere1");
        Valutazione v2 = new Valutazione(g, 8, "giudizio dieci lettere2");
        MediaPesata strat = new MediaPesata();
        assertEquals(6.0, strat.calcola(List.of(v1, v2)), 1e-9);
    }

    @Test
    @DisplayName("MediaPesata con pesi differenti per giudici")
    void mediaPesataConPesi() {
        Giudice ga = giudiceA();
        Giudice gb = giudiceB();
        Valutazione vA = new Valutazione(ga, 4, "giudizio dieci lettereA");
        Valutazione vB = new Valutazione(gb, 10, "giudizio dieci lettereB");
        MediaPesata strat = new MediaPesata(Map.of(ga, 1.0, gb, 3.0));
        // (4*1 + 10*3) / (1+3) = 34 / 4 = 8.5
        assertEquals(8.5, strat.calcola(List.of(vA, vB)), 1e-9);
    }

    @Test
    @DisplayName("Le strategie hanno nome distinto")
    void nomiStrategie() {
        assertEquals("Media semplice", new MediaSemplice().nome());
        assertEquals("Media pesata", new MediaPesata().nome());
    }
}
