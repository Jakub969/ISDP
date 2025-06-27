package vizualizer;

public class Ride {
    String start;
    String end;
    String startName;
    String endName;
    boolean deadheadTrip; // true = zastavil, false = prešiel

    public Ride(String start, String end, String startName, String endName, boolean deadheadTrip) {
        this.start = start;
        this.end = end;
        this.startName = startName;
        this.endName = endName;
        this.deadheadTrip = deadheadTrip;
    }
}
