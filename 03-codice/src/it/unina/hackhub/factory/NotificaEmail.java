package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

/**
 * Creator concreto: produce {@link NotificaEmailMsg}.
 * Stub per iterazione 1 (vera integrazione mail in fase Spring Boot).
 */
public class NotificaEmail extends NotificaPublisher {

    @Override
    protected Notifica creaNotifica(EventoHackathon evento, Utente destinatario, String contenuto) {
        return new NotificaEmailMsg(destinatario, evento, contenuto);
    }
}
