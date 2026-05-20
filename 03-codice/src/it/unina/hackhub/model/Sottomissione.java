package it.unina.hackhub.model;

import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.strategy.StrategiaPunteggio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sottomissione {

    private final String id;
    private final Team team;
    private final Hackathon hackathon;
    private String titolo;
    private String descrizione;
    private String link;
    private Instant timestampInvio;

    private final List<Valutazione> valutazioni = new ArrayList<>();
    private double punteggioAggregato;
    private boolean sostituita;

    public Sottomissione(Team team, Hackathon hackathon, String titolo, String descrizione, String link) {
        if (team == null)      throw new ViolazioneInvarianteException("team obbligatorio");
        if (hackathon == null) throw new ViolazioneInvarianteException("hackathon obbligatorio");
        if (titolo == null || titolo.strip().length() < 3)
            throw new ViolazioneInvarianteException("titolo troppo corto (min 3)");
        if (descrizione == null || descrizione.length() > 2000)
            throw new ViolazioneInvarianteException("descrizione obbligatoria, max 2000 caratteri");
        if (link == null || !(link.startsWith("http://") || link.startsWith("https://")))
            throw new ViolazioneInvarianteException("link deve iniziare con http:// o https://");

        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.hackathon = hackathon;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.link = link;
        this.timestampInvio = Instant.now();
        this.punteggioAggregato = 0.0;
    }

    public String getId() { return id; }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public String getTitolo() { return titolo; }
    public String getDescrizione() { return descrizione; }
    public String getLink() { return link; }
    public Instant getTimestampInvio() { return timestampInvio; }

    public List<Valutazione> getValutazioni() {
        return Collections.unmodifiableList(valutazioni);
    }

    public double getPunteggioAggregato() { return punteggioAggregato; }
    public boolean èSostituita() { return sostituita; }

    /** UC15 - sostituzione: aggiorna i contenuti e ribatte il timestamp. */
    public void aggiorna(String titolo, String descrizione, String link) {
        if (titolo == null || titolo.strip().length() < 3)
            throw new ViolazioneInvarianteException("titolo troppo corto");
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.link = link;
        this.timestampInvio = Instant.now();
    }

    public void marcaSostituita() { this.sostituita = true; }

    public void aggiungiValutazione(Valutazione v) {
        if (v == null) throw new ViolazioneInvarianteException("valutazione nulla");
        valutazioni.add(v);
    }

    public boolean èValutataDa(Giudice g) {
        for (Valutazione v : valutazioni) {
            if (Objects.equals(v.getGiudice(), g)) return true;
        }
        return false;
    }

    public boolean èValutata() { return !valutazioni.isEmpty(); }

    /**
     * Information Expert: la Sottomissione possiede le valutazioni,
     * quindi è naturalmente l'esperta del calcolo aggregato.
     * Delega il "come" alla {@link StrategiaPunteggio} (Strategy).
     */
    public void ricalcolaPunteggio(StrategiaPunteggio strategia) {
        this.punteggioAggregato = strategia.calcola(valutazioni);
    }

    @Override
    public String toString() {
        return "Sottomissione{" + titolo + " by " + team.getNome()
                + ", val=" + valutazioni.size() + ", agg=" + punteggioAggregato + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Sottomissione s) && Objects.equals(s.id, this.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
