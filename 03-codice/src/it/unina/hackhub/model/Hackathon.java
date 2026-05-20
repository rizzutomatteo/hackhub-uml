package it.unina.hackhub.model;

import it.unina.hackhub.exception.ViolazioneInvarianteException;
import it.unina.hackhub.observer.EventoHackathon;
import it.unina.hackhub.observer.Osservatore;
import it.unina.hackhub.observer.Soggetto;
import it.unina.hackhub.state.InIscrizione;
import it.unina.hackhub.state.StatoHackathon;
import it.unina.hackhub.strategy.MediaSemplice;
import it.unina.hackhub.strategy.StrategiaPunteggio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Hackathon implements Soggetto {

    // --- Identità e dati anagrafici --------------------------------------------------
    private final String id;
    private final String nome;
    private final String regolamento;
    private final LocalDate scadenzaIscrizioni;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String luogo;
    private final double premio;
    private final int dimensioneMaxTeam;

    // --- Staff -----------------------------------------------------------------------
    private final Organizzatore organizzatore;
    private final Giudice giudice;
    private final List<Mentore> mentori = new ArrayList<>();

    // --- Partecipanti ----------------------------------------------------------------
    private final List<Team> teamIscritti = new ArrayList<>();
    private final List<Sottomissione> sottomissioni = new ArrayList<>();

    // --- Pattern State ---------------------------------------------------------------
    private StatoHackathon stato;

    // --- Pattern Strategy ------------------------------------------------------------
    private StrategiaPunteggio strategiaPunteggio;

    // --- Pattern Observer ------------------------------------------------------------
    private final List<Osservatore> osservatori = new ArrayList<>();

    // --- Esito finale ----------------------------------------------------------------
    private Team vincitore;

    public Hackathon(String nome, String regolamento,
                     LocalDate scadenzaIscrizioni, LocalDate dataInizio, LocalDate dataFine,
                     String luogo, double premio, int dimensioneMaxTeam,
                     Organizzatore organizzatore, Giudice giudice, List<Mentore> mentori) {

        if (nome == null || nome.isBlank())
            throw new ViolazioneInvarianteException("nome hackathon obbligatorio");
        if (regolamento == null || regolamento.isBlank())
            throw new ViolazioneInvarianteException("regolamento obbligatorio");
        if (scadenzaIscrizioni == null || dataInizio == null || dataFine == null)
            throw new ViolazioneInvarianteException("date obbligatorie");
        if (!scadenzaIscrizioni.isBefore(dataInizio))
            throw new ViolazioneInvarianteException(
                "scadenzaIscrizioni deve essere strettamente anteriore a dataInizio");
        if (!dataFine.isAfter(dataInizio))
            throw new ViolazioneInvarianteException("dataFine deve essere posteriore a dataInizio");
        if (luogo == null || luogo.isBlank())
            throw new ViolazioneInvarianteException("luogo obbligatorio");
        if (premio < 0)
            throw new ViolazioneInvarianteException("premio non può essere negativo");
        if (dimensioneMaxTeam < 1)
            throw new ViolazioneInvarianteException("dimensioneMaxTeam deve essere >= 1");
        if (organizzatore == null) throw new ViolazioneInvarianteException("Organizzatore obbligatorio");
        if (giudice == null)        throw new ViolazioneInvarianteException("Giudice obbligatorio");
        if (mentori == null || mentori.isEmpty())
            throw new ViolazioneInvarianteException("almeno un Mentore obbligatorio");

        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.regolamento = regolamento;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premio = premio;
        this.dimensioneMaxTeam = dimensioneMaxTeam;
        this.organizzatore = organizzatore;
        this.giudice = giudice;
        this.mentori.addAll(mentori);

        // Default: State = InIscrizione, Strategy = MediaSemplice
        this.stato = new InIscrizione();
        this.strategiaPunteggio = new MediaSemplice();
    }

    // ==============================  GETTER  ============================================
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getRegolamento() { return regolamento; }
    public LocalDate getScadenzaIscrizioni() { return scadenzaIscrizioni; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public String getLuogo() { return luogo; }
    public double getPremio() { return premio; }
    public int getDimensioneMaxTeam() { return dimensioneMaxTeam; }
    public Organizzatore getOrganizzatore() { return organizzatore; }
    public Giudice getGiudice() { return giudice; }
    public List<Mentore> getMentori() { return Collections.unmodifiableList(mentori); }
    public List<Team> getTeamIscritti() { return Collections.unmodifiableList(teamIscritti); }
    public List<Sottomissione> getSottomissioni() { return Collections.unmodifiableList(sottomissioni); }
    public StatoHackathon getStato() { return stato; }
    public StrategiaPunteggio getStrategiaPunteggio() { return strategiaPunteggio; }
    public Team getVincitore() { return vincitore; }

    // ==============================  STAFF MANAGEMENT  ==================================

    public void aggiungiMentore(Mentore m) {
        if (m == null) throw new ViolazioneInvarianteException("mentore nullo");
        if (mentori.contains(m))
            throw new ViolazioneInvarianteException("mentore già assegnato");
        mentori.add(m);
    }

    // ==============================  STATE PATTERN: DELEGA  ============================

    /** Setter del Context: invocato dagli {@link StatoHackathon} concreti durante la transizione. */
    public void impostaStato(StatoHackathon nuovoStato) {
        if (nuovoStato == null) throw new ViolazioneInvarianteException("stato nullo");
        this.stato = nuovoStato;
    }

    public boolean puoiIscrivereTeam() { return stato.puoiIscrivereTeam(); }
    public boolean puoiSottomettere() { return stato.puoiSottomettere(); }
    public boolean puoiValutare()    { return stato.puoiValutare(); }
    public boolean puoiProclamare()  { return stato.puoiProclamare(); }

    public void avvia()             { stato.avvia(this); }
    public void apriValutazione()   { stato.apriValutazione(this); }
    public void concludi()          { stato.concludi(this); }

    // ==============================  STRATEGY PATTERN  ==================================

    public void setStrategiaPunteggio(StrategiaPunteggio strategia) {
        if (strategia == null) throw new ViolazioneInvarianteException("strategia nulla");
        this.strategiaPunteggio = strategia;
    }

    // ==============================  ISCRIZIONI E SOTTOMISSIONI  ========================

    public boolean èIscritto(Team t) { return teamIscritti.contains(t); }

    public void iscriviTeam(Team t) {
        if (t == null) throw new ViolazioneInvarianteException("team nullo");
        if (teamIscritti.contains(t))
            throw new ViolazioneInvarianteException("team già iscritto");
        if (t.numeroMembri() > dimensioneMaxTeam)
            throw new ViolazioneInvarianteException(
                "team troppo grande: " + t.numeroMembri() + " > " + dimensioneMaxTeam);
        teamIscritti.add(t);
    }

    public void aggiungiSottomissione(Sottomissione s) {
        if (s == null) throw new ViolazioneInvarianteException("sottomissione nulla");
        sottomissioni.add(s);
    }

    public Sottomissione sottomissioneDi(Team t) {
        for (Sottomissione s : sottomissioni) {
            if (Objects.equals(s.getTeam(), t) && !s.èSostituita()) return s;
        }
        return null;
    }

    public List<Sottomissione> sottomissioniNonValutateDa(Giudice g) {
        List<Sottomissione> out = new ArrayList<>();
        for (Sottomissione s : sottomissioni) {
            if (!s.èSostituita() && !s.èValutataDa(g)) out.add(s);
        }
        return out;
    }

    public boolean tutteSottomissioniValutate() {
        if (sottomissioni.isEmpty()) return false;
        for (Sottomissione s : sottomissioni) {
            if (!s.èSostituita() && !s.èValutataDa(giudice)) return false;
        }
        return true;
    }

    public void proclamaVincitore(Team t) {
        if (!stato.puoiProclamare())
            throw new ViolazioneInvarianteException(
                "Non si può proclamare un vincitore nello stato " + stato.nome());
        if (!teamIscritti.contains(t))
            throw new ViolazioneInvarianteException("team non iscritto");
        if (!tutteSottomissioniValutate())
            throw new ViolazioneInvarianteException("non tutte le sottomissioni sono state valutate");
        this.vincitore = t;
        notifica(EventoHackathon.VINCITORE_PROCLAMATO, t);
        concludi();
    }

    // ==============================  OBSERVER PATTERN  ==================================

    @Override
    public void aggiungiOsservatore(Osservatore o) {
        if (o == null) return;
        if (!osservatori.contains(o)) osservatori.add(o);
    }

    @Override
    public void rimuoviOsservatore(Osservatore o) {
        osservatori.remove(o);
    }

    @Override
    public void notifica(EventoHackathon evento, Object payload) {
        // Copia difensiva: l'aggiornamento potrebbe modificare la lista.
        for (Osservatore o : new ArrayList<>(osservatori)) {
            o.aggiorna(this, evento, payload);
        }
    }

    @Override
    public String toString() {
        return "Hackathon{" + nome + " [" + stato.nome() + "]"
                + ", team=" + teamIscritti.size()
                + ", sott=" + sottomissioni.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Hackathon h) && Objects.equals(h.id, this.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
