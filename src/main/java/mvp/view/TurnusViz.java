package mvp.view;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;
import static guru.nidi.graphviz.model.Factory.*;

import udaje.Ride;
import udaje.Turnus;
import udaje.Zmena;
import udaje.Spoj;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TurnusViz {
    public static void renderTurnus(Turnus turnus, String cesta) throws Exception {
        Graph g = graph("Turnus_" + turnus.getID()).directed()
                .graphAttr().with(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
                .nodeAttr().with(Shape.RECTANGLE);

        g = g.with(vykresliZmenu(turnus.getPrvaZmena(), "blue"));

        if (turnus.getDruhaZmena() != null) {
            g = g.with(vykresliZmenu(turnus.getDruhaZmena(), "green"));
        }

        Graphviz.fromGraph(g)
                .width(1200)
                .render(Format.PNG)
                .toFile(new File(cesta + "/turnus_" + turnus.getID() + ".png"));
    }

    public static ArrayList<Ride> renderTurnus(Turnus turnus) {
        return vykresliZmenuPreGUI(turnus.getPrvaZmena());
    }

    private static ArrayList<Ride> vykresliZmenuPreGUI(Zmena zmena) {
        ArrayList<Ride> rides = new ArrayList<>();


        Spoj spoj = zmena.getPrvySpoj();
        Ride ride;
        while (spoj != null) {
            String label = "Spoj " + spoj.getID() + "\n" + cas(spoj.getCasOdchoduVMinutach()) + " → " + cas(spoj.getCasPrichoduVMinutach());

            ride = new Ride(
                    spoj.getCasOdchodu().format(DateTimeFormatter.ofPattern("HH:mm")),
                    spoj.getCasPrichodu().format(DateTimeFormatter.ofPattern("HH:mm")),
                    Integer.toString(spoj.getMiestoOdchodu()),
                    Integer.toString(spoj.getMiestoPrichodu()),
                    spoj.getObsluzenost());
            rides.add(ride);
            spoj = spoj.getNasledujuciSpoj();
        }
        return rides;
    }

    private static LinkSource vykresliZmenu(Zmena zmena, String color) {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Link> links = new ArrayList<>();

        Spoj spoj = zmena.getPrvySpoj();
        while (spoj != null) {
            String label = "Spoj " + spoj.getID() + "\n" + cas(spoj.getCasOdchoduVMinutach()) + " → " + cas(spoj.getCasPrichoduVMinutach());
            Node node = node(label).with(Color.named(color));
            nodes.add(node);
            spoj = spoj.getNasledujuciSpoj();
        }

        for (int i = 0; i < nodes.size() - 1; i++) {
            links.add(nodes.get(i).link(to(nodes.get(i + 1)).with(Color.named(color), Style.SOLID)).linkTo());
        }

        // Spojíme všetky linky ako jeden komponent
        Node result = nodes.get(0);
        for (Link link : links) {
            result = result.link(link);
        }
        return result;
    }

    private static String cas(int minuty) {
        return String.format("%02d:%02d", minuty / 60, minuty % 60);
    }
}

