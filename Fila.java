public class Fila{
    private double anterior;
    private int a;
    private int c;
    private int m;

    public enum TipoEvento {
        CHEGADA,
        SAIDA
    }

    public Fila(int seed, int a, int c, int m){
        this.anterior = (double) seed;
        this.a = a;
        this.c = c;
        this.m = m;
    }

    public double NextRandom(){
        anterior = (a*anterior+c)%m;
        return (double)anterior/m;
    }

    public Fila.TipoEvento NextEvent(){
        // ler o proximo evento da tabela
        return Fila.TipoEvento.CHEGADA;
    }
    
    public int Chegada(){
        // evento de chegada
        return 0;
    }
    public int Saida(){
        // evento de saida
        return 0;
    }
    
}
