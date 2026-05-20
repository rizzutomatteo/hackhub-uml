package it.unina.hackhub.state;

import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.observer.EventoHackathon;

public class InCorso implements StatoHackathon {

    @Override public boolean puoiSottomettere() { return true; }

    @Override
    public void apriValutazione(Hackathon h) {
        h.impostaStato(new InValutazione());
        h.notifica(EventoHackathon.HACKATHON_TERMINATO, h);
    }

    @Override public String nome() { return "InCorso"; }
}
