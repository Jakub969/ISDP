package mvp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class View {
    private Presenter presenter;
    public View()
    {
        this.presenter = new Presenter();
        this.presenter.nacitajData("dist.txt", "trips.txt");
        //vypisVsetkySpoje();
        //this.vypisVsetkyLinky();
        //vykonajMinimalizaciuAutobusov();
        vykonajMinimalizaciuPrazdnychPrejazdov();
        //vykonajMinimalizaciuVodicov();
        //vykonajMaximalizaciuObsadenosti();
        //vykonajMaximalizaciuObsluzenychSpojov();
    }

    public void vypisVsetkySpoje()
    {
        String[][] udajeSpoje = this.presenter.vypisVsetkySpoje();
        System.out.println("ID linky, ID spoja, Miesto odchodu, Čas odchodu, Miesto príchodu, Čas príchodu");
        for (String[] udajeSpoj : udajeSpoje) {
            for (String udaj : udajeSpoj) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
        }
    }

    public void vypisVsetkyLinky()
    {
        LinkedHashMap<Integer, String[][]> linkyUdaje = this.presenter.vypisVsetkyLinky();
        for (int linka_id: linkyUdaje.keySet())
        {
            String[][] udajeSpoje = linkyUdaje.get(linka_id);
            System.out.println("Linka " + linka_id);
            for (String[] udajeSpoj : udajeSpoje)
            {
                for (String udaj : udajeSpoj)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void vykonajMinimalizaciuAutobusov()
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        String pocetBusov = this.presenter.vykonajMinimalizaciuAutobusov(turnusyUdaje, spojeUdaje);

        System.out.println("Počet turnusov: " + pocetBusov);

        int counter = 1;
        System.out.println("Tur \t Zač \t Kon");
        for (String[] turnus: turnusyUdaje)
        {
            System.out.print(counter + "\t");
            for (String udaj : turnus) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
            counter++;
        }

        System.out.println();
        System.out.println("Lin \t spoj \t mod \t cod \t mpr \t cpr");
        counter = 1;
        for (String[][] turnusUdaje : spojeUdaje)
        {
            System.out.println("Turnus " + counter);
            for (String[] spojUdaje : turnusUdaje)
            {
                for (String udaj : spojUdaje)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
            counter++;
        }
    }

    public void vykonajMinimalizaciuPrazdnychPrejazdov()
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        String prazdnePrejazdy = this.presenter.vykonajMinimalizaciuPrazdnychPrejazdov(39, turnusyUdaje, spojeUdaje);

        System.out.println("Súčet prázdnych prejazdov: " + prazdnePrejazdy);

        int counter = 1;
        System.out.println("Tur \t Zač \t Kon");
        for (String[] turnus: turnusyUdaje)
        {
            System.out.print(counter + "\t");
            for (String udaj : turnus) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
            counter++;
        }

        System.out.println();
        System.out.println("Lin \t spoj \t mod \t cod \t mpr \t cpr");
        counter = 1;
        for (String[][] turnusUdaje : spojeUdaje)
        {
            System.out.println("Turnus " + counter);
            for (String[] spojUdaje : turnusUdaje)
            {
                for (String udaj : spojUdaje)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
            counter++;
        }
    }

    public void vykonajMinimalizaciuVodicov()
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        String prazdnePrejazdy = this.presenter.vykonajMinimalizaciuVodicov(4, turnusyUdaje, spojeUdaje);

        System.out.println("Počet vodičov: " + prazdnePrejazdy);

        int counter = 1;
        System.out.println("Tur \t Zač \t Kon");
        for (String[] turnus: turnusyUdaje)
        {
            System.out.print(counter + "\t");
            for (String udaj : turnus) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
            counter++;
        }

        System.out.println();
        System.out.println("Lin \t spoj \t mod \t cod \t mpr \t cpr");
        counter = 1;
        for (String[][] turnusUdaje : spojeUdaje)
        {
            System.out.println("Turnus " + counter);
            for (String[] spojUdaje : turnusUdaje)
            {
                for (String udaj : spojUdaje)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
            counter++;
        }
    }

    public void vykonajMaximalizaciuObsadenosti()
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        this.presenter.vykonajMaximalizaciuObsadenosti(2, 3, turnusyUdaje, spojeUdaje);

        int counter = 1;
        System.out.println("Tur \t Zač \t Kon");
        for (String[] turnus: turnusyUdaje)
        {
            System.out.print(counter + "\t");
            for (String udaj : turnus) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
            counter++;
        }

        System.out.println();
        System.out.println("Lin \t spoj \t mod \t cod \t mpr \t cpr");
        counter = 1;
        for (String[][] turnusUdaje : spojeUdaje)
        {
            System.out.println("Turnus " + counter);
            for (String[] spojUdaje : turnusUdaje)
            {
                for (String udaj : spojUdaje)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
            counter++;
        }
    }

    public void vykonajMaximalizaciuObsluzenychSpojov()
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        this.presenter.vykonajMaximalizaciuObsluzenychSpojov(0.6358024691358025, 2, 3, turnusyUdaje, spojeUdaje);

        int counter = 1;
        System.out.println("Tur \t Zač \t Kon");
        for (String[] turnus: turnusyUdaje)
        {
            System.out.print(counter + "\t");
            for (String udaj : turnus) {
                System.out.print(udaj + "\t");
            }
            System.out.println();
            counter++;
        }

        System.out.println();
        System.out.println("Lin \t spoj \t mod \t cod \t mpr \t cpr");
        counter = 1;
        for (String[][] turnusUdaje : spojeUdaje)
        {
            System.out.println("Turnus " + counter);
            for (String[] spojUdaje : turnusUdaje)
            {
                for (String udaj : spojUdaje)
                {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
            counter++;
        }
    }
}
