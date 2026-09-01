public class App {
    public static void main(String[] args) {
        int K = 10; // Max fila
        int[] times = new int[K + 1]; // tempo acumulado em cada estado
        double tempoGlobal = 0; // tmepo total
        int clientesNaFila = 0; // estado atual da fila
        Escalonador esc = new Escalonador();

        int seed = 0, a = 1, c = 0, m = 100;
        Fila fila = new Fila(seed, a, c, m);
        int count = 15;

        Evento inicio = new Evento(3.0, Tipo.CHEGADA);
        esc.add(inicio);
        

        while (count > 0) {
            double proximoEvento = fila.NextRandom(); 
            // evento = NextEvent() é feito para verificarmos no escalonador
            // qual é o próximo evento a ser feito
            times[clientesNaFila] += proximoEvento;
            tempoGlobal += proximoEvento;

            Fila.TipoEvento evento = fila.NextEvent();
            if (evento == Fila.TipoEvento.CHEGADA) {
                // if fila cheia sai
                if (clientesNaFila == K) {
                    System.out.println("Fila cheia!");
                } else {
                    // otherwise fila++
                    System.out.printf("Nova chegada (%d)\n", clientesNaFila);
                    clientesNaFila = fila.Chegada();
                }
            } else if (evento == Fila.TipoEvento.SAIDA) {
                // fila--
                clientesNaFila = fila.Saida();
            }
            count--;
        }

        // for (int i = 0; i < K + 1; i++) {
        //     System.out.println(i + ": " + times[i] + " (" + (times[i]/tempoGlobal*100) + "%)");
        // }
    }
}
