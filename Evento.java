public class Evento {
    public double tEntrada;
    // public float tProcesso;
    public Tipo tipo;

    /*
    public Evento (float e, float p, Tipo t) {
        this.tEntrada = e;
        this.tProcesso = p;
        this.tipo = t;
    }
    */

    public Evento (double entrada, Tipo tipo) {
        this.tEntrada = entrada;
        this.tipo = tipo;
    }
}
