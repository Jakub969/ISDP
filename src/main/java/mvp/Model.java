package mvp;

import data.*;

import files.LoaderOfData;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Model {
    private LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky;
    private LinkedHashMap<Integer, Spoj> spoje;
    private LinkedHashMap<Integer, Zastavka> zastavky;
    private LinkedHashMap<Integer, Linka> linky;
    public Model()
    {
        this.useky = new LinkedHashMap<>();
        this.spoje = new LinkedHashMap<>();
        this.zastavky = new LinkedHashMap<>();
        this.linky = new LinkedHashMap<>();
        //String dataSpoj[][]
    }

    public String nacitajData(String pSuborUseky, String pSuborSpoje)
    {
        LoaderOfData loader = new LoaderOfData();
        try
        {
            loader.nacitajUseky(pSuborUseky, this.useky, this.zastavky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }

        try
        {
            loader.nacitajSpoje(pSuborSpoje, this.spoje, this.linky, this.zastavky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní spojov!";
        }

        for (int id_i : this.spoje.keySet())
        {
            Spoj spoj_i = this.spoje.get(id_i);
            int miestoPrichodu_i = spoj_i.getMiestoPrichoduID();
            int casPrichodu_i = spoj_i.getCasPrichodu();
            for (int id_j : this.spoje.keySet())
            {
                Spoj spoj_j = this.spoje.get(id_j);
                int miestoOdchodu_j = spoj_j.getMiestoOdchoduID();
                int casOdchodu_j = spoj_j.getCasOdchodu();
                int dist = useky.get(new Dvojica<>(miestoPrichodu_i, miestoOdchodu_j)).getCasPrejazdu();

                if (casPrichodu_i + dist <= casOdchodu_j)
                {
                    spoj_i.pridajSpojKtoryMozeNasledovat(spoj_j);
                }
            }
        }
        return "Načítanie údajov bolo úspešné.";
    }

    public String[][] vypisVsetkySpoje()
    {
        String[][] udajeSpoje = new String[this.spoje.size()][6];
        int counter = 0;
        for (int id: this.spoje.keySet())
        {
            Spoj spoj_i = this.spoje.get(id);
            udajeSpoje[counter] = spoj_i.vypis();
            counter++;
        }
        return udajeSpoje;
    }

    public LinkedHashMap<Integer, String[][]> vypisVsetkyLinky()
    {
        LinkedHashMap<Integer, String[][]> linkyUdaje = new LinkedHashMap<>();
        for (int linka_id: this.linky.keySet())
        {
            Linka linka_i = this.linky.get(linka_id);
            linkyUdaje.put(linka_id, linka_i.vypis());
        }

        return linkyUdaje;
    }
}
