public class Evento {
    public double tEntrada;
    public Tipo tipo;
    public int filaOrigem;
    public int filaDestino;

    public Evento (double entrada, Tipo tipo, int filaOrigem, int filaDestino) {
        this.tEntrada = entrada;
        this.tipo = tipo;
        this.filaOrigem = filaOrigem;
        this.filaDestino = filaDestino;
    }

    @Override
    public String toString() {
        return "Evento [tEntrada=" + tEntrada + ", tipo=" + tipo + ", filaOrigem=" + filaOrigem + ", filaDestino="
                + filaDestino + "]";
    }

    
}
