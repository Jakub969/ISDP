package mvp;

import mvp.view.TurnusViz;
import udaje.Linka;
import udaje.Ride;
import udaje.Turnus;
import java.io.File;
import java.util.*;

public class Presenter {
    private final Model model;
    private final String outputDir; // Pridaný adresár pre ukladanie vizualizácií

    public Presenter() {
        this.model = new Model();
        this.outputDir = "turnus_vizualizacie"; // Predvolený výstupný adresár
    }

    public Presenter(String outputDirectory) {
        this.model = new Model();
        this.outputDir = outputDirectory;
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

    // Pomocná metóda pre vykreslenie turnusov
    private void vykresliTurnusy() throws Exception {
        List<Turnus> turnusy = model.getVsetkyTurnusy();


        if (turnusy != null) {
            for (int i = 0; i < turnusy.size(); i++) {
                Turnus t = turnusy.get(i);
                String subor = outputDir + "/turnus_" + (i+1) + "_" +
                        model.getPoslednyTypOptimalizacie().toLowerCase().replace(" ", "_") + ".png";
                TurnusViz.renderTurnus(t, subor);

            }
        }
    }

    private Map<String, List<Ride>> vykresliTurnusyPreGUI() {
        List<Turnus> turnusy = model.getVsetkyTurnusy();
        Map<String, List<Ride>> turnusMap = new HashMap<>();
        if (turnusy != null) {
            for (int i = 0; i < turnusy.size(); i++) {
                Turnus t = turnusy.get(i);
                turnusMap.put("Linka " + t.getID(), TurnusViz.renderTurnus(t));
            }
        }
        return turnusMap;
    }


    //3. panel - Minimalizácia počtu autobusov
    public String vykonajMinimalizaciuAutobusov(ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje) throws Exception {
        String result = this.model.vykonajMinimalizaciuAutobusov(pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, null);
        vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }
    public boolean jeProstrediePripravene()
    {
        return this.model.jeProstrediePripravene();
    }

    //4. panel - Minimalizácia počtu vodičov
    public String vykonajMinimalizaciuVodicov(int pPocetBusov, double pGap, int pCasLimit, ArrayList<String[]> pUdajeOturnusoch,
                                              ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje,
                                              ArrayList<String[]> pUdajeOiteraciach) throws Exception {
        String result = this.model.vykonajMinimalizaciuVodicov(pPocetBusov, pGap, pCasLimit, pUdajeOturnusoch,
                pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
        vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }

    //5. panel - Maximalizácia obsadenosti
    public String vykonajMaximalizaciuObsadenosti(int pPocetBusov, int pPocetVodicov, double pGap, int pCasLimit,
                                                   ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                   ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach) throws Exception {
        String result = this.model.vykonajMaximalizaciuObsadenosti(pPocetBusov, pPocetVodicov, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
        vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }

    //6. panel - Maximalizácia obslúžených spojov
    public String vykonajMaximalizaciuObsluzenychSpojov(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                        ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                        ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach) throws Exception {
        String result = this.model.vykonajMaximalizaciuObsluzenychSpojov(pPocetBusov, pPocetVodicov, pH, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
        vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }

    //7. panel - Maximalizácia obslúžených spojov
    public String vykonajMinimalizaciuNeobsluzenychCestujucich(int pPocetBusov, int pPocetVodicov, double pH, double pGap, int pCasLimit,
                                                        ArrayList<String[]> pUdajeOturnusoch, ArrayList<String[]> pTurnusyUdaje,
                                                        ArrayList<String[][]> pSpojeUdaje, ArrayList<String[]> pUdajeOiteraciach) throws Exception {
        String result = this.model.vykonajMinimalizaciuNeobsluzenychCestujucich(pPocetBusov, pPocetVodicov, pH, pGap, pCasLimit,
                pUdajeOturnusoch, pTurnusyUdaje, pSpojeUdaje, pUdajeOiteraciach);
        vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }

    // 8. panel - Experiment
    public String vykonajExperiment(ArrayList<String[]> informacieObehu,
                                    int pPocetBusov, int pPocetVodicov, int pCasLimit, double pGap)
    {
        return this.model.vykonajExperiment(informacieObehu, pPocetBusov, pPocetVodicov, pCasLimit, pGap);
    }
}