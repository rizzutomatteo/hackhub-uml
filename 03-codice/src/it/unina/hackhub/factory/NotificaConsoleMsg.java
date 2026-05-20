package it.unina.hackhub.factory;

import it.unina.hackhub.model.Utente;
import it.unina.hackhub.observer.EventoHackathon;

public class NotificaConsoleMsg extends Notifica {

    public NotificaConsoleMsg(Utente destinatario, EventoHackathon evento, String contenuto) {
        super(destinatario, evento, contenuto);
    }

    @Override
    public void invia() {
        System.out.printf("[NOTIFICA-CONSOLE] %-25s → %s : %s%n",
                evento.name(),
                destinatario == null ? "<broadcast>" : destinatario.getEmail(),
                contenuto);
    }
}
