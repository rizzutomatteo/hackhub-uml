package it.unina.hackhub.state;

public class Conclusa implements StatoHackathon {
    // Stato terminale: tutti i puoi* tornano false (default dell'interfaccia)
    // e tutte le transizioni sollevano StatoNonValidoException.

    @Override public String nome() { return "Conclusa"; }
}
