package it.unina.hackhub.observer;

public interface Soggetto {
    void aggiungiOsservatore(Osservatore o);
    void rimuoviOsservatore(Osservatore o);
    void notifica(EventoHackathon evento, Object payload);
}
