package it.unina.hackhub.model;

public class Organizzatore extends MembroStaff {

    public Organizzatore(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }

    @Override
    public String ruolo() { return "Organizzatore"; }
}
