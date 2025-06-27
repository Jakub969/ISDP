package vizualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BusSchedulePanel extends JPanel {

    private final Map<String, List<Ride>> busLines = new LinkedHashMap<>();
    private final List<SegmentInfo> clickableSegments = new ArrayList<>();


    public BusSchedulePanel() {
        busLines.put("Autobus 1", Arrays.asList(
                new Ride("04:00", "04:14", "Zastavka 1", "Zastavka 2", true),
                new Ride("04:15", "04:30", "Zastavka 2", "Zastavka 3", false), // nebol zastavka, len prejazd
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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                for (SegmentInfo si : clickableSegments) {
                    if (si.area.contains(p)) {
                        JOptionPane.showMessageDialog(BusSchedulePanel.this, si.info, "Info", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int marginLeft = 100;
        int marginTop = 50;
        int lineSpacing = 80;
        int lineLength = 2300;

        long timeStart = toMillis("04:00");
        long timeEnd = toMillis("25:00");
        long timeRange = timeEnd - timeStart;

        int busIndex = 0;

        clickableSegments.clear();

        for (Map.Entry<String, List<Ride>> entry : busLines.entrySet()) {
            String busName = entry.getKey();
            List<Ride> rides = entry.getValue();

            int y = marginTop + busIndex * lineSpacing + 15;

            // Nakreslíme hlavnú čiaru pre linku
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(marginLeft, y, marginLeft + lineLength, y);
            g2.drawString(busName, 10, y + 5);

            for (int i = 0; i < rides.size() - 1; i++) {
                Ride current = rides.get(i);
                Ride next = rides.get(i + 1);

                long depTime = toMillis(current.end);
                long arrTime = toMillis(next.start);

                double depRatio = (double) (depTime - timeStart) / timeRange;
                double arrRatio = (double) (arrTime - timeStart) / timeRange;

                int xStart = marginLeft + (int) (depRatio * lineLength);
                int xEnd = marginLeft + (int) (arrRatio * lineLength);

                g2.setColor(Color.LIGHT_GRAY);
                g2.setStroke(new BasicStroke(4)); // hrubšia čiara prejazdu
                g2.drawLine(xStart, y, xEnd, y);

                long pauseMinutes = (arrTime - depTime) / (60 * 1000);
                String waitInfo = "Čakanie medzi jazdami: " + pauseMinutes + " min ("
                        + formatTime(depTime) + " – " + formatTime(arrTime) + ")";

                clickableSegments.add(new SegmentInfo(
                        new Rectangle(xStart, y - 2, xEnd - xStart, 4),
                        waitInfo
                ));

            }

            boolean drawUp = true;
            for (Ride ride : rides) {
                long arrival = toMillis(ride.start);
                long departure = toMillis(ride.end);

                double arrivalRatio = (double) (arrival - timeStart) / timeRange;
                double departureRatio = (double) (departure - timeStart) / timeRange;

                int xArrival = marginLeft + (int) (arrivalRatio * lineLength);
                int xDeparture = marginLeft + (int) (departureRatio * lineLength);

                if (ride.deadheadTrip) {
                    g2.setColor(Color.GREEN);
                } else {
                    g2.setColor(Color.YELLOW);
                }
                g2.fillRect(xArrival, y - 3, xDeparture - xArrival, 6);

                String rideInfo = "Autobus " + busName + ": " +
                        ride.startName + " → " + ride.endName +
                        " (" + ride.start + " – " + ride.end + ")" +
                        (ride.deadheadTrip ? " [BEŽNÁ JAZDA]" : " [PREJAZD]");

                clickableSegments.add(new SegmentInfo(
                        new Rectangle(xArrival, y - 3, xDeparture - xArrival, 6),
                        rideInfo
                ));




                // Bodky
                g2.setColor(Color.BLUE);
                g2.fillOval(xArrival - 4, y - 4, 8, 8);
                g2.setColor(Color.RED);
                g2.fillOval(xDeparture - 4, y - 4, 8, 8);

                // Popis zastávky
                g2.setColor(Color.DARK_GRAY);
                if (drawUp) {
                    g2.drawString(ride.startName, xArrival -10, y - 10);
                    drawUp = !drawUp;
                }

                else {
                    g2.drawString(ride.startName, xArrival -10, y + 20);
                    drawUp = !drawUp;
                }

                if (ride.equals(rides.get(rides.size() - 1))) {
                    if (drawUp) {
                        g2.drawString(ride.endName, xDeparture -10, y - 10);
                        drawUp = !drawUp;
                    }

                    else {
                        g2.drawString(ride.endName, xDeparture -10, y + 20);
                        drawUp = !drawUp;
                    }
                }
            }

            busIndex++;
        }


        // Časová os
        g2.setColor(Color.BLACK);
        int i = 0;
        while (true) {
            long t = timeStart + i * 60 * 1000;
            if (t > timeEnd) {
                break;
            }
            double ratio = (double) (t - timeStart) / timeRange;
            int x = marginLeft + (int) (ratio * lineLength);
            g2.drawString(formatTime(t), x - 15, marginTop - 15);
            i += 30;
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
        frame.setSize(1000, 600); // uprav veľkosť okna podľa potreby

        BusSchedulePanel panel = new BusSchedulePanel();
        panel.setPreferredSize(new Dimension(2500, 1000)); // nastav dostatočnú veľkosť pre scroll

        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

}
