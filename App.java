public class App {
    public static void main(String[] args) {
        int K = 10; // Max fila
        int[] tempos = new int[K + 1]; // tempo acumulado em cada estado
        double tempoGlobal = 0; // tmepo total
        int clientesNaFila = 0; // estado atual da fila
        Escalonador esc = new Escalonador();
        int clientesPerdidos = 0; // contador para quantidade de clientes perdidos pela fila cheia

        int seed = 0, a = 1, c = 0, m = 100; // Precisamos escolher números melhores (e bem maiores)
        Fila fila = new Fila(seed, a, c, m);
        

        Evento inicio = new Evento(3.0, Tipo.CHEGADA);
        esc.add(inicio);
        

        int count = 100000; // 100.000 que foi pedido no módulo
        while (count > 0) {
            Evento evento = esc.getProxEvento(); //Verifica o arraylist para pegar o próximo evento 
            
            if (evento.tipo == Tipo.CHEGADA) {
                // if fila cheia sai
                if (clientesNaFila == K) {
                    System.out.println("Fila cheia!");
                    clientesPerdidos++;
                } else {
                    // otherwise fila++
                    System.out.printf("Nova chegada (%d)\n", clientesNaFila);
                    clientesNaFila = fila.Chegada();
                }
            } else if (evento.tipo == Tipo.SAIDA) {
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
