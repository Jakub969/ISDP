package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Linka
{
    private final int ID;
    private ArrayList<Spoj> spoje;

    public Linka(int pID)
    {
        this.ID = pID;
        this.spoje = new ArrayList<>();
    }

    public void pridajSpoj(Spoj pSpoj)
    {
        this.spoje.add(pSpoj);
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
}
