package data;

import mvp.Model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Turnus {
    private final Spoj prvySpoj;
    private final Spoj poslednySpoj;
    public Turnus(Spoj pPrvySpoj)
    {
        this.prvySpoj = pPrvySpoj;

        Spoj spoj = this.prvySpoj;
        while(spoj.getNasledujuci() != null)
        {
            spoj = spoj.getNasledujuci();
        }
        this.poslednySpoj = spoj;
    }

    public Spoj getPrvySpoj()
    {
        return prvySpoj;
    }

    public String[] vypis(LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky)
    {
        int pristavenie = pUseky.get(new Dvojica<>(Model.DEPO, this.prvySpoj.getMiestoOdchodu().getID())).getCasPrejazdu();
        int odstavenie = pUseky.get(new Dvojica<>(this.poslednySpoj.getMiestoPrichodu().getID(), Model.DEPO)).getCasPrejazdu();

        int prazdnePrejazdy = 0;
        Spoj spoj = this.prvySpoj;
        while(spoj.getNasledujuci() != null)
        {
            int mpr_i = spoj.getMiestoPrichoduID();
            int mod_j = spoj.getNasledujuci().getMiestoOdchoduID();
            prazdnePrejazdy += pUseky.get(new Dvojica<>(mpr_i, mod_j)).getCasPrejazdu();
            spoj = spoj.getNasledujuci();
        }

        String[] udajeTurnus = new String[5];
        int casZaciatku = this.prvySpoj.getCasOdchoduMinuty() - pristavenie;
        int casKonca = this.poslednySpoj.getCasPrichoduMinuty() + odstavenie;
        udajeTurnus[0] = LocalTime.ofSecondOfDay(casZaciatku * 60L).toString().substring(0, 5);
        udajeTurnus[1] = LocalTime.ofSecondOfDay(casKonca * 60L).toString().substring(0, 5);
        udajeTurnus[2] = String.valueOf(pristavenie);
        udajeTurnus[3] = String.valueOf(odstavenie);
        udajeTurnus[4] = String.valueOf(prazdnePrejazdy);
        return udajeTurnus;
    }

    public String[][] vypisSpoje(LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky)
    {
        ArrayList<String[]> udajeTurnus = new ArrayList<>();

        //pristavna jazda

        //ostatne spoje
        Spoj spoj = this.prvySpoj;
        while(spoj != null)
        {
            udajeTurnus.add(spoj.vypis(useky));
            spoj = spoj.getNasledujuci();
        }

        //odstavna jazda
        return udajeTurnus.toArray(new String[udajeTurnus.size()][]);
    }
}
