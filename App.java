public class App {

    // ******************************************
    // PARA RODAR:
    //      Compilar: javac *.java
    //      Executar: java App <qtdServidores1> <K1> <qtdServidores2> <K2>
    // ******************************************
    public static void main(String[] args) {
        // Quantidade de servidores disponíveis para o atendimento da fila1
        int qtdServidores1 = Integer.parseInt(args[0]);
        // Tamanho máximo da fila1
        int K1 = Integer.parseInt(args[1]);
        
        // Quantidade de servidores disponíveis para o atendimento da fila2
        int qtdServidores2 = Integer.parseInt(args[2]);
        // Tamanho máximo da fila2
        int K2 = Integer.parseInt(args[3]);

        // Tempo total da simulação
        double tempoGlobal = 0;
        // Criação do escalonador, nele fica guardado os próximos eventos, ele decide qual será o próximo pelo tempo mais recente
        Escalonador esc = new Escalonador();
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de chegada
        double minArrival = 1, maxArrival = 5;
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de passagem
        double minPass = 4, maxPass = 5;
        // Variáveis para o cálculo dos valores pseudoaleatórios de tempo de saída
        double minService = 1, maxService = 3;
        // Variável auxiliar para guardar o tempo que se passou do último evento para inserir na lista de tempos
        double tempoAux = 0;

        if (args.length < 4) {
            System.out.println("Erro. Insira no formato:\n java App <qtdServidores1> <K1> <qtdServidores2> <K2>");
            System.exit(0);
        }

        // Esses valores precisam ser alterador para o teste
        Aleatorio rnd = new Aleatorio(1103, 12345, 429496, 157987);

        // Inicia um evento inicial para inserir uma chegada de cliente novo no tempo desejado
        Evento inicio = new Evento(2.5, Tipo.CHEGADA);
        esc.add(inicio);

        //Criação da fila1 (será a que os clientes chegarão primeiro)
        Fila fila1 = new Fila(qtdServidores1, K1);

        // Criação da fila2 (irá ser a atendida pelos servidores após passar pela fila1)
        Fila fila2 = new Fila(qtdServidores2, K2);

        int count = 100000;
        while (count > 0) {
            Evento evento = esc.getProxEvento(); //Verifica o escalonador para pegar o próximo evento

            // Insere o tempo que se passou entre o evento anterior ao evento a ocorrer no momento
            // na leitura de dados de cada espaço de fila e o tempo global

            tempoAux = evento.tEntrada - tempoGlobal;
            tempoGlobal = evento.tEntrada;
            
            fila1.incTempo(tempoAux);
            fila2.incTempo(tempoAux);

            switch (evento.tipo) {
                case Tipo.CHEGADA:
                    // fila1.incTempo(tempoAux);
                    if (fila1.status() < fila1.capacity()) {
                        fila1.in();
                        if (fila1.status() <= fila1.servers()) {
                            esc.add(new Evento(tempoGlobal + rnd.proxTempo(minPass, maxPass), Tipo.PASSAGEM));
                        }
                    } else {
                        fila1.incLoss();
                    }
                    esc.add(new Evento(tempoGlobal + rnd.proxTempo(minArrival,maxArrival), Tipo.CHEGADA));
                    break;
                case Tipo.PASSAGEM:
                    // fila1.incTempo(tempoAux);
                    // fila2.incTempo(tempoAux);
                    fila1.out();
                    if (fila1.status() >= fila1.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.proxTempo(minPass, maxPass), Tipo.PASSAGEM));
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
                    // fila2.incTempo(tempoAux);
                    fila2.out();
                    if (fila2.status() >= fila2.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.proxTempo(minService, maxService), Tipo.SAIDA));
                    }
                    break;
            
                default:
                    break;
            }
            count--;
        }

        System.out.println("============================================");
        System.out.println("Fila1 (G/G/" + qtdServidores1 + "/" + K1 + ")");
        System.out.println("Chegada: " + minArrival + " ... " + maxArrival);
        System.out.println("Passagem: " + minPass + " ... " + maxPass);
        System.out.println("============================================");

        System.out.println("Tempos da fila 1: ");
        double tempos[] = fila1.getTimes();
        for (int i = 0; i < K1 + 1; i++) {
            System.out.println(i + ": " + tempos[i] + " (" + ((tempos[i]/tempoGlobal)*100) + "%)");
        }
        
        System.out.println("============================================");
        System.out.println("Fila2 (G/G/" + qtdServidores2 + "/" + K2 + ")");
        System.out.println("Saida: " + minService + " ... " + maxService);
        System.out.println("============================================");

        System.out.println("\nTempos da fila 2: ");
        double tempos2[] = fila2.getTimes();
        for (int i = 0; i < K2 + 1; i++) {
            System.out.println(i + ": " + tempos2[i] + " (" + ((tempos2[i]/tempoGlobal)*100) + "%)");
        }
        int perdaTotal = fila1.loss()+fila2.loss();
        System.out.println("Clientes perdidos: " + perdaTotal);
        System.out.println("(Perdidos fila1: " + fila1.loss() + ")");
        System.out.println("(Perdidos fila2: " + fila2.loss() + ")");
        System.out.println("Tempo total em simulação: " + tempoGlobal);
    }
}
