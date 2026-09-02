public class App {

    // ******************************************
    // PARA RODAR:
    //      Compilar: javac *.java
    //      Executar: java App <qtdServidores> <K>
    // ******************************************
    public static void main(String[] args) {
        // Quantidade de servidores disponíveis para o atendimento da fila
        int qtdServidores = Integer.parseInt(args[0]);
        // Tamanho máximo da fila
        int K = Integer.parseInt(args[1]);
        // Tempo acumulado que é calculado em cada estado
        double[] tempos = new double[K + 1];
        // Tempo total da simulação
        double tempoGlobal = 0;
        // Quantidade atual de clientes na fila
        int clientesNaFila = 0;
        // Criação do escalonador, nele fica guardado os próximos eventos, ele decide qual será o próximo pelo tempo mais recente
        Escalonador esc = new Escalonador();
        // Contador para a quantidade de clientes perdidos
        int clientesPerdidos = 0;
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de chegada
        double aChegada = 3, bChegada = 5;
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de saída
        double aSaida = 4, bSaida = 5;
        // Variável auxiliar para guardar o tempo que se passou do último evento para inserir na lista de tempos
        double tempoAux = 0;

        // Esses valores precisam ser alterador para o teste
        Aleatorio rnd = new Aleatorio(1103, 12345, 429496, 157987);

        // Isso aqui não altera (pelo menos não nessa entrega)
        Evento inicio = new Evento(3.0, Tipo.CHEGADA);
        esc.add(inicio);
        

        // int count = 100000; // 100.000 que foi pedido no módulo
        int count = 6;
        while (count > 0) {
            Evento evento = esc.getProxEvento(); //Verifica o arraylist para pegar o próximo evento 
            
            if (evento.tipo == Tipo.CHEGADA) {
                if (clientesNaFila >= K) {
                    // System.out.println("Fila cheia!");
                    clientesPerdidos++;
                } else {
                    tempoAux = evento.tEntrada - tempoGlobal;
                    tempos[clientesNaFila] += tempoAux;
                    tempoGlobal = evento.tEntrada; 
                    clientesNaFila++;
                    if (clientesNaFila <= qtdServidores) {
                        esc.add(new Evento(tempoGlobal + rnd.proxTempo(aSaida, bSaida), Tipo.SAIDA));
                    }

                    esc.add(new Evento(tempoGlobal + rnd.proxTempo(aChegada,bChegada), Tipo.CHEGADA));
                }
            } else if (evento.tipo == Tipo.SAIDA) {
                tempoAux = evento.tEntrada - tempoGlobal;
                tempos[clientesNaFila] += tempoAux;
                tempoGlobal = evento.tEntrada;
                clientesNaFila--;
                if (clientesNaFila >= qtdServidores) {
                    esc.add(new Evento(tempoGlobal + rnd.proxTempo(aSaida, bSaida), Tipo.SAIDA));
                }

                // clientesNaFila = fila.Saida();
            }
            count--;
        }

        for (int i = 0; i < K + 1; i++) {
            System.out.println(i + ": " + tempos[i] + " (" + ((tempos[i]/tempoGlobal)*100) + "%)");
        }
        System.out.println("Clientes perdidos: " + clientesPerdidos);
        System.out.println("Tempo total em simulação: " + tempoGlobal);
    }
}
