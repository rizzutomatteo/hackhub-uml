package it.unina.hackhub.repository;

import it.unina.hackhub.model.Team;
import it.unina.hackhub.model.Utente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeamRepository {

    private final Map<String, Team> byId = new LinkedHashMap<>();

    public void salva(Team t) {
        if (t == null) return;
        byId.put(t.getId(), t);
    }

    public Team trovaPerId(String id) {
        return byId.get(id);
    }

    public Team trovaPerMembro(Utente u) {
        if (u == null) return null;
        for (Team t : byId.values()) {
            if (t.contieneMembro(u)) return t;
        }
        return null;
    }

    public List<Team> tutti() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }
}
