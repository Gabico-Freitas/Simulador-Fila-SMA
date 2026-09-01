public class Evento {
    public double tEntrada;
    public double tSaida;
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
        this.tSaida = -1;
        this.tipo = tipo;
    }

    public void setSaida (double saida) {
        this.tSaida = saida;
    }

}
