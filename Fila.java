import java.util.LinkedList;
import java.util.Queue;

public class Fila{
    private double anterior;
    private int a;
    private int c;
    private int m;
    private Queue<Integer> q;

    public enum TipoEvento {
        CHEGADA,
        SAIDA
    }

    public Fila(int seed, int a, int c, int m){
        this.anterior = (double) seed;
        this.a = a;
        this.c = c;
        this.m = m;
        this.q = new LinkedList<>();
    }

    public Fila.TipoEvento NextEvent(){
        // ler o proximo evento da tabela
        return Fila.TipoEvento.CHEGADA;
    }
    
    public int Chegada(){
        // evento de chegada
        c++;
        q.add(c);
        return q.size();
    }
    public int Saida(){
        // evento de saida
        return q.size(); // retorna tamanho da fila
        // return q.remove(); // alternativa para retornar elemento removido
    }
    
}
