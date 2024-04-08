package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
    public String[][] vypis(LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky)
    {
        String[][] udajeSpoje = new String[this.spoje.size()][9];
        int counter = 0;
        for (Spoj spoj_i: this.spoje)
        {
            udajeSpoje[counter] = spoj_i.vypis(useky);
            counter++;
        }
        return udajeSpoje;
    }

    public int getID() {
        return ID;
    }

    public ArrayList<Spoj> getSpoje()
    {
        return this.spoje;
    }

    public int getObsadenost() {
        return obsadenost;
    }
}
