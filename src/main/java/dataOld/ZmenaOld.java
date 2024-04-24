package dataOld;

import mvpOld.ModelOld;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ZmenaOld
{
    private final SpojOld prvySpoj;
    private final SpojOld poslednySpoj;
    public ZmenaOld(SpojOld pPrvySpoj)
    {
        this.prvySpoj = pPrvySpoj;

        SpojOld spoj = this.prvySpoj;
        while(spoj.getNasledujuci() != null)
        {
            spoj = spoj.getNasledujuci();
        }
        this.poslednySpoj = spoj;
    }

    public SpojOld getPoslednySpoj()
    {
        return this.poslednySpoj;
    }

    public String[] vypis(LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> pUseky)
    {
        int pristavenie = pUseky.get(new DvojicaOld<>(ModelOld.DEPO, this.prvySpoj.getMiestoOdchoduID())).getCasPrejazdu();
        int odstavenie = pUseky.get(new DvojicaOld<>(this.poslednySpoj.getMiestoPrichoduID(), ModelOld.DEPO)).getCasPrejazdu();

        int prazdnePrejazdy = 0;
        SpojOld spoj = this.prvySpoj;
        while(spoj.getNasledujuci() != null)
        {
            int mpr_i = spoj.getMiestoPrichoduID();
            int mod_j = spoj.getNasledujuci().getMiestoOdchoduID();
            prazdnePrejazdy += pUseky.get(new DvojicaOld<>(mpr_i, mod_j)).getCasPrejazdu();
            spoj = spoj.getNasledujuci();
        }

        String[] udajeZmena = new String[5];
        int casZaciatku = this.prvySpoj.getCasOdchoduMinuty() - pristavenie;
        int casKonca = this.poslednySpoj.getCasPrichoduMinuty() + odstavenie;
        udajeZmena[0] = LocalTime.ofSecondOfDay(casZaciatku * 60L).toString().substring(0, 5);
        udajeZmena[1] = LocalTime.ofSecondOfDay(casKonca * 60L).toString().substring(0, 5);
        udajeZmena[2] = String.valueOf(pristavenie);
        udajeZmena[3] = String.valueOf(odstavenie);
        udajeZmena[4] = String.valueOf(prazdnePrejazdy);
        return udajeZmena;
    }

    public String[][] vypisSpoje(LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> useky)
    {
        ArrayList<String[]> udajeZmena = new ArrayList<>();

        SpojOld spoj = this.prvySpoj;
        while(spoj != null)
        {
            udajeZmena.add(spoj.vypis(useky));
            spoj = spoj.getNasledujuci();
        }

        return udajeZmena.toArray(new String[udajeZmena.size()][]);
    }

    public ArrayList<ArrayList<Integer>> porusujeBP(int tur, LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> useky,
                               LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> DT,
                               LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> T)
    {
        //vytvorenie všetkých nepretržitých jázd
        ArrayList<DvojicaOld<Integer, Integer>> jazdy = new ArrayList<>();
        int z = this.prvySpoj.getCasOdchoduMinuty() - useky.get(new DvojicaOld<>(ModelOld.DEPO, this.prvySpoj.getMiestoOdchoduID())).getCasPrejazdu();
        SpojOld spoj = this.prvySpoj;
        int k;
        while (spoj != poslednySpoj)
        {
            DvojicaOld<Integer, Integer> prechod = new DvojicaOld<>(spoj.getID(), spoj.getNasledujuci().getID());
            k = spoj.getCasPrichoduMinuty() + DT.get(prechod);
            int p = T.get(prechod) - DT.get(prechod);
            if(p >= 10)
            {
                jazdy.add(new DvojicaOld<>(z, k));
                z = spoj.getNasledujuci().getCasOdchoduMinuty();
            }
            spoj = spoj.getNasledujuci();
        }

        k = this.poslednySpoj.getCasPrichoduMinuty() + useky.get(new DvojicaOld<>(this.poslednySpoj.getMiestoPrichoduID(), ModelOld.DEPO)).getCasPrejazdu();
        jazdy.add(new DvojicaOld<>(z, k));

        ArrayList<ArrayList<Integer>> porusenia = new ArrayList<>();
        for (DvojicaOld<Integer, Integer> jazda: jazdy)
        {
            int zac = jazda.prva();
            int kon = zac + 270;
            ArrayList<Integer> spoje = spocitajBPPrestavku(tur, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            kon = zac + 390;
            spoje = spocitajJOPrestavku(tur, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            kon = jazda.druha();
            zac = kon - 270;
            spoje = spocitajBPPrestavku(tur, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            zac = kon - 390;
            spoje = spocitajJOPrestavku(tur, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);
        }
        return porusenia;
    }

    private ArrayList<Integer> spocitajBPPrestavku(int tur, ArrayList<DvojicaOld<Integer, Integer>> jazdy,
                                  int zacIntervalu, int konIntervalu)
    {
/*        int zac = jazdy.get(0).prvyPrvok();
        int kon = jazdy.get(jazdy.size() - 1).druhyPrvok();
        if(zacIntervalu < zac || konIntervalu > kon)
            return null;*/

        int zac = jazdy.get(0).prva();
        int kon = jazdy.get(jazdy.size() - 1).druha();
        int skutocneTrvanieIntervalu = Math.min(kon, konIntervalu) - Math.max(zac, zacIntervalu);

        int bp = 0;
        for (int i = 0; i < jazdy.size(); i++)
        {
            DvojicaOld<Integer, Integer> jazda = jazdy.get(i);
            int zacJazdy = jazda.prva();
            if(i > 0)
            {
                DvojicaOld<Integer, Integer> predchadzajucaJazda = jazdy.get(i - 1);
                int konPredchJazdy = predchadzajucaJazda.druha();
                if(!(zacJazdy < zacIntervalu || konPredchJazdy > konIntervalu)){

                    int prestavka = Math.min(konIntervalu, zacJazdy) - Math.max(zacIntervalu, konPredchJazdy);

                    if(prestavka >= 15)
                        bp += prestavka;
                }
            }
        }

        if(skutocneTrvanieIntervalu - bp > 240 && bp < 30)
        {
            ArrayList<Integer> spojeID = new ArrayList<>();
            System.out.println("Porusenie BP v turnuse " + tur + " v case od " +
                    LocalTime.ofSecondOfDay(zacIntervalu * 60L).toString().substring(0, 5) +
                    " do " + LocalTime.ofSecondOfDay((konIntervalu * 60L) % 86400).toString().substring(0, 5) +
                    ", súčet prestávok = " + bp + "!");
            SpojOld spoj = this.prvySpoj;
            while(spoj != null)
            {
                if(!(spoj.getCasOdchoduMinuty() >= konIntervalu
                        || spoj.getCasPrichoduMinuty() <= zacIntervalu))
                {
                    spojeID.add(spoj.getID());
                }
                spoj = spoj.getNasledujuci();
            }
            return spojeID;
        }
        return null;
    }

    private ArrayList<Integer> spocitajJOPrestavku(int tur, ArrayList<DvojicaOld<Integer, Integer>> jazdy,
                                    int zacIntervalu, int konIntervalu)
    {
        int zac = jazdy.get(0).prva();
        int kon = jazdy.get(jazdy.size() - 1).druha();
        int skutocneTrvanieIntervalu = Math.min(kon, konIntervalu) - Math.max(zac, zacIntervalu);

        int jo = 0;
        for (int i = 0; i < jazdy.size(); i++)
        {
            DvojicaOld<Integer, Integer> jazda = jazdy.get(i);
            int zacJazdy = jazda.prva();
            if(i > 0)
            {
                DvojicaOld<Integer, Integer> predchadzajucaJazda = jazdy.get(i - 1);
                int konPredchJazdy = predchadzajucaJazda.druha();
                if(!(zacJazdy < zacIntervalu || konPredchJazdy > konIntervalu)){

                    int prestavka = Math.min(konIntervalu, zacJazdy) - Math.max(zacIntervalu, konPredchJazdy);

                    if(prestavka >= 30)
                        jo += prestavka;
                }
            }
        }

        if(skutocneTrvanieIntervalu - jo > 360 && jo < 30)
        {
            ArrayList<Integer> spojeID = new ArrayList<>();
            System.out.println("Porusenie JO v turnuse " + tur + " v case od " +
                    LocalTime.ofSecondOfDay(zacIntervalu * 60L).toString().substring(0, 5) +
                    " do " + LocalTime.ofSecondOfDay((konIntervalu * 60L) % 86400).toString().substring(0, 5) +
                    ", súčet prestávok = " + jo + "!");
            SpojOld spoj = this.prvySpoj;
            while(spoj != null)
            {
                if(!(spoj.getCasOdchoduMinuty() >= konIntervalu
                        || spoj.getCasPrichoduMinuty() <= zacIntervalu))
                {
                    spojeID.add(spoj.getID());
                }
                spoj = spoj.getNasledujuci();
            }
            return spojeID;
        }
        return null;
    }
}
