import java.util.List;
import java.util.ArrayList;

public class Fila{
    private String id; // nome da fila
    private int server; // qtd de servidores da fila
    private int capacity; // capacidade da fila
    private double minArrival, maxArrival; // tempo minimo e máximo para chegada de clientes
    private double minService, maxService; // tempo mínimo e máximo para atendimento de clientes
    private int costumers; // qtd atual de clientes na fila
    private int loss; // contador para a quantidade de clientes perdidos
    private double times[]; // vetor dos tempos acumulados para cada estado da fila
    private List<Route> rotas;

    public Fila (String id, int server, int capacity, double minArrival, double maxArrival, double minService, double maxService) {
        this.id = id;
        this.server = server;
        this.capacity = capacity;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
        this.costumers = 0;
        this.loss = 0;
        this.times = new double[capacity+1];
        this.rotas = new ArrayList<Route>();
    }

    public void addRotas(Route rota) {
        rotas.add(rota);
    }

    public List<Route> getRotas() {
        return rotas;
    }

    public String getId() {
        return id;
    }

    public int status() {
        return costumers;
    }

    public int capacity() {
        return capacity;
    }

    public int servers() {
        return server;
    }

    public int loss() {
        return loss;
    }

    public void in() {
        costumers++;
    }

    public void out() {
        costumers--;
    }

    public void incLoss() {
        loss++;
    }

    public void incTempo(double tempo) {
        times[costumers] += tempo;
    }

    public double[] getTimes () {
        return times;
    }
}
