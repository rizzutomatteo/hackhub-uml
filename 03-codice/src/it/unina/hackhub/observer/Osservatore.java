package it.unina.hackhub.observer;

public interface Osservatore {
    void aggiorna(Soggetto soggetto, EventoHackathon evento, Object payload);
}
