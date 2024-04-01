package mvp;

import data.Linka;

import java.util.LinkedHashMap;

public class View {
    private Presenter presenter;
    public View()
    {
        this.presenter = new Presenter();
        this.presenter.nacitajData("dist.txt", "trips.txt");
        this.vypisVsetkyLinky();
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
            for (String[] udajeSpoj : udajeSpoje) {
                for (String udaj : udajeSpoj) {
                    System.out.print(udaj + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
