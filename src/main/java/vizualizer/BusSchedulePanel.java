package vizualizer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BusSchedulePanel extends JPanel {

    private final Map<String, List<Ride>> busLines = new LinkedHashMap<>();

    public BusSchedulePanel() {
        busLines.put("Autobus 1", Arrays.asList(
                new Ride("04:00", "04:05", "Zastavka 1", "Zastavka 2", true),
                new Ride("04:25", "04:30", "Zastavka 2", "Zastavka 3", false), // nebol zastavka, len prejazd
                new Ride("04:50", "04:55", "Zastavka 3", "Zastavka 4", true)
        ));

        busLines.put("Autobus 2", Arrays.asList(
                new Ride("14:00", "14:05", "Zastavka 1", "Zastavka 2", true),
                new Ride("14:25", "14:30", "Zastavka 2", "Zastavka 3", false), // nebol zastavka, len prejazd
                new Ride("14:50", "14:55", "Zastavka 3", "Zastavka 4", true)
        ));

        busLines.put("Autobus 3", Arrays.asList(
                new Ride("04:00", "04:05", "Zastavka 1", "Zastavka 2", true),
                new Ride("04:25", "04:30", "Zastavka 2", "Zastavka 3", false), // nebol zastavka, len prejazd
                new Ride("04:50", "04:55", "Zastavka 3", "Zastavka 4", true)
        ));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int marginLeft = 100;
        int marginTop = 50;
        int lineSpacing = 80;
        int lineLength = 1700;

        long timeStart = toMillis("04:00");
        long timeEnd = toMillis("15:30");
        long timeRange = timeEnd - timeStart;

        int busIndex = 0;

        for (Map.Entry<String, List<Ride>> entry : busLines.entrySet()) {
            String busName = entry.getKey();
            List<Ride> stops = entry.getValue();

            int y = marginTop + busIndex * lineSpacing + 15;

            // Nakreslíme hlavnú čiaru pre linku
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(marginLeft, y, marginLeft + lineLength, y);
            g2.drawString(busName, 10, y + 5);

            for (Ride stop : stops) {
                long arrival = toMillis(stop.start);
                long departure = toMillis(stop.end);

                double arrivalRatio = (double) (arrival - timeStart) / timeRange;
                double departureRatio = (double) (departure - timeStart) / timeRange;

                int xArrival = marginLeft + (int) (arrivalRatio * lineLength);
                int xDeparture = marginLeft + (int) (departureRatio * lineLength);

                // Šedá čiara medzi príchodom a odchodom
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(xArrival, y - 3, xDeparture - xArrival, 6);

                // Bodky
                g2.setColor(Color.BLUE);
                g2.fillOval(xArrival - 4, y - 4, 8, 8);
                g2.setColor(Color.RED);
                g2.fillOval(xDeparture - 4, y - 4, 8, 8);

                // Popis zastávky
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(stop.startName + " (" + stop.start + "–" + stop.end + ")", xDeparture + 5, y - 10);
            }

            for (int i = 0; i < stops.size() - 1; i++) {
                Ride current = stops.get(i);
                Ride next = stops.get(i + 1);

                long depTime = toMillis(current.end);
                long arrTime = toMillis(next.start);

                double depRatio = (double) (depTime - timeStart) / timeRange;
                double arrRatio = (double) (arrTime - timeStart) / timeRange;

                int xStart = marginLeft + (int) (depRatio * lineLength);
                int xEnd = marginLeft + (int) (arrRatio * lineLength);

                // Farba podľa toho, či zastavil na ďalšej zastávke
                if (next.deadheadTrip) {
                    g2.setColor(Color.GREEN);
                } else {
                    g2.setColor(Color.YELLOW);
                }

                g2.setStroke(new BasicStroke(4)); // hrubšia čiara prejazdu
                g2.drawLine(xStart, y, xEnd, y);
            }

            busIndex++;
        }


        // Časová os
        g2.setColor(Color.BLACK);
        for (int i = 0; i <= 1000; i += 15) {
            long t = timeStart + i * 60 * 1000;
            double ratio = (double) (t - timeStart) / timeRange;
            int x = marginLeft + (int) (ratio * lineLength);
            //g2.drawLine(x, marginTop - 10, x, marginTop + busLines.size() * lineSpacing);
            g2.drawString(formatTime(t), x - 15, marginTop - 15);
        }
    }

    private long toMillis(String timeStr) {
        try {
            return new SimpleDateFormat("HH:mm").parse(timeStr).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatTime(long millis) {
        return new SimpleDateFormat("HH:mm").format(new Date(millis));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Vizualizácia trás autobusov");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 400);
        frame.add(new BusSchedulePanel());
        frame.setVisible(true);
    }
}
