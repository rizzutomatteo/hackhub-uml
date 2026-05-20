package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

/**
 * Stub: in 1a iterazione l'invio email è simulato a console.
 * In fase Spring Boot verrà sostituito da un vero JavaMailSender.
 */
public class NotificaEmailMsg extends Notifica {

    public NotificaEmailMsg(Utente destinatario, EventoHackathon evento, String contenuto) {
        super(destinatario, evento, contenuto);
    }

    @Override
    public void invia() {
        System.out.printf("[NOTIFICA-EMAIL  ] %-25s → To:%s : %s%n",
                evento.name(),
                destinatario == null ? "<broadcast>" : destinatario.getEmail(),
                contenuto);
    }
}
