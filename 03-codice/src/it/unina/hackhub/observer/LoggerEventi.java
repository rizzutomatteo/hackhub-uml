package it.unina.hackhub.observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerEventi implements Osservatore {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void aggiorna(Soggetto soggetto, EventoHackathon evento, Object payload) {
        String ts = LocalDateTime.now().format(FMT);
        String src = soggetto == null ? "?" : soggetto.getClass().getSimpleName();
        System.out.printf("[%s] [LOG] %-25s da %s   payload=%s%n",
                ts, evento.name(), src, payload);
    }
}
