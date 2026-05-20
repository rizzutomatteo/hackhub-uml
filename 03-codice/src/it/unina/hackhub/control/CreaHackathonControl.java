package it.unina.hackhub.control;

import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.model.*;
import it.unina.hackhub.observer.EventoHackathon;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller del caso d'uso UC02 — Crea Hackathon.
 * <p>GRASP <b>Controller</b>: riceve gli eventi dal Boundary e coordina
 * la creazione dell'aggregato Hackathon (Information Expert: HackHub).
 */
public class CreaHackathonControl {

    private final HackHub hackHub;

    public CreaHackathonControl(HackHub hackHub) {
        this.hackHub = hackHub;
    }

    public Hackathon crea(String nome, String regolamento,
                          LocalDate scadenzaIscrizioni, LocalDate dataInizio, LocalDate dataFine,
                          String luogo, double premio, int dimensioneMaxTeam,
                          Organizzatore organizzatore, Giudice giudice, List<Mentore> mentori) {

        // validazione locale (defense in depth oltre alla validazione del costruttore)
        if (nome != null) {
            for (Hackathon esistente : hackHub.tuttiHackathon()) {
                if (nome.equalsIgnoreCase(esistente.getNome()))
                    throw new ViolazioneInvarianteException(
                        "Esiste già un hackathon con questo nome: " + nome);
            }
        }

        Hackathon h = new Hackathon(nome, regolamento,
                scadenzaIscrizioni, dataInizio, dataFine,
                luogo, premio, dimensioneMaxTeam,
                organizzatore, giudice, mentori);

        // Assegnamento staff agli hackathon (relazione bidirezionale)
        organizzatore.assegnaA(h);
        giudice.assegnaA(h);
        for (Mentore m : mentori) m.assegnaA(h);

        hackHub.registraHackathon(h);

        // Pattern Observer: evento HACKATHON_CREATO
        h.notifica(EventoHackathon.HACKATHON_CREATO, h);

        return h;
    }
}
