package mvp;

import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import optimalizacia.ModelMaxObsadenosti;
import optimalizacia.ModelMaxObsluzenychSpojov;
import optimalizacia.ModelMinBusov;
import optimalizacia.ModelMinVodicov;
import udaje.Turnus;
import java.util.List;
import udaje.*;
import subory.NacitavacUdajov;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Model {
    public static final int K = 10000;
    public static int DEPO;
    public static int REZERVA_1;
    public static int REZERVA_2;
    public static int C_VODIC;
    public static int C_KM;
    public static int DT_MAX;
    public static int T_MAX;
    public static int MIN_TRVANIE_BP;
    public static int MIN_TRVANIE_POJ;
    public static int SUCET_BP;
    public static int SUCET_POJ;
    public static int INTERVAL_BP;
    public static int INTERVAL_POJ;

    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky;
    private LinkedHashMap<Integer, Spoj> spoje;
    private LinkedHashMap<Integer, Linka> linky;
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT;
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> T;
    private List<Turnus> aktualneTurnusy;
    private String poslednyTypOptimalizacie;

    private GRBEnv env;

    public Model()
    {
        DEPO = 59;
        REZERVA_1 = 0;
        REZERVA_2 = 0;
        C_VODIC = 50;
        C_KM = 2;
        DT_MAX = 10 * 60;
        T_MAX = 13 * 60;
        MIN_TRVANIE_BP = 15;
        MIN_TRVANIE_POJ = 30;
        SUCET_BP = 30;
        SUCET_POJ = 30;
        INTERVAL_BP = 4 * 60 + 30;
        INTERVAL_POJ = 6 * 60 + 30;
    }

    //1. panel - Vstupné údaje
    public void ziskajKonstanty(String[] konstanty)
    {
        konstanty[0] = String.valueOf(DEPO);
        konstanty[1] = String.valueOf(REZERVA_1);
        konstanty[2] = String.valueOf(REZERVA_2);
        konstanty[3] = String.valueOf(C_VODIC);
        konstanty[4] = String.valueOf(C_KM);
        konstanty[5] = String.valueOf(DT_MAX);
        konstanty[6] = String.valueOf(T_MAX);
        konstanty[7] = String.valueOf(MIN_TRVANIE_BP);
        konstanty[8] = String.valueOf(MIN_TRVANIE_POJ);
        konstanty[9] = String.valueOf(SUCET_BP);
        konstanty[10] = String.valueOf(SUCET_POJ);
        konstanty[11] = String.valueOf(INTERVAL_BP);
        konstanty[12] = String.valueOf(INTERVAL_POJ);
    }
    public String nastavKonstanty(int depo, int r1, int r2, int cVodic, int cKm, int dtMax, int tMax,
                                  int trvanieBP, int trvaniePOJ, int sucetBP, int sucetPOJ, int intervalBP, int intervalPOJ)
    {
        DEPO = depo;
        REZERVA_1 = r1;
        REZERVA_2 = r2;
        C_VODIC = cVodic;
        C_KM = cKm;
        DT_MAX = dtMax;
        T_MAX = tMax;
        MIN_TRVANIE_BP = trvanieBP;
        MIN_TRVANIE_POJ = trvaniePOJ;
        SUCET_BP = sucetBP;
        SUCET_POJ = sucetPOJ;
        INTERVAL_BP = intervalBP;
        INTERVAL_POJ = intervalPOJ;
        if(jeProstrediePripravene())
            this.vypocitajOstatneUdaje();
        return "Zmeny boli uložené";
    }
    public String nacitajSpojeUseky(String pNazovSuboruUseky, String pNazovSuboruSpoje)
    {
        this.useky = new LinkedHashMap<>();
        this.spoje = new LinkedHashMap<>();
        this.linky = new LinkedHashMap<>();

        //načítanie úsekov, spojov a liniek
        NacitavacUdajov nacitavacUdajov = new NacitavacUdajov();
        try
        {
            nacitavacUdajov.nacitajUseky(pNazovSuboruUseky, this.useky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }

        try
        {
            nacitavacUdajov.nacitajSpoje(pNazovSuboruSpoje, this.spoje, this.linky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní spojov!";
        }

        this.vypocitajOstatneUdaje();
        return "Načítanie údajov bolo úspešné.";
    }

    public String nacitajUseky(File pSuborUseky)
    {
        this.useky = new LinkedHashMap<>();

        //načítanie úsekov
        NacitavacUdajov nacitavacUdajov = new NacitavacUdajov();
        try
        {
            nacitavacUdajov.nacitajUseky(pSuborUseky, this.useky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní úsekov!";
        }
        if(this.spoje != null)
            this.vypocitajOstatneUdaje();
        return "Načítanie úsekov bolo úspešné.";
    }

    public String nacitajSpoje(File pSuborSpoje)
    {
        this.spoje = new LinkedHashMap<>();
        this.linky = new LinkedHashMap<>();

        //načítanie úsekov, spojov a liniek
        NacitavacUdajov nacitavacUdajov = new NacitavacUdajov();

        try
        {
            nacitavacUdajov.nacitajSpoje(pSuborSpoje, this.spoje, this.linky);
        }
        catch (Exception e)
        {
            return "Chyba pri načítavaní spojov!";
        }

        if(this.useky != null)
            this.vypocitajOstatneUdaje();
        return "Načítanie spojov bolo úspešné.";
    }
    private void vypocitajOstatneUdaje()
    {
        try
        {
            this.env = new GRBEnv();
            this.env.start();
        }
        catch (GRBException ignored) {}

        this.DT = new LinkedHashMap<>();
        this.T = new LinkedHashMap<>();
        for (Spoj spoj_i : this.spoje.values())
        {
            spoj_i.pripravMnoziny();
        }

        for (Spoj spoj_i : this.spoje.values())
        {
            int miestoPrichodu_i = spoj_i.getMiestoPrichodu();
            int casPrichodu_i = spoj_i.getCasPrichoduVMinutach();

            for (Spoj spoj_j : this.spoje.values())
            {
                int miestoOdchodu_j = spoj_j.getMiestoOdchodu();
                int casOdchodu_j = spoj_j.getCasOdchoduVMinutach();

                int casPrejazdu = this.useky.get(new Dvojica<>(miestoPrichodu_i, miestoOdchodu_j));

                if (casPrichodu_i + casPrejazdu + REZERVA_1 <= casOdchodu_j)
                {
                    int casMedziSpojmi = spoj_j.getCasOdchoduVMinutach() - spoj_i.getCasPrichoduVMinutach();
                    int casJazdy = (casMedziSpojmi - casPrejazdu < 10) ? casMedziSpojmi : casPrejazdu;

                    this.DT.put(new Dvojica<>(spoj_i.getID(), spoj_j.getID()), casJazdy);
                    this.T.put(new Dvojica<>(spoj_i.getID(), spoj_j.getID()), casMedziSpojmi);

                    spoj_i.pridajMoznyNasledujuciSpoj(spoj_j);
                    spoj_j.pridajMoznyPredchadzajuciSpoj(spoj_i);
                }

                int casPrejazduDoDepa = this.useky.get(new Dvojica<>(miestoPrichodu_i, DEPO));
                int casPrejazduZDepa = this.useky.get(new Dvojica<>(DEPO, miestoOdchodu_j));

                if (casPrichodu_i + casPrejazduDoDepa + casPrejazduZDepa + REZERVA_2 <= casOdchodu_j)
                {
                    spoj_i.pridajMoznyNasledujuciSpojSVymenouVodica(spoj_j);
                    spoj_j.pridajMoznyPredchadzajuciSpojSVymenouVodica(spoj_i);
                }
            }
        }
    }

    //2. panel - Zobrazenie údajov
    public String[][] getUdajeUseky()
    {
        if(this.useky != null)
        {
            String[][] udajeUseky = new String[this.useky.size()][3];
            int count = 0;
            for (Dvojica<Integer, Integer> usek : this.useky.keySet())
            {
                udajeUseky[count][0] = String.valueOf(usek.prvyPrvok());
                udajeUseky[count][1] = String.valueOf(usek.druhyPrvok());
                udajeUseky[count][2] = String.valueOf(this.useky.get(usek));
                count++;
            }
            return udajeUseky;
        }
        return null;
    }

    public String[][] getUdajeSpoje()
    {
        if(this.spoje != null)
        {
            String[][] udajeSpoje = new String[this.spoje.size()][9];
            int count = 0;
            for (Spoj spoj: this.spoje.values())
            {
                udajeSpoje[count] = spoj.vypisInfo();
                count++;
            }
            return udajeSpoje;
        }
        return null;
    }

    public LinkedHashMap<Integer, Linka> getLinky()
    {
        return this.linky;
    }

    /**
     * Získa zoznam všetkých turnusov z poslednej optimalizácie
     * @return zoznam turnusov alebo null ak nebola vykonaná žiadna optimalizácia
     */
    public List<Turnus> getVsetkyTurnusy() {
        return this.aktualneTurnusy;
    }

    /**
     * Získa konkrétny turnus podľa indexu
     * @param index index turnusu v zozname
     * @return požadovaný turnus alebo null ak neexistuje
     */
    public Turnus getTurnus(int index) {
        if (aktualneTurnusy == null || index < 0 || index >= aktualneTurnusy.size()) {
            return null;
        }
        return aktualneTurnusy.get(index);
    }

    /**
     * Získa typ poslednej optimalizácie
     * @return reťazec popisujúci typ optimalizácie
     */
    public String getPoslednyTypOptimalizacie() {
        return poslednyTypOptimalizacie;
    }

    // 3. panel - Minimalizácia počtu autobusov
    public boolean jeProstrediePripravene()
    {
        return this.env != null;
    }

    public String vykonajMinimalizaciuAutobusov(ArrayList<String[]> pUdajeOturnusoch,
                                                ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje,
                                                ArrayList<String[]> pUdajeOiteraciach)
    {
        try
        {
            ModelMinBusov minBusov = new ModelMinBusov(this.spoje, this.useky, pUdajeOiteraciach);
            boolean vyrieseny = minBusov.vyriesModel(this.env, 0, -1);
            if(!vyrieseny)
                return "Model nemá riešenie!";

            this.aktualneTurnusy = minBusov.getTurnusy();
            this.poslednyTypOptimalizacie = "Minimalizácia počtu autobusov";
            for (Turnus turnus: aktualneTurnusy)
            {
                pUdajeOturnusoch.add(turnus.vypisUdajeOturnuse());
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypisZmenu(this.DT));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));
            }

            String info = minBusov.getInformacieOmodeli();
            minBusov.zrusModel();
            return info;
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }

    // 4. panel - Minimalizácia počtu vodičov
    public String vykonajMinimalizaciuVodicov(int pPocetBusov, double pGap, int pCasLimit, ArrayList<String[]> pUdajeOturnusoch,
                                                ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje,
                                                ArrayList<String[]> pUdajeOiteraciach)
    {
        try
        {
            ModelMinVodicov minVodicov = new ModelMinVodicov(pPocetBusov, this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
            boolean vyrieseny = minVodicov.vyriesModel(this.env, pGap, pCasLimit);
            if(!vyrieseny)
                return "Model nemá riešenie!";

            this.aktualneTurnusy = minVodicov.getTurnusy();
            this.poslednyTypOptimalizacie = "Minimalizácia počtu vodičov";
            for (Turnus turnus: aktualneTurnusy)
            {
                pUdajeOturnusoch.add(turnus.vypisUdajeOturnuse());
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypisZmenu(this.DT));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));
                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypisZmenu(this.DT));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }

            String info = minVodicov.getInformacieOmodeli();
            minVodicov.zrusModel();
            return info;
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }

    // 5. panel - Maximalizácia obsadenosti
    public String vykonajMaximalizaciuObsadenosti(int pPocetBusov, int pPocetVodicov, double pGap, int pCasLimit,
                                                  ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                  ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        try
        {
            ModelMaxObsadenosti maxObsadenosti = new ModelMaxObsadenosti(this.linky, pPocetBusov, pPocetVodicov,
                    this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
            boolean vyrieseny = maxObsadenosti.vyriesModel(this.env, pGap, pCasLimit);
            if(!vyrieseny)
                return "Model nemá riešenie!";

            this.aktualneTurnusy = maxObsadenosti.getTurnusy();
            this.poslednyTypOptimalizacie = "Maximalizácia obsadenosti";
            for (Turnus turnus: aktualneTurnusy)
            {
                pUdajeOturnusoch.add(turnus.vypisUdajeOturnuse());
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypisZmenu(this.DT));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));
                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypisZmenu(this.DT));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }

            String info = maxObsadenosti.getInformacieOmodeli();
            maxObsadenosti.zrusModel();
            return info;
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }

    // 6. panel - Maximalizácia obslúžených spojov
    public String vykonajMaximalizaciuObsluzenychSpojov(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                  ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                  ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        try
        {
            ModelMaxObsluzenychSpojov maxObsluzenychSpojov = new ModelMaxObsluzenychSpojov(pH, this.linky, pPocetBusov, pPocetVodicov,
                    this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
            boolean vyrieseny = maxObsluzenychSpojov.vyriesModel(this.env, pGap, pCasLimit);
            if(!vyrieseny)
                return "Model nemá riešenie!";

            this.aktualneTurnusy = maxObsluzenychSpojov.getTurnusy();
            this.poslednyTypOptimalizacie = "Maximalizácia obslúžených spojov";
            for (Turnus turnus: aktualneTurnusy)
            {
                pUdajeOturnusoch.add(turnus.vypisUdajeOturnuse());
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypisZmenu(this.DT));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));
                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypisZmenu(this.DT));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }

            String info = maxObsluzenychSpojov.getInformacieOmodeli();
            maxObsluzenychSpojov.zrusModel();
            return info;
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }

    // 7. panel - Minimalizácia obslúžených cestujúcich
    public String vykonajMinimalizaciuNeobsluzenychCestujucich(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                        ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                        ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        try
        {
            ModelMaxObsluzenychSpojov maxObsluzenychSpojov = new ModelMaxObsluzenychSpojov(pH, this.linky, pPocetBusov, pPocetVodicov,
                    this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
            boolean vyrieseny = maxObsluzenychSpojov.vyriesModel(this.env, pGap, pCasLimit);
            if(!vyrieseny)
                return "Model nemá riešenie!";

            this.aktualneTurnusy = maxObsluzenychSpojov.getTurnusy();
            this.poslednyTypOptimalizacie = "Minimalizácia neobslúžených cestujúcich";
            for (Turnus turnus: aktualneTurnusy)
            {
                pUdajeOturnusoch.add(turnus.vypisUdajeOturnuse());
                pTurnusyUdaje.add(turnus.getPrvaZmena().vypisZmenu(this.DT));
                pSpojeUdaje.add(turnus.getPrvaZmena().vypisSpoje(this.useky));
                if(turnus.getDruhaZmena() != null)
                {
                    pTurnusyUdaje.add(turnus.getDruhaZmena().vypisZmenu(this.DT));
                    pSpojeUdaje.add(turnus.getDruhaZmena().vypisSpoje(this.useky));
                }
            }

            String info = maxObsluzenychSpojov.getInformacieOmodeli();
            maxObsluzenychSpojov.zrusModel();
            return info;
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }

    // 8. panel - Experiment
    public String vykonajExperiment(ArrayList<String[]> informacieObehu, int pPocetBusov, int pPocetVodicov, int pCasLimit, double pGap)
    {
        ArrayList<String[]> pUdajeOiteraciach = new ArrayList<>();
        try
        {
            for (int i = pPocetBusov; i >= 1; i--)
            {
                for (int j = Math.min(pPocetVodicov, i * 2); j >= i; j--) {
                    String[] info = new String[8];
                    info[0] = String.valueOf(i);
                    info[1] = String.valueOf(j);

                    ModelMaxObsadenosti maxObsadenosti = new ModelMaxObsadenosti(this.linky, i, j,
                            this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
                    maxObsadenosti.vyriesModel(this.env, pGap, pCasLimit);
                    double h_max = maxObsadenosti.getVysledok();
                    info[2] = String.valueOf(String.format("%.4f", h_max));
                    info[3] = String.valueOf(String.format("%.4f", maxObsadenosti.getGap()));
                    info[4] = String.valueOf(String.format("%.2f", maxObsadenosti.getCas()));
                    maxObsadenosti.zrusModel();

                    ModelMaxObsluzenychSpojov maxObsluzenychSpojov = new ModelMaxObsluzenychSpojov(h_max, this.linky, i, j,
                            this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
                    boolean vyrieseny = maxObsluzenychSpojov.vyriesModel(this.env, pGap, pCasLimit);
                    while (!vyrieseny) {
                        h_max = h_max - 0.1;
                        maxObsluzenychSpojov = new ModelMaxObsluzenychSpojov(h_max, this.linky, i, j,
                                this.spoje, this.useky, this.DT, this.T, pUdajeOiteraciach);
                        vyrieseny = maxObsluzenychSpojov.vyriesModel(this.env, pGap, pCasLimit);
                    }
                    info[5] = String.valueOf(maxObsluzenychSpojov.getPocetObsluzenychSpojov());
                    info[6] = String.valueOf(String.format("%.4f", maxObsluzenychSpojov.getGap()));
                    info[7] = String.valueOf(String.format("%.2f", maxObsluzenychSpojov.getCas()));
                    maxObsluzenychSpojov.zrusModel();

                    informacieObehu.add(info);

                }
            }
            return "Hotovo";
        }
        catch (Exception e)
        {
            return "Chyba pri riešení modelu!";
        }
    }
}
