public class Route {
    public String source;
    public String target;
    public double prob;

    @Override
    public String toString() {
        return String.format("%s -> %s (%.2f)", source, target, prob);
    }
}