package it.unina.hackhub.model;

public class Giudice extends MembroStaff {

    public Giudice(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }

    @Override
    public String ruolo() { return "Giudice"; }
}
