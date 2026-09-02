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

    public Evento getProxEvento() {
        Evento resp = null;
        double tempo = Double.MAX_VALUE;
        int aux = 0;
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).tipo == Tipo.CHEGADA) {
                if (eventos.get(i).tEntrada < tempo) {
                    resp = eventos.get(i);
                    tempo = eventos.get(i).tEntrada;
                    // aux = i;
                }
            }
            if (eventos.get(i).tipo == Tipo.SAIDA) {
                if (eventos.get(i).tEntrada < tempo) {
                    resp = eventos.get(i);
                    tempo = eventos.get(i).tEntrada;
                    // aux = i;
                }
            }
        }
        eventos.remove(resp);
        return resp;
    }

    // Classe para ser utilizada como uma forma de "tabela" para verificar qual será
    // o próximo evento a ocorrer (depende do tempo)
}
