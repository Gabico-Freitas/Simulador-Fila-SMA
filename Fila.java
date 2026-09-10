public class Fila{
    private int server; // qtd de servidores da fila
    private int capacity; // capacidade da fila
    private double minArrival, maxArrival; // tempo minimo e máximo para chegada de clientes
    private double minService, maxService; // tempo mínimo e máximo para atendimento de clientes
    private int costumers; // qtd atual de clientes na fila
    private int loss; // contador para a quantidade de clientes perdidos
    private double times[]; // vetor dos tempos acumulados para cada estado da fila

    public Fila (int server, int capacity, 
                double minArrival, double maxArrival,
                double minService, double maxService) {

        this.server = server;
        this.capacity = capacity;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
        this.costumers = 0;
        this.loss = 0;
        this.times = new double[capacity+1];
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
