package it.unina.hackhub.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MembroStaff extends Utente {

    private final List<Hackathon> hackathonAssegnati = new ArrayList<>();

    protected MembroStaff(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }

    public void assegnaA(Hackathon h) {
        if (h == null) return;
        if (!hackathonAssegnati.contains(h)) hackathonAssegnati.add(h);
    }

    public List<Hackathon> getHackathonAssegnati() {
        return Collections.unmodifiableList(hackathonAssegnati);
    }

    public boolean èAssegnatoA(Hackathon h) {
        return hackathonAssegnati.contains(h);
    }

    public abstract String ruolo();
}
