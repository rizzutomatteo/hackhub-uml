package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

/**
 * Creator concreto: produce {@link NotificaConsoleMsg}.
 */
public class NotificaConsole extends NotificaPublisher {

    @Override
    protected Notifica creaNotifica(EventoHackathon evento, Utente destinatario, String contenuto) {
        return new NotificaConsoleMsg(destinatario, evento, contenuto);
    }
}
