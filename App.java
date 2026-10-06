import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class App {

    // ******************************************
    // PARA RODAR:
    //      Compilar: javac *.java
    //      Executar: java App -filename <arquivo.yml>
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

        double somador = 0;
        for (int i = 0; i < listaFilas.size(); i++) {
            for (int j = 0; j < listaFilas.get(i).getRotas().size(); j++) {
                somador += listaFilas.get(i).getRotas().get(j).prob;
            }
            if (1.0 - somador != 0) {
                listaFilas.get(i).addRotas(new Route(listaFilas.get(i).getId(), "", 1.0 - somador));
            }
            somador = 0;
        }

        Queue<Integer> sementes = new LinkedList<>();
        for (int seeds : yml.seeds) {
            sementes.add(seeds);
        }

        int seed = 0;
        if (!sementes.isEmpty()) seed = sementes.poll();
        Aleatorio rnd = new Aleatorio(1103, 12345, 429496, seed);


        // Tempo total da simulação
        double tempoGlobal = 0;
        // Criação do escalonador, nele fica guardado os próximos eventos, ele decide qual será o próximo pelo tempo mais recente
        Escalonador esc = new Escalonador();
        // Variável auxiliar para guardar o tempo que se passou do último evento para inserir na lista de tempos
        double tempoAux = 0;

        // Esses valores precisam ser alterador para o teste

        // Insere os eventos iniciais para o início da execução da fila
        for (String chave : yml.arrivals.keySet()) {
            esc.add(new Evento(yml.arrivals.get(chave), Tipo.CHEGADA, -1, buscaIndexQueues(listaFilas, chave)));
        }

        long limite = yml.rndnumbersPerSeed;
        rnd.setLimite(limite);

        while (rnd.hasNext()) {
            Evento evento = esc.getProxEvento(); //Verifica o escalonador para pegar o próximo evento

            // Insere o tempo que se passou entre o evento anterior ao evento a ocorrer no momento
            // na leitura de dados de cada espaço de fila e o tempo global

            tempoAux = evento.tEntrada - tempoGlobal;
            tempoGlobal = evento.tEntrada;

            for (Fila fila : listaFilas) {
                fila.incTempo(tempoAux);
            }

            Fila filaOrigem = null;
            Fila filaDestino = null;

            if (evento.filaOrigem >= 0) {
                filaOrigem = listaFilas.get(evento.filaOrigem);
            }
            if (evento.filaDestino >= 0) {
                filaDestino = listaFilas.get(evento.filaDestino);
            }

            double sum = 0;

            switch (evento.tipo) {
                case Tipo.CHEGADA:
                    if (filaDestino.status() < filaDestino.capacity()) {
                        filaDestino.in();
                        if (filaDestino.status() <= filaDestino.servers()) {
                            double prob = rnd.aleatorio(0, 1);
                            for (Route rota : filaDestino.getRotas()) {
                                sum += rota.prob;
                                if (prob < sum) {
                                    if (rota.target.isEmpty()) {
                                        // Caso não tenha rota de passagem, marca uma saida
                                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaDestino.minService(), filaDestino.maxService()), Tipo.SAIDA, listaFilas.indexOf(filaDestino), -1));
                                    } else {
                                        // Marca uma passagem
                                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaDestino.minService(), filaDestino.maxService()), Tipo.PASSAGEM, listaFilas.indexOf(filaDestino), buscaIndexQueues(listaFilas, rota.target)));
                                    }
                                    break;
                                }                            
                            }
                        }
                    } else {
                        filaDestino.incLoss();
                    }
                    esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaDestino.minArrival(),filaDestino.maxArrival()), Tipo.CHEGADA, -1, listaFilas.indexOf(filaDestino)));
                    break;
                case Tipo.PASSAGEM:
                    filaOrigem.out();
                    if (filaOrigem.status() >= filaOrigem.servers()) {
                        double prob = rnd.aleatorio(0, 1);
                        for (Route rota : filaOrigem.getRotas()) {
                            sum += rota.prob;
                            if (prob < sum) {
                                if (rota.target.isEmpty()) {
                                    // Caso não tenha rota de passagem, marca uma saida
                                    esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaOrigem.minService(), filaOrigem.maxService()), Tipo.SAIDA, listaFilas.indexOf(filaOrigem), -1));
                                } else {
                                    // Marca uma passagem
                                    esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaOrigem.minService(), filaOrigem.maxService()), Tipo.PASSAGEM, listaFilas.indexOf(filaOrigem), buscaIndexQueues(listaFilas, rota.target)));
                                }
                                break;
                            }
                        }
                    }
                    if (filaDestino.status() < filaDestino.capacity()) {
                        filaDestino.in();
                        if (filaDestino.status() <= filaDestino.servers()) {
                            double prob = rnd.aleatorio(0, 1);
                            for (Route rota : filaDestino.getRotas()) {
                                sum += rota.prob;
                                if (prob < sum) {
                                    if (rota.target.isEmpty()) {
                                        // Caso não tenha rota de passagem, marca uma saida
                                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaDestino.minService(), filaDestino.maxService()), Tipo.SAIDA, listaFilas.indexOf(filaDestino), -1));
                                    } else {
                                        // Marca uma passagem
                                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaDestino.minService(), filaDestino.maxService()), Tipo.PASSAGEM, listaFilas.indexOf(filaDestino), buscaIndexQueues(listaFilas, rota.target)));
                                    }
                                    break;
                                }                            
                            }
                        }
                    } else {
                        filaDestino.incLoss();
                    }
                    break;
                case Tipo.SAIDA:
                    filaOrigem.out();
                    if (filaOrigem.status() >= filaOrigem.servers()) {
                        esc.add(new Evento(tempoGlobal + rnd.aleatorio(filaOrigem.minService(), filaOrigem.maxService()), Tipo.SAIDA, listaFilas.indexOf(filaOrigem), -1));
                    }
                    break;
            
                default:
                    break;
            }
            if (!rnd.hasNext() && !sementes.isEmpty()) {
                    rnd.setSeed(sementes.poll());
            }
        }

        for (Fila f : listaFilas) {
            f.setTempoGlobal(tempoGlobal);
            System.out.println(f);
        }
        System.out.println("Tempo total em simulação: " + tempoGlobal);

        System.out.println(rnd.sorteios);
    }

    public static int buscaIndexQueues(ArrayList<Fila> listaFilas, String chave) {
        for (int i = 0; i < listaFilas.size(); i++) {
            if (listaFilas.get(i).getId().equals(chave)) {
                return i;
            }
        }
        return -1;
    }
}
