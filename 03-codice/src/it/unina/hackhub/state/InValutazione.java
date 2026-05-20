package it.unina.hackhub.state;

import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.observer.EventoHackathon;

public class InValutazione implements StatoHackathon {

    @Override public boolean puoiValutare() { return true; }
    @Override public boolean puoiProclamare() { return true; }

    @Override
    public void concludi(Hackathon h) {
        h.impostaStato(new Conclusa());
        h.notifica(EventoHackathon.VINCITORE_PROCLAMATO, h);
    }

    @Override public String nome() { return "InValutazione"; }
}
