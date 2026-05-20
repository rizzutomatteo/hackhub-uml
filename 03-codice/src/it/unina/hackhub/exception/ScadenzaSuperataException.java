package it.unina.hackhub.exception;

public class ScadenzaSuperataException extends RuntimeException {
    public ScadenzaSuperataException(String messaggio) {
        super(messaggio);
    }
}
