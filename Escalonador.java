import java.util.LinkedList;

public class Escalonador {
    // public double saida;
    // public double chegada;
    private LinkedList<Evento> eventos;

    /*
    public Escalonador (double chegada, double saida) {
        this.chegada = chegada;
        this.saida = saida;
        this.eventos = new ArrayList<>();
    }
    */

    public Escalonador() {
        this.eventos = new LinkedList<>();
    }

    // Adiciona o evento na lista
    public void add(Evento e) {
        eventos.add(e);
    }

    // Busca qual é o evento com o menor tempo e o retorna como resposta
    // (Apaga ele da lista no final da execução)
    public Evento getProxEvento() {
        Evento resp = null;
        double tempo = Double.MAX_VALUE;
        int aux = 0;
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).tEntrada < tempo) {
                resp = eventos.get(i);
                tempo = eventos.get(i).tEntrada;
                aux = i;
            }
        }
        
        if (tempo != Double.MAX_VALUE) eventos.remove(aux);
        return resp;
    }

    // Classe para ser utilizada como uma forma de "tabela" para verificar qual será
    // o próximo evento a ocorrer (depende do tempo)
}
