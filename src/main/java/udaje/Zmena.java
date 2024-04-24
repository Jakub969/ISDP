package udaje;

import mvp.Model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Zmena {
    private final int IDturnusu;
    private final int IDzmeny;
    private final Spoj prvySpoj;
    private final Spoj poslednySpoj;
    private final int pristavenie;
    private final int odstavenie;
    private int prazdnePrejazdy;
    public Zmena(int pIdTurnusu, int pIdZmeny, Spoj pPrvySpoj, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky)
    {
        this.IDturnusu = pIdTurnusu;
        this.IDzmeny = pIdZmeny;
        this.prvySpoj = pPrvySpoj;
        this.pristavenie = pUseky.get(new Dvojica<>(Model.DEPO, this.prvySpoj.getMiestoOdchodu()));

        Spoj spoj = this.prvySpoj;
        this.prazdnePrejazdy = 0;
        while(spoj.getNasledujuciSpoj() != null)
        {
            int mpr_i = spoj.getMiestoPrichodu();
            int mod_j = spoj.getNasledujuciSpoj().getMiestoOdchodu();
            this.prazdnePrejazdy += pUseky.get(new Dvojica<>(mpr_i, mod_j));
            spoj = spoj.getNasledujuciSpoj();
        }

        this.poslednySpoj = spoj;
        this.odstavenie = pUseky.get(new Dvojica<>(this.poslednySpoj.getMiestoPrichodu(), Model.DEPO));
    }

    public int getTrvanieZmeny()
    {
        int zaciatokZmeny = this.prvySpoj.getCasOdchoduVMinutach() - this.pristavenie;
        int koniecZmeny = this.poslednySpoj.getCasPrichoduVMinutach() + this.odstavenie;
        return koniecZmeny - zaciatokZmeny;
    }

    public int getTrvanieJazdy(LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT)
    {
        int trvanieJazdy = this.pristavenie + this.prvySpoj.getTrvanieSpoja();
        Spoj spoj = this.prvySpoj;
        while(spoj != this.poslednySpoj)
        {
            trvanieJazdy += DT.get(new Dvojica<>(spoj.getID(), spoj.getNasledujuciSpoj().getID()));
            trvanieJazdy += spoj.getNasledujuciSpoj().getTrvanieSpoja();
            spoj = spoj.getNasledujuciSpoj();
        }
        trvanieJazdy += this.odstavenie;
        return trvanieJazdy;
    }
    public Spoj getPoslednySpoj()
    {
        return this.poslednySpoj;
    }

    public String[] vypisZmenu(LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT)
    {
        String[] udajeZmena = new String[9];
        int casZaciatku = this.prvySpoj.getCasOdchoduVMinutach() - this.pristavenie;
        int casKonca = this.poslednySpoj.getCasPrichoduVMinutach() + this.odstavenie;
        udajeZmena[0] = String.valueOf(this.IDturnusu);
        udajeZmena[1] = String.valueOf(this.IDzmeny);
        udajeZmena[2] = LocalTime.ofSecondOfDay(casZaciatku * 60L).toString().substring(0, 5);
        udajeZmena[3] = LocalTime.ofSecondOfDay(casKonca * 60L).toString().substring(0, 5);
        udajeZmena[4] = String.valueOf(this.pristavenie);
        udajeZmena[5] = String.valueOf(this.odstavenie);
        udajeZmena[6] = String.valueOf(this.prazdnePrejazdy);

        int trvanieZmeny = this.getTrvanieZmeny();
        if(trvanieZmeny % 60 < 10)
            udajeZmena[7] = trvanieZmeny / 60 + ":0" + trvanieZmeny % 60;
        else
            udajeZmena[7] = trvanieZmeny / 60 + ":" + trvanieZmeny % 60;

        int trvanieJazdy = this.getTrvanieJazdy(DT);
        if(trvanieJazdy % 60 < 10)
            udajeZmena[8] = trvanieJazdy / 60 + ":0" + trvanieJazdy % 60;
        else
            udajeZmena[8] = trvanieJazdy / 60 + ":" + trvanieJazdy % 60;

        return udajeZmena;
    }

    public String[][] vypisSpoje(LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky)
    {
        ArrayList<String[]> udajeZmena = new ArrayList<>();

        Spoj spoj = this.prvySpoj;
        while(spoj != null)
        {
            udajeZmena.add(spoj.vypisTurnus(pUseky));
            spoj = spoj.getNasledujuciSpoj();
        }

        return udajeZmena.toArray(new String[udajeZmena.size()][]);
    }

    public ArrayList<ArrayList<Integer>> ziskajPoruseniaPrestavok(LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky,
                                                                  LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT,
                                                                  LinkedHashMap<Dvojica<Integer, Integer>, Integer> T)
    {
         //vytvorenie všetkých nepretržitých jázd
        ArrayList<Dvojica<Integer, Integer>> jazdy = new ArrayList<>();
        int z = this.prvySpoj.getCasOdchoduVMinutach() - useky.get(new Dvojica<>(Model.DEPO, this.prvySpoj.getMiestoOdchodu()));
        int k;
        Spoj spoj = this.prvySpoj;
        while (spoj != this.poslednySpoj)
        {
            Dvojica<Integer, Integer> prechod = new Dvojica<>(spoj.getID(), spoj.getNasledujuciSpoj().getID());
            k = spoj.getCasPrichoduVMinutach() + DT.get(prechod);
            int p = T.get(prechod) - DT.get(prechod);
            if(p >= 10)
            {
                jazdy.add(new Dvojica<>(z, k));
                z = spoj.getNasledujuciSpoj().getCasOdchoduVMinutach();
            }
            spoj = spoj.getNasledujuciSpoj();
        }

        k = this.poslednySpoj.getCasPrichoduVMinutach() + useky.get(new Dvojica<>(this.poslednySpoj.getMiestoPrichodu(), Model.DEPO));
        jazdy.add(new Dvojica<>(z, k));

        //získanie všetkých porušení prestávok
        ArrayList<ArrayList<Integer>> porusenia = new ArrayList<>();
        for (Dvojica<Integer, Integer> jazda: jazdy)
        {
            int zac = jazda.prvyPrvok();
            int kon = zac + Model.INTERVAL_BP;
            ArrayList<Integer> spoje = skontrolujCasovyInterval(Model.MIN_TRVANIE_BP, Model.SUCET_BP, Model.INTERVAL_BP, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            kon = zac + Model.INTERVAL_POJ;
            spoje = skontrolujCasovyInterval(Model.MIN_TRVANIE_POJ, Model.SUCET_POJ, Model.INTERVAL_POJ, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            kon = jazda.druhyPrvok();
            zac = kon - Model.INTERVAL_BP;
            spoje = skontrolujCasovyInterval(Model.MIN_TRVANIE_BP, Model.SUCET_BP, Model.INTERVAL_BP, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);

            zac = kon - Model.INTERVAL_POJ;
            spoje = skontrolujCasovyInterval(Model.MIN_TRVANIE_POJ, Model.SUCET_POJ, Model.INTERVAL_POJ, jazdy, zac, kon);
            if(spoje != null && !porusenia.contains(spoje))
                porusenia.add(spoje);
        }
        return porusenia;

    }

    private ArrayList<Integer> skontrolujCasovyInterval(int min_trvanie, int sucet, int interval,
                                                        ArrayList<Dvojica<Integer, Integer>> jazdy,
                                                        int zacIntervalu, int konIntervalu)
    {
        int zac = jazdy.get(0).prvyPrvok();
        int kon = jazdy.get(jazdy.size() - 1).druhyPrvok();
        int skutocneTrvanieIntervalu = Math.min(kon, konIntervalu) - Math.max(zac, zacIntervalu);

        int sucetPrestavok = 0;
        for (int i = 0; i < jazdy.size(); i++)
        {
            Dvojica<Integer, Integer> jazda = jazdy.get(i);
            int zacJazdy = jazda.prvyPrvok();
            if(i > 0)
            {
                Dvojica<Integer, Integer> predchadzajucaJazda = jazdy.get(i - 1);
                int konPredchJazdy = predchadzajucaJazda.druhyPrvok();
                if(!(zacJazdy < zacIntervalu || konPredchJazdy > konIntervalu))
                {
                    int prestavka = Math.min(konIntervalu, zacJazdy) - Math.max(zacIntervalu, konPredchJazdy);
                    if(prestavka >= min_trvanie)
                        sucetPrestavok += prestavka;
                }
            }
        }
        if(skutocneTrvanieIntervalu - sucetPrestavok > (interval - sucet) && sucetPrestavok < sucet)
        {
            ArrayList<Integer> spojeID = new ArrayList<>();
            System.out.println("Porusenie prestavky v turnuse " + this.IDturnusu + " v case od " +
                    LocalTime.ofSecondOfDay(zacIntervalu * 60L).toString().substring(0, 5) +
                    " do " + LocalTime.ofSecondOfDay((konIntervalu * 60L) % 86400).toString().substring(0, 5) +
                    ", súčet prestávok = " + sucetPrestavok + "!");
            Spoj spoj = this.prvySpoj;
            while(spoj != null)
            {
                if(!(spoj.getCasOdchoduVMinutach() >= konIntervalu
                        || spoj.getCasPrichoduVMinutach() <= zacIntervalu))
                {
                    spojeID.add(spoj.getID());
                }
                spoj = spoj.getNasledujuciSpoj();
            }
            return spojeID;
        }
        return null;
    }
}