package it.unina.hackhub.strategy;

import it.unina.hackhub.model.Valutazione;

import java.util.List;

public interface StrategiaPunteggio {
    double calcola(List<Valutazione> valutazioni);
    String nome();
}
