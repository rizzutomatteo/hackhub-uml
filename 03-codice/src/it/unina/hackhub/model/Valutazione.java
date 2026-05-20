package it.unina.hackhub.model;

import it.unina.hackhub.exception.ViolazioneInvarianteException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Valutazione {

    public static final int PUNTEGGIO_MIN = 0;
    public static final int PUNTEGGIO_MAX = 10;
    public static final int LUNGHEZZA_MIN_GIUDIZIO = 10;

    private final String id;
    private final Giudice giudice;
    private final int punteggio;
    private final String giudizioScritto;
    private final Instant timestamp;

    public Valutazione(Giudice giudice, int punteggio, String giudizioScritto) {
        if (giudice == null) throw new ViolazioneInvarianteException("giudice nullo");
        if (punteggio < PUNTEGGIO_MIN || punteggio > PUNTEGGIO_MAX)
            throw new ViolazioneInvarianteException(
                "punteggio fuori range [" + PUNTEGGIO_MIN + "," + PUNTEGGIO_MAX + "]: " + punteggio);
        if (giudizioScritto == null || giudizioScritto.strip().length() < LUNGHEZZA_MIN_GIUDIZIO)
            throw new ViolazioneInvarianteException(
                "giudizio scritto troppo corto (min " + LUNGHEZZA_MIN_GIUDIZIO + " caratteri)");

        this.id = UUID.randomUUID().toString();
        this.giudice = giudice;
        this.punteggio = punteggio;
        this.giudizioScritto = giudizioScritto;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public Giudice getGiudice() { return giudice; }
    public int getPunteggio() { return punteggio; }
    public String getGiudizioScritto() { return giudizioScritto; }
    public Instant getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Valutazione{" + punteggio + "/10 by " + giudice.getNome() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Valutazione v) && Objects.equals(v.id, this.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
