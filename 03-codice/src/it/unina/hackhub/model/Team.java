package it.unina.hackhub.model;

import it.unina.hackhub.exception.ViolazioneInvarianteException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Team {

    private final String id;
    private final String nome;
    private final List<Utente> membri = new ArrayList<>();

    public Team(String nome, Utente fondatore) {
        if (nome == null || nome.isBlank())
            throw new ViolazioneInvarianteException("nome team obbligatorio");
        if (fondatore == null)
            throw new ViolazioneInvarianteException("un team deve avere almeno un fondatore");
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.membri.add(fondatore);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }

    public List<Utente> getMembri() {
        return Collections.unmodifiableList(membri);
    }

    public int numeroMembri() { return membri.size(); }

    public boolean contieneMembro(Utente u) {
        return membri.contains(u);
    }

    public void aggiungiMembro(Utente u) {
        if (u == null) throw new ViolazioneInvarianteException("utente nullo");
        if (membri.contains(u))
            throw new ViolazioneInvarianteException("utente già nel team");
        membri.add(u);
    }

    @Override
    public String toString() {
        return "Team{" + nome + ", membri=" + membri.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Team t) && Objects.equals(t.id, this.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
