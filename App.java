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
        // Tempo total da simulação
        double tempoGlobal = 0;
        // Criação do escalonador, nele fica guardado os próximos eventos, ele decide qual será o próximo pelo tempo mais recente
        Escalonador esc = new Escalonador();
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de chegada
        double minArrival = 1, maxArrival = 5;
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de saída
        double minService = 4, maxService = 5;
        // Variável auxiliar para guardar o tempo que se passou do último evento para inserir na lista de tempos
        double tempoAux = 0;

        // Esses valores precisam ser alterador para o teste
        Aleatorio rnd = new Aleatorio(1103, 12345, 429496, 157987);

        // Isso aqui não altera (pelo menos não nessa entrega)
        Evento inicio = new Evento(3.0, Tipo.CHEGADA);
        esc.add(inicio);

        //Criação da fila1 (será a que os clientes chegarão primeiro)
        Fila fila1 = new Fila(qtdServidores, K, minArrival, maxArrival, minService, maxService);

        // Criação da fila2 (irá ser a atendida pelos servidores)
        Fila fila2 = new Fila(qtdServidores, K, minArrival, maxArrival, minService, maxService);

        int count = 100000;
        while (count > 0) {
            Evento evento = esc.getProxEvento(); //Verifica o escalonador para pegar o próximo evento

            // Insere o tempo que se passou entre o evento anterior ao evento a ocorrer no momento
            // na leitura de dados de cada espaço de fila e o tempo global

            tempoAux = evento.tEntrada - tempoGlobal;
            tempoGlobal = evento.tEntrada;

            switch (evento.tipo) {
                case Tipo.CHEGADA:
                    fila1.incTempo(tempoAux);
                    if (fila1.status() < fila1.capacity()) {
                        fila1.in();
                        if (fila1.status() <= fila1.servers()) {
                            esc.add(new Evento(tempoGlobal + rnd.proxTempo(minService, maxService), Tipo.PASSAGEM));
                        }
                    } else {
                        fila1.incLoss();
                    }
                    esc.add(new Evento(tempoGlobal + rnd.proxTempo(minArrival,maxArrival), Tipo.CHEGADA));
                    break;
                case Tipo.PASSAGEM:
                    fila1.incTempo(tempoAux);
                    fila1.out();
                    if (fila1.status() >= fila1.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.proxTempo(minService, maxService), Tipo.PASSAGEM));
                    }
                    if (fila2.status() < fila2.capacity()) {
                        fila2.in();
                        if (fila2.status() <= fila2.servers()) {
                            esc.add(new Evento(tempoGlobal + rnd.proxTempo(minService, maxService), Tipo.SAIDA));
                        }
                    } else {
                        fila2.incLoss();
                    }
                    break;
                case Tipo.SAIDA:
                    fila2.incTempo(tempoAux);
                    fila2.out();
                    if (fila2.status() >= fila2.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.proxTempo(minService, maxService), Tipo.SAIDA));
                    }
                    break;
            
                default:
                    break;
            }
            
            // Caso for chegada verifica se a fila está cheia
            // if (evento.tipo == Tipo.CHEGADA) {
            //     // Caso esteja cheia, somente incrementa o contador de clientes perdidos
            //     if (clientesNaFila >= K) {
            //         clientesPerdidos++;
            //     } else {
            //         // Se ela não estiver cheia insere o processo na fila e verifica se já pode ser contabilizado uma saída para ele
            //         // (depende da quantidade de servidores)
            //         clientesNaFila++;
            //         if (clientesNaFila <= qtdServidores) {
            //             esc.add(new Evento(tempoGlobal + rnd.proxTempo(aSaida, bSaida), Tipo.SAIDA));
            //         }
            //     }

            //     // Insere um novo evento de chegada (não deve depender se a fila está cheia ou não)
            //     esc.add(new Evento(tempoGlobal + rnd.proxTempo(aChegada,bChegada), Tipo.CHEGADA));

            // } else if (evento.tipo == Tipo.SAIDA) {
            //     // Decrementa o contador de clientes na fila e já prepara a próxima saída
            //     clientesNaFila--;
            //     if (clientesNaFila >= qtdServidores) {
            //         esc.add(new Evento(tempoGlobal + rnd.proxTempo(aSaida, bSaida), Tipo.SAIDA));
            //     }
            // }
            // Decrementa o count para o loop
            count--;
        }

        System.out.println("Tempos da fila 1: ");
        double tempos[] = fila1.getTimes();
        for (int i = 0; i < K + 1; i++) {
            System.out.println(i + ": " + tempos[i] + " (" + ((tempos[i]/tempoGlobal)*100) + "%)");
        }
        System.out.println("\n\nTempos da fila 2: ");
        double tempos2[] = fila2.getTimes();
        for (int i = 0; i < K + 1; i++) {
            System.out.println(i + ": " + tempos2[i] + " (" + ((tempos2[i]/tempoGlobal)*100) + "%)");
        }
        System.out.println("Clientes perdidos: " + fila1.loss()+fila2.loss());
        System.out.println("Tempo total em simulação: " + tempoGlobal);
    }
}
