package it.unina.hackhub.state;

import it.unina.hackhub.model.Hackathon;
import it.unina.hackhub.observer.EventoHackathon;

public class InIscrizione implements StatoHackathon {

    @Override public boolean puoiIscrivereTeam() { return true; }

    @Override
    public void avvia(Hackathon h) {
        h.impostaStato(new InCorso());
        h.notifica(EventoHackathon.HACKATHON_AVVIATO, h);
    }

    @Override public String nome() { return "InIscrizione"; }
}
