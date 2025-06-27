package udaje;

public class Ride {
    String start;
    String end;
    String startName;
    String endName;
    boolean deadheadTrip;

    public Ride(String start, String end, String startName, String endName, boolean deadheadTrip) {
        this.start = start;
        this.end = end;
        this.startName = startName;
        this.endName = endName;
        this.deadheadTrip = deadheadTrip;
    }
}
