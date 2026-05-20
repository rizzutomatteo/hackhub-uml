package it.unina.hackhub.strategy;

import it.unina.hackhub.model.Valutazione;

import java.util.List;

public class MediaSemplice implements StrategiaPunteggio {

    @Override
    public double calcola(List<Valutazione> valutazioni) {
        if (valutazioni == null || valutazioni.isEmpty()) return 0.0;
        double somma = 0.0;
        for (Valutazione v : valutazioni) somma += v.getPunteggio();
        return somma / valutazioni.size();
    }

    @Override
    public String nome() { return "Media semplice"; }
}
