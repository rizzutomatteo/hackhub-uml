package it.unina.hackhub.model;

public class Mentore extends MembroStaff {

    public Mentore(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }

    @Override
    public String ruolo() { return "Mentore"; }
}
