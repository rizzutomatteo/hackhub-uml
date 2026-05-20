package it.unina.hackhub.exception;

public class StatoNonValidoException extends RuntimeException {
    public StatoNonValidoException(String messaggio) {
        super(messaggio);
    }
}
