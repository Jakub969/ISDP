package dataOld;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LinkaOld
{
    private final int ID;
    private int obsadenost;
    private ArrayList<SpojOld> spoje;

    public LinkaOld(int pID)
    {
        this.ID = pID;
        this.spoje = new ArrayList<>();
        this.obsadenost = 0;
    }

    public void pridajSpoj(SpojOld pSpoj)
    {
        this.spoje.add(pSpoj);
        this.obsadenost += pSpoj.getObsadenost();
    }
    public String[][] vypis(LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> useky)
    {
        String[][] udajeSpoje = new String[this.spoje.size()][9];
        int counter = 0;
        for (SpojOld spoj_i: this.spoje)
        {
            udajeSpoje[counter] = spoj_i.vypis(useky);
            counter++;
        }
        return udajeSpoje;
    }

    public int getID() {
        return ID;
    }

    public ArrayList<SpojOld> getSpoje()
    {
        return this.spoje;
    }

    public int getObsadenost() {
        return obsadenost;
    }
}
