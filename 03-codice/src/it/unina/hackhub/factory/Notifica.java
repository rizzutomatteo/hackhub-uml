package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

/**
 * Product astratto del pattern Factory Method.
 */
public abstract class Notifica {

    protected final Utente destinatario;
    protected final EventoHackathon evento;
    protected final String contenuto;

    protected Notifica(Utente destinatario, EventoHackathon evento, String contenuto) {
        this.destinatario = destinatario;
        this.evento = evento;
        this.contenuto = contenuto;
    }

    public Utente getDestinatario() { return destinatario; }
    public EventoHackathon getEvento() { return evento; }
    public String getContenuto() { return contenuto; }

    public abstract void invia();
}
