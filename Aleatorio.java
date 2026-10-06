public class Aleatorio {
    private int a;
    private int c;
    private int M;
    private int anterior;
    public long sorteios = 0;
    private long limite;

    public Aleatorio(int a, int c, int M, int seed) {
        this.a = a;
        this.c = c;
        this.M = M;
        this.anterior = seed;
    }

    public void setLimite(long limite) {
        this.limite = limite;
    }

    public boolean hasNext() { return sorteios < limite; }

    public void setSeed(int seed) {
        this.anterior = seed;
        this.sorteios = 0;
    }
    
    // Cria o valor pseudo aleatório
    private double NextRandom(){
        anterior = (a*anterior+c)%M;
        sorteios++;
        return (double)anterior/M;
        
    }

    // Calcula o próximo tempo para o evento
    // (Irá depender a entrada irá depender se for uma chegada ou uma saída)
    public double aleatorio (double a, double b) {
        return a + ((b-a) * NextRandom());
    }
}
