public class Aleatorio {
    private int a;
    private int c;
    private int M;
    private int anterior;

    public Aleatorio(int a, int c, int M, int seed) {
        this.a = a;
        this.c = c;
        this.M = M;
        this.anterior = seed;
    }
    
    public double NextRandom(){
        anterior = (a*anterior+c)%M;
        return (double)anterior/M;
    }
}
