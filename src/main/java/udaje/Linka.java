package udaje;

import java.util.ArrayList;

public class Linka
{
    private final int ID;
    private int obsadenost;
    private ArrayList<Spoj> spoje;

    public Linka(int pID)
    {
        this.ID = pID;
        this.spoje = new ArrayList<>();
        this.obsadenost = 0;
    }

    public void pridajSpoj(Spoj pSpoj)
    {
        this.spoje.add(pSpoj);
        this.obsadenost += pSpoj.getObsadenost();
    }

    public int getID() {
        return ID;
    }

    public ArrayList<Spoj> getSpoje()
    {
        return this.spoje;
    }

    public double getRealnaObsadenost()
    {
        int realna = 0;
        for (Spoj spoj : spoje)
        {
            if(spoj.getObsluzenost())
                realna += spoj.getObsadenost();
        }
        return (double) realna / this.obsadenost;
    }


    public int getObsadenost() {
        return this.obsadenost;
    }

    public String[][] vypisSpoje()
    {
        String[][] udajeSpoje = new String[this.spoje.size()][9];
        int counter = 0;
        for (Spoj spoj_i: this.spoje)
        {
            udajeSpoje[counter] = spoj_i.vypisInfo();
            counter++;
        }
        return udajeSpoje;
    }

    public int getPocetObsluzenychSpojov()
    {
        int pocet = 0;
        for (Spoj spoj : spoje)
        {
            if(spoj.getObsluzenost())
                pocet++;
        }
        return pocet;
    }
}
