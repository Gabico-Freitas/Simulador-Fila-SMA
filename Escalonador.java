import java.util.ArrayList;

public class Escalonador {
    // public double saida;
    // public double chegada;
    private ArrayList<Evento> eventos;

    /*
    public Escalonador (double chegada, double saida) {
        this.chegada = chegada;
        this.saida = saida;
        this.eventos = new ArrayList<>();
    }
    */

    public Escalonador() {
        this.eventos = new ArrayList<>();
    }

    // adiciona evento no index fornecido
    // TODO: tempos sao em double, percorrer arraylist para descobrir onde cai
    public void add(Evento e) {
        eventos.add(e);
    }

    // Classe para ser utilizada como uma forma de "tabela" para verificar qual será
    // o próximo evento a ocorrer (depende do tempo)
}
