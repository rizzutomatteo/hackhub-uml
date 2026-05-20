package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;
import it.unina.hackhub.observer.Osservatore;
import it.unina.hackhub.observer.Soggetto;

import java.util.Collection;
import java.util.Collections;

/**
 * Creator astratto del pattern Factory Method.
 * <p>È anche un {@link Osservatore}: si registra sul {@link Soggetto}
 * (Hackathon) e, quando riceve un evento, costruisce e invia una
 * Notifica concreta delegando la creazione al factory method
 * {@link #creaNotifica(EventoHackathon, Utente)}.
 */
public abstract class NotificaPublisher implements Osservatore {

    @Override
    public void aggiorna(Soggetto soggetto, EventoHackathon evento, Object payload) {
        Collection<Utente> destinatari = estraiDestinatari(evento, payload);
        String contenuto = formattaContenuto(evento, payload);
        for (Utente d : destinatari) {
            Notifica n = creaNotifica(evento, d, contenuto);
            n.invia();
        }
    }

    /**
     * Factory Method: lasciato alle sottoclassi concrete decidere quale
     * sottoclasse di {@link Notifica} istanziare.
     */
    protected abstract Notifica creaNotifica(EventoHackathon evento, Utente destinatario, String contenuto);

    /**
     * Politica di estrazione destinatari. Default: nessuno (broadcast).
     * Le sottoclassi possono ridefinire per scegliere a chi inviare.
     */
    protected Collection<Utente> estraiDestinatari(EventoHackathon evento, Object payload) {
        // Per la 1a iterazione: notifica generica senza destinatario specifico.
        // Singleton-list che ammette null (a differenza di List.of(...)).
        return Collections.singletonList(null);
    }

    protected String formattaContenuto(EventoHackathon evento, Object payload) {
        return "Evento HackHub: " + evento.name()
                + (payload != null ? " — " + payload : "");
    }
}
