package it.unina.hackhub.repository;

import it.unina.hackhub.model.Utente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UtenteRepository {

    private final Map<String, Utente> byId = new LinkedHashMap<>();

    public void salva(Utente u) {
        if (u == null) return;
        byId.put(u.getId(), u);
    }

    public Utente trovaPerId(String id) {
        return byId.get(id);
    }

    public Utente trovaPerEmail(String email) {
        if (email == null) return null;
        for (Utente u : byId.values()) {
            if (email.equalsIgnoreCase(u.getEmail())) return u;
        }
        return null;
    }

    public List<Utente> tutti() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }
}
