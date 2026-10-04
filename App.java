import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class App {

    // ******************************************
    // PARA RODAR:
    //      Compilar: javac *.java
    //      Executar: java App <qtdServidores1> <K1> <qtdServidores2> <K2>
    // ******************************************

    public static void main(String[] args) {
        YmlReader yml = new YmlReader();
        ArrayList<Fila> listaFilas = new ArrayList<>();

        if (args[0].startsWith("-filename")) {
            // Ler arquivo .yml
            String filename = args[1];
            File f = new File(filename);
            try  {
                yml = yml.read(f);

                // System.out.println(yml.toString());
            }
            catch (FileNotFoundException e) {
            System.out.printf("File \"%s\" was not found.\n", filename);
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid number in file: " + e.getMessage());
            }
        }

        ArrayList<String> chavesFila = new ArrayList<>();
        yml.queues.forEach((chave, valor) -> {
            chavesFila.add(chave);
        });

        for (String chave : chavesFila) {
            YmlReader.ConfigFila aux = yml.queues.get(chave);
            listaFilas.add(new Fila(
                chave,
                aux.servers,
                aux.capacity,
                aux.minArrival,
                aux.maxArrival,
                aux.minService,
                aux.maxService
            ));
        }

        for (Route rota : yml.network) {
            int aux = buscaIndexQueues(listaFilas, rota.source);
            listaFilas.get(aux).addRotas(rota);
        }

        // Tempo total da simulação
        double tempoGlobal = 0;
        // Criação do escalonador, nele fica guardado os próximos eventos, ele decide qual será o próximo pelo tempo mais recente
        Escalonador esc = new Escalonador();
        // Variável auxiliar para guardar o tempo que se passou do último evento para inserir na lista de tempos
        double tempoAux = 0;

        // Esses valores precisam ser alterador para o teste
        Aleatorio rnd = new Aleatorio(1103, 12345, 429496, 157987);

        // Insere os eventos iniciais para o início da execução da fila
        for (String chave : yml.arrivals.keySet()) {
            esc.add(new Evento(yml.arrivals.get(chave), Tipo.CHEGADA, -1, buscaIndexQueues(listaFilas, chave)));
        }

        //Criação da fila1 (será a que os clientes chegarão primeiro)
        Fila fila1 = new Fila("",0, 0, 0, 0,0,0);

        // Criação da fila2 (irá ser a atendida pelos servidores após passar pela fila1)
        Fila fila2 = new Fila("",0, 0,0,0,0,0);

        int count = 100000;
        while (count > 0) {
            Evento evento = esc.getProxEvento(); //Verifica o escalonador para pegar o próximo evento

            // Insere o tempo que se passou entre o evento anterior ao evento a ocorrer no momento
            // na leitura de dados de cada espaço de fila e o tempo global

            tempoAux = evento.tEntrada - tempoGlobal;
            tempoGlobal = evento.tEntrada;

            for (Fila fila : listaFilas) {
                fila.incTempo(tempoAux);
            }

            Fila filaOrigem = listaFilas.get(evento.filaOrigem);
            String chaveOrigem = filaOrigem.getId();
            Fila filaDestino = listaFilas.get(evento.filaDestino);
            String chaveDestino = filaDestino.getId();
            
            double minPass=0, maxPass=0, minArrival=0, maxArrival=0, minService=0, maxService = 0;


            switch (evento.tipo) {
                case Tipo.CHEGADA:
                    if (filaDestino.status() < filaDestino.capacity()) {
                        filaDestino.in();
                        if (filaDestino.status() <= filaDestino.servers()) {
                            if (rnd.aleatorio(0, 1) < filaDestino.getRotas()) {

                            }
                            esc.add(new Evento(tempoGlobal + rnd.aleatorio(minPass, maxPass), Tipo.PASSAGEM, listaFilas.indexOf(filaOrigem), 0));
                        }
                    } else {
                        fila1.incLoss();
                    }
                    esc.add(new Evento(tempoGlobal + rnd.aleatorio(minArrival,maxArrival), Tipo.CHEGADA,0,0));
                    break;
                case Tipo.PASSAGEM:
                    fila1.out();
                    if (fila1.status() >= fila1.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(minPass, maxPass), Tipo.PASSAGEM,0,0));
                    }
                    if (fila2.status() < fila2.capacity()) {
                        fila2.in();
                        if (fila2.status() <= fila2.servers()) {
                            esc.add(new Evento(tempoGlobal + rnd.aleatorio(minService, maxService), Tipo.SAIDA,0,0));
                        }
                    } else {
                        fila2.incLoss();
                    }
                    break;
                case Tipo.SAIDA:
                    fila2.out();
                    if (fila2.status() >= fila2.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(minService, maxService), Tipo.SAIDA,0,0));
                    }
                    break;
            
                default:
                    break;
            }
            count--;
        }

        System.out.println("============================================");
        System.out.println("Fila1 (G/G/" + 0 + "/" + 0 + ")");
        System.out.println("Chegada: " + 0 + " ... " + 0);
        System.out.println("Passagem: " + 0 + " ... " + 0);
        System.out.println("============================================");

        System.out.println("Tempos da fila 1: ");
        double tempos[] = fila1.getTimes();
        for (int i = 0; i < 0 + 1; i++) {
            System.out.println(i + ": " + tempos[i] + " (" + ((tempos[i]/tempoGlobal)*100) + "%)");
        }
        
        System.out.println("\n============================================");
        System.out.println("Fila2 (G/G/" + 0 + "/" + 0 + ")");
        System.out.println("Saida: " + 0 + " ... " + 0);
        System.out.println("============================================");

        System.out.println("Tempos da fila 2: ");
        double tempos2[] = fila2.getTimes();
        for (int i = 0; i < 0 + 1; i++) {
            System.out.println(i + ": " + tempos2[i] + " (" + ((tempos2[i]/tempoGlobal)*100) + "%)");
        }
        int perdaTotal = fila1.loss()+fila2.loss();
        System.out.println("Clientes perdidos: " + perdaTotal);
        System.out.println("(Perdidos fila1: " + fila1.loss() + ")");
        System.out.println("(Perdidos fila2: " + fila2.loss() + ")");
        System.out.println("Tempo total em simulação: " + tempoGlobal);
    }

    public static int buscaIndexQueues(ArrayList<Fila> listaFilas, String chave) {
        for (int i = 0; i < listaFilas.size(); i++) {
            if (listaFilas.get(i).getId().equals(chave)) {
                return i;
            }
        }
        return -1;
    }

    public static Route buscaIndexNetwork(List<Route> network, String chave) {
        for (int i = 0; i < network.size(); i++) {
            if (network.get(i).source.equals(chave)) {
                return network.get(i);
            }
        }
        return null;
    }

}
