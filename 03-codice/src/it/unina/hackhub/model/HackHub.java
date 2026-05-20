package it.unina.hackhub.model;

import it.unina.hackhub.repository.HackathonRepository;
import it.unina.hackhub.repository.TeamRepository;
import it.unina.hackhub.repository.UtenteRepository;

import java.util.List;

/**
 * Radice del modello di dominio.
 * <p><b>Non è un Singleton</b>: l'istanza unica è gestita esplicitamente
 * (creata nel {@code Main} e iniettata nei Control). Questa scelta:
 * - rende il codice testabile (in JUnit ogni test crea il proprio HackHub),
 * - rispetta il vincolo della traccia (≥ 2 pattern diversi da Singleton).
 * <p>Applica GRASP <b>Information Expert</b> per le query "tutti gli hackathon",
 * "trova team del dato utente", ecc. — perché possiede i riferimenti ai repository.
 */
public class HackHub {

    private final String nome;
    private final HackathonRepository hackathonRepo;
    private final TeamRepository teamRepo;
    private final UtenteRepository utenteRepo;

    public HackHub(String nome,
                   HackathonRepository hackathonRepo,
                   TeamRepository teamRepo,
                   UtenteRepository utenteRepo) {
        this.nome = nome;
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
        this.utenteRepo = utenteRepo;
    }

    public String getNome() { return nome; }

    // ==============================  UTENTI  ============================================

    public void registraUtente(Utente u) { utenteRepo.salva(u); }
    public Utente trovaUtente(String id) { return utenteRepo.trovaPerId(id); }
    public List<Utente> tuttiUtenti() { return utenteRepo.tutti(); }

    // ==============================  TEAM  ==============================================

    public void registraTeam(Team t) { teamRepo.salva(t); }
    public Team trovaTeam(String id) { return teamRepo.trovaPerId(id); }
    public Team trovaTeamDi(Utente u) { return teamRepo.trovaPerMembro(u); }
    public List<Team> tuttiTeam() { return teamRepo.tutti(); }

    // ==============================  HACKATHON  =========================================

    public void registraHackathon(Hackathon h) { hackathonRepo.salva(h); }
    public Hackathon trovaHackathon(String id) { return hackathonRepo.trovaPerId(id); }
    public List<Hackathon> tuttiHackathon() { return hackathonRepo.tutti(); }
    public List<Hackathon> hackathonInIscrizione() {
        return hackathonRepo.filtraPerStato("InIscrizione");
    }
}
