package it.unina.hackhub.repository;

import it.unina.hackhub.model.Hackathon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DAO in-memory per Hackathon (1a iterazione).
 * Verrà sostituito da uno Spring Data JpaRepository nella seconda iterazione.
 */
public class HackathonRepository {

    private final Map<String, Hackathon> byId = new LinkedHashMap<>();

    public void salva(Hackathon h) {
        if (h == null) return;
        byId.put(h.getId(), h);
    }

    public Hackathon trovaPerId(String id) {
        return byId.get(id);
    }

    public List<Hackathon> tutti() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }

    public List<Hackathon> filtraPerStato(String nomeStato) {
        List<Hackathon> out = new ArrayList<>();
        for (Hackathon h : byId.values()) {
            if (Objects.equals(h.getStato().nome(), nomeStato)) out.add(h);
        }
        return out;
    }
}
