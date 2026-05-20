package it.unina.hackhub.strategy;

import it.unina.hackhub.model.Giudice;
import it.unina.hackhub.model.Valutazione;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Media pesata sui Giudici: ad ogni giudice è associato un peso.
 * Se un giudice non ha peso esplicito, il suo peso è 1.0.
 */
public class MediaPesata implements StrategiaPunteggio {

    private final Map<Giudice, Double> pesiPerGiudice;

    public MediaPesata() {
        this.pesiPerGiudice = new HashMap<>();
    }

    public MediaPesata(Map<Giudice, Double> pesiPerGiudice) {
        this.pesiPerGiudice = new HashMap<>(pesiPerGiudice);
    }

    public void setPeso(Giudice g, double peso) {
        if (peso < 0) throw new IllegalArgumentException("peso non può essere negativo");
        pesiPerGiudice.put(g, peso);
    }

    @Override
    public double calcola(List<Valutazione> valutazioni) {
        if (valutazioni == null || valutazioni.isEmpty()) return 0.0;
        double sommaPesata = 0.0;
        double sommaPesi = 0.0;
        for (Valutazione v : valutazioni) {
            double peso = pesiPerGiudice.getOrDefault(v.getGiudice(), 1.0);
            sommaPesata += v.getPunteggio() * peso;
            sommaPesi += peso;
        }
        return sommaPesi == 0.0 ? 0.0 : sommaPesata / sommaPesi;
    }

    @Override
    public String nome() { return "Media pesata"; }
}
