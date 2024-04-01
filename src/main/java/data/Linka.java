package data;

import java.util.ArrayList;

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
    public String[][] vypis()
    {
        String[][] udajeSpoje = new String[this.spoje.size()][6];
        int counter = 0;
        for (Spoj spoj_i: this.spoje)
        {
            udajeSpoje[counter] = spoj_i.vypis();
            counter++;
        }
        return udajeSpoje;
    }
}
