package mvp;

import data.*;

import files.LoaderOfData;
import optimalizacia.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Model {
    public static final int REZERVA_1 = 0;
    public static final int REZERVA_2 = 0;
    public static final int DEPO = 59;
    public static final int C_VODIC = 50;
    public static final int C_KM = 2;
    public static final int K = 10000;

    public static final int DT_MAX = 10 * 60;
    public static final int T_MAX = 13 * 60;
    private LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky;
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT;
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> T;
    private LinkedHashMap<Integer, Spoj> spoje;
    private LinkedHashMap<Integer, Zastavka> zastavky;
    private LinkedHashMap<Integer, Linka> linky;

    public Model()
    {
        this.useky = new LinkedHashMap<>();
        this.spoje = new LinkedHashMap<>();
        this.DT = new LinkedHashMap<>();
        this.T = new LinkedHashMap<>();
        this.zastavky = new LinkedHashMap<>();
        this.linky = new LinkedHashMap<>();
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

        for (Spoj spoj_i : this.spoje.values())
        {
            int miestoPrichodu_i = spoj_i.getMiestoPrichoduID();
            int casPrichodu_i = spoj_i.getCasPrichoduMinuty();
            for (Spoj spoj_j : this.spoje.values())
            {
                int miestoOdchodu_j = spoj_j.getMiestoOdchoduID();
                int casOdchodu_j = spoj_j.getCasOdchoduMinuty();
                int casPrejazdu = this.useky.get(new Dvojica<>(miestoPrichodu_i, miestoOdchodu_j)).getCasPrejazdu();

                if (casPrichodu_i + casPrejazdu + REZERVA_1 <= casOdchodu_j)
                {
                    //System.out.println(spoj_i.getID() + " : " + spoj_j.getID());
                    int casMedziSpojmi = spoj_j.getCasOdchoduMinuty() - spoj_i.getCasPrichoduMinuty();
                    int casJazdy = (casMedziSpojmi - casPrejazdu < 10) ? casMedziSpojmi : casPrejazdu;
                    this.DT.put(new Dvojica<>(spoj_i.getID(), spoj_j.getID()), casJazdy);
                    this.T.put(new Dvojica<>(spoj_i.getID(), spoj_j.getID()), casMedziSpojmi);

                    //System.out.println("cas prejazdu: " + casPrejazdu);
                    //System.out.println("DT: " + casJazdy);
                    //System.out.println("T:" + casMedziSpojmi);
                    //System.out.println();
                    spoj_i.pridajNaslednostSpoja(spoj_j);
                }

                int dist1 = this.useky.get(new Dvojica<>(miestoPrichodu_i, DEPO)).getCasPrejazdu();
                int dist2 = this.useky.get(new Dvojica<>(DEPO, miestoOdchodu_j)).getCasPrejazdu();
                if (casPrichodu_i + dist1 + dist2 + REZERVA_2 <= casOdchodu_j)
                {
                    spoj_i.pridajNaslednostSpojaVodic(spoj_j);
                }
            }
        }
        return "Načítanie údajov bolo úspešné.";
    }

    //TODO prerobiť - bez vypisov o časoch prejazdov
    public String[][] vypisVsetkySpoje()
    {
        String[][] udajeSpoje = new String[this.spoje.size()][10];
        int counter = 0;
        for (Spoj spoj_i: this.spoje.values())
        {
            udajeSpoje[counter] = spoj_i.vypis(this.useky);
            counter++;
        }
        return udajeSpoje;
    }

    //TODO prerobiť - bez vypisov o časoch prejazdov
    public LinkedHashMap<Integer, String[][]> vypisVsetkyLinky()
    {
        LinkedHashMap<Integer, String[][]> linkyUdaje = new LinkedHashMap<>();
        for (Linka linka_i: this.linky.values())
        {
            linkyUdaje.put(linka_i.getID(), linka_i.vypis(this.useky));
        }

        return linkyUdaje;
    }

    public String vykonajMinimalizaciuAutobusov(ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        try
        {
            MinimalizaciaAutobusov minBusov = new MinimalizaciaAutobusov(this.spoje);
            int pocetBusov = minBusov.getPocetAutobusov();
            ArrayList<Turnus> turnusy = minBusov.vytvorTurnusy(this.spoje);
            for (Turnus turnus: turnusy)
            {
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypis(this.useky));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));

                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypis(this.useky));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }
            return String.valueOf(pocetBusov);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
    }

    public String vykonajMinimalizaciuPrazdnychPrejazdov(int pPocetBusov,
                                                         ArrayList<String[]> pTurnusyUdaje,
                                                         ArrayList<String[][]> pSpojeUdaje)
    {
        try
        {
            MinimalizaciaPrejazdov minBusov = new MinimalizaciaPrejazdov(pPocetBusov, this.spoje, this.useky);
            int prazdnePrejazdy = minBusov.getPrazdnePrejazdy();
            ArrayList<Turnus> turnusy = minBusov.vytvorTurnusy(this.spoje, this.useky, this.DT, this.T);
            for (Turnus turnus: turnusy)
            {
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypis(this.useky));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));

                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypis(this.useky));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }
            return String.valueOf(prazdnePrejazdy);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
    }

    public String vykonajMinimalizaciuVodicov(int pPocetBusov,
                                              ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        try
        {
            MinimalizaciaVodicov minVodicov = new MinimalizaciaVodicov(pPocetBusov, this.spoje, this.useky, this.DT, this.T);
            int pocetVodicov = minVodicov.getPocetVodicov();
            ArrayList<Turnus> turnusy = minVodicov.vytvorTurnusy(this.spoje);
            for (Turnus turnus: turnusy)
            {
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypis(this.useky));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));

                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypis(this.useky));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }
            return String.valueOf(pocetVodicov);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
    }

    public String vykonajMaximalizaciuObsadenosti(int pPocetBusov,
                                                  int pPocetVodicov,
                                                  ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        try
        {
            MaximalizaciaObsadenosti maxObs = new MaximalizaciaObsadenosti(pPocetBusov, pPocetVodicov,this.linky, this.spoje, this.useky, this.DT, this.T);
            ArrayList<Turnus> turnusy = maxObs.vytvorTurnusy(this.spoje);

            for (Turnus turnus: turnusy)
            {
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypis(this.useky));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));

                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypis(this.useky));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }
            return "";
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
    }

    public String vykonajMaximalizaciuObsluzenychSpojov(double y, int pPocetBusov, int pPocetVodicov,
                                                        ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        try
        {
            MaximalizaciaObsluzenychSpojov maxObs = new MaximalizaciaObsluzenychSpojov(y, pPocetBusov, pPocetVodicov,
                    this.linky, this.spoje, this.useky, this.DT, this.T);
            ArrayList<Turnus> turnusy = maxObs.vytvorTurnusy(this.spoje);
            for (Turnus turnus: turnusy)
            {
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypis(this.useky));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));

                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypis(this.useky));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }
            return "";
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
    }
}
