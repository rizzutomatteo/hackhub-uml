package it.unina.hackhub.model;

import it.unina.hackhub.exception.ViolazioneInvarianteException;

import java.util.Objects;
import java.util.UUID;

public class Utente {

    private final String id;
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;

    public Utente(String nome, String cognome, String email, String password) {
        if (nome == null || nome.isBlank())
            throw new ViolazioneInvarianteException("nome obbligatorio");
        if (cognome == null || cognome.isBlank())
            throw new ViolazioneInvarianteException("cognome obbligatorio");
        if (email == null || !email.contains("@"))
            throw new ViolazioneInvarianteException("email non valida");
        if (password == null || password.length() < 4)
            throw new ViolazioneInvarianteException("password troppo corta (min 4)");
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }

    boolean autenticaCon(String password) {
        return Objects.equals(this.password, password);
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " <" + email + ">";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Utente u) && Objects.equals(u.id, this.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
