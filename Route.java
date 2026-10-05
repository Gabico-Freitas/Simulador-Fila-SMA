public class Route {
    public String source;
    public String target;
    public double prob;

    public Route(String source, String target, double prob) {
        this.source = source;
        this.target = target;
        this.prob = prob;
    }

    public Route() {
        
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.2f)", source, target, prob);
    }
}