import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class YmlReader {

    public static class ConfigFila {
        public String id;
        public int servers = -1;
        public int capacity = -1;
        public double minArrival = -1;
        public double maxArrival = -1;
        public double minService = -1;
        public double maxService = -1;

        @Override
        public String toString() {
            return String.format(
                "%s [servers=%d, capacity=%d, arrival=%.1f-%.1f, service=%.1f-%.1f]",
                id, servers, capacity, minArrival, maxArrival, minService, maxService
            );
        }
    }

    public Map<String, Double> arrivals = new LinkedHashMap<>();
    public Map<String, ConfigFila> queues = new LinkedHashMap<>();
    public List<Route> network = new ArrayList<>();
    public int rndnumbersPerSeed = 0;
    public List<Integer> seeds = new ArrayList<>();

    private enum Block {
        NONE,
        ARRIVALS,
        QUEUES,
        NETWORK,
        RNDNUMBERS,
        SEEDS
    }

    public YmlReader read(File f) throws FileNotFoundException {
        YmlReader data = new YmlReader();
        Block b = Block.NONE;
        ConfigFila currentQ = null;
        Route currentRoute = null;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                int hash = line.indexOf('#'); // finds comment start
                if (hash >= 0) line = line.substring(0, hash);
                String t = line.trim(); // removes comment part

                if (t.isEmpty()) continue;
                if (t.startsWith("!")) continue; // what is the !PARAMETERS for? srsly

                if (t.equals("arrivals:")) {
                    // primeiras chegadas em cada fila
                    b = Block.ARRIVALS;
                }
                else if (t.equals("queues:")) {
                    // declaração de filas
                    b = Block.QUEUES;
                    currentQ = null;
                }
                else if (t.equals("network:")) {
                    // filas de origem, destino e probabilidades
                    b = Block.NETWORK;
                    currentRoute = null;
                }
                else if (t.equals("rndnumbers:")) {
                    // números aleatórios (double, entre 0 - 1)
                    b = Block.RNDNUMBERS;
                }
                else if (t.startsWith("rndnumbersPerSeed:")) {
                    // quantidade de números pseudo-aleatórios a serem gerados (requer seeds)
                    b = Block.NONE;
                    data.rndnumbersPerSeed = Integer.parseInt(value(t)); // directly associate value to var
                }
                else if (t.equals("seeds:")) {
                    // seeds a serem utilizadas na geração de números pseudo-aleatórios
                    b = Block.SEEDS;
                }

                else {
                    switch (b) {
                        case ARRIVALS:
                            data.arrivals.put(identifier(t), Double.parseDouble(value(t)));
                            break;

                        case QUEUES:
                            if (t.endsWith(":")) {
                                currentQ = new ConfigFila();
                                currentQ.id = t.substring(0, t.length() - 1).trim();
                                data.queues.put(currentQ.id, currentQ);
                            }
                            else if (currentQ != null) {
                                String k = identifier(t), v = value(t);
                                switch (k) {
                                    case "servers":    currentQ.servers = Integer.parseInt(v); break;
                                    case "capacity":   currentQ.capacity = Integer.parseInt(v); break;
                                    case "minArrival": currentQ.minArrival = Double.parseDouble(v); break;
                                    case "maxArrival": currentQ.maxArrival = Double.parseDouble(v); break;
                                    case "minService": currentQ.minService = Double.parseDouble(v); break;
                                    case "maxService": currentQ.maxService = Double.parseDouble(v); break;
                                    default: System.err.println("Unknown variable in queues: " + k);
                                }
                            }
                            break;

                        case NETWORK:
                            if (t.startsWith("-")) {
                                currentRoute = new Route();
                                data.network.add(currentRoute);
                                t = t.substring(1).trim();
                            }
                            if (currentRoute != null) {
                                String k = identifier(t), v = value(t);
                                switch (k) {
                                    case "source":      currentRoute.source = v; break;
                                    case "target":      currentRoute.target = v; break;
                                    case "probability": currentRoute.prob = Double.parseDouble(v); break;
                                    default: System.err.println("Unknown variable in network: " + k);
                                }
                            }
                            break;

                        case SEEDS:
                            if (t.startsWith("-"))
                                data.seeds.add(Integer.parseInt(t.substring(1).trim()));
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        return data;
    }

    private static String identifier(String s) {
        int i = s.indexOf(':');
        return (i < 0 ? s : s.substring(0, i)).trim();
    }

    private static String value(String s) {
        int i = s.indexOf(':');
        return i < 0 ? "" : s.substring(i + 1).trim();
    }

    public void main(String[] args) {
        if (args.length == 0) {
            throw new InvalidParameterException("Please specify the file name.");
        }
        String filename = args[0];
        File f = new File(filename);

        try {
            YmlReader d = read(f);

            System.out.println("== arrivals ==");
            d.arrivals.forEach((q, t) -> System.out.println(q + ": " + t));

            System.out.println("== queues ==");
            d.queues.values().forEach(System.out::println);

            System.out.println("== network ==");
            d.network.forEach(System.out::println);

            System.out.println("rndnumbersPerSeed: " + d.rndnumbersPerSeed);
            System.out.println("seeds: " + d.seeds);
        }
        catch (FileNotFoundException e) {
            System.out.printf("File \"%s\" was not found.\n", filename);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid number in file: " + e.getMessage());
        }
    }
}