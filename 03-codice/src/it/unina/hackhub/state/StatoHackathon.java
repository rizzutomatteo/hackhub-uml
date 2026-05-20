package it.unina.hackhub.state;

import it.unina.hackhub.exception.StatoNonValidoException;
import it.unina.hackhub.model.Hackathon;

/**
 * Pattern State (GoF).
 * <p>L'Hackathon (Context) delega le operazioni che dipendono dallo stato
 * di vita ({@code puoi*}, {@code avvia}, {@code apriValutazione},
 * {@code concludi}) alla concreta implementazione di questa interfaccia.
 * <p>I metodi {@code puoi*} hanno default {@code false}: ciascun
 * stato concreto sovrascrive solo quelli che gli competono.
 * I metodi di transizione hanno default che solleva
 * {@link StatoNonValidoException}: le transizioni valide
 * sono dichiarate esplicitamente nei concreti.
 */
public interface StatoHackathon {

    default boolean puoiIscrivereTeam() { return false; }
    default boolean puoiSottomettere() { return false; }
    default boolean puoiValutare() { return false; }
    default boolean puoiProclamare() { return false; }

    default void avvia(Hackathon h) {
        throw new StatoNonValidoException(
            "Transizione non ammessa dallo stato '" + nome() + "': avvia()");
    }
    default void apriValutazione(Hackathon h) {
        throw new StatoNonValidoException(
            "Transizione non ammessa dallo stato '" + nome() + "': apriValutazione()");
    }
    default void concludi(Hackathon h) {
        throw new StatoNonValidoException(
            "Transizione non ammessa dallo stato '" + nome() + "': concludi()");
    }

    String nome();
}
