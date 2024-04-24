package mvp;

import udaje.Linka;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Presenter {
    private final Model model;
    public Presenter() {
        this.model = new Model();
    }

    //1. panel - Vstupné údaje
    public void ziskajKonstanty(String[] konstanty)
    {
        this.model.ziskajKonstanty(konstanty);
    };

    public String nastavKonstanty(int depo, int r1, int r2, int c_vodic, int c_km, int dt_max, int t_max,
                                  int trvanieBP, int trvaniePOJ, int sucetBP, int sucetPOJ, int intervalBP, int intervalPOJ)
    {
        return this.model.nastavKonstanty(depo, r1 ,r2, c_vodic, c_km, dt_max, t_max, trvanieBP, trvaniePOJ, sucetBP, sucetPOJ, intervalBP, intervalPOJ);
    }

    public String nacitajSpojeUseky(String pNazovSuboruUseky, String pNazovSuboruSpoje)
    {
        return this.model.nacitajSpojeUseky(pNazovSuboruUseky, pNazovSuboruSpoje);
    }

    public String nacitajUseky(File pSuborUseky)
    {
        return this.model.nacitajUseky(pSuborUseky);
    }

    public String nacitajSpoje(File pSuborSpoje)
    {
        return this.model.nacitajSpoje(pSuborSpoje);
    }

    //2. panel - Zobrazenie údajov
    public String[][] getUdajeUseky()
    {
        return this.model.getUdajeUseky();
    }
    public String[][] getUdajeSpoje()
    {
        return this.model.getUdajeSpoje();
    }
    public LinkedHashMap<Integer, Linka> getLinky()
    {
        return this.model.getLinky();
    }

    //3. panel - Minimalizácia počtu autobusov
    public String vykonajMinimalizaciuAutobusov(ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        return this.model.vykonajMinimalizaciuAutobusov(pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, null);
    }
    public boolean jeProstrediePripravene()
    {
        return this.model.jeProstrediePripravene();
    }

    //4. panel - Minimalizácia počtu vodičov
    public String vykonajMinimalizaciuVodicov(int pPocetBusov, double pGap, int pCasLimit, ArrayList<String[]> pUdajeOturnusoch,
                                              ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje,
                                              ArrayList<String[]> pUdajeOiteraciach)
    {
        return this.model.vykonajMinimalizaciuVodicov(pPocetBusov, pGap, pCasLimit, pUdajeOturnusoch,
                pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
    }

    //5. panel - Maximalizácia obsadenosti
    public String vykonajMaximalizaciuObsadenosti(int pPocetBusov, int pPocetVodicov, double pGap, int pCasLimit,
                                                   ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                   ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        return this.model.vykonajMaximalizaciuObsadenosti(pPocetBusov, pPocetVodicov, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
    }

    //6. panel - Maximalizácia obslúžených spojov
    public String vykonajMaximalizaciuObsluzenychSpojov(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                        ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                        ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        return this.model.vykonajMaximalizaciuObsluzenychSpojov(pPocetBusov, pPocetVodicov, pH, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
    }

    //7. panel - Maximalizácia obslúžených spojov
    public String vykonajMinimalizaciuNeobsluzenychCestujucich(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                        ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                        ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach)
    {
        return this.model.vykonajMinimalizaciuNeobsluzenychCestujucich(pPocetBusov, pPocetVodicov, pH, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
    }

    // 8. panel - Experiment
    public String vykonajExperiment(ArrayList<String[]> informacieObehu,
                                    int pPocetBusov, int pPocetVodicov, int pCasLimit, double pGap)
    {
        return this.model.vykonajExperiment(informacieObehu, pPocetBusov, pPocetVodicov, pCasLimit, pGap);
    }
}