package mvp;

import mvp.view.TurnusViz;
import udaje.Linka;
import vizualizer.BusSchedulePanel;
import vizualizer.Ride;
import udaje.Turnus;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class Presenter {
    private final Model model;
    private final String outputDir; // Pridaný adresár pre ukladanie vizualizácií

    public class TurnusDTO {
        public LocalTime startDate;
        public LocalTime endDate;
        public Map<String, List<Ride>> turnusMap;

        public TurnusDTO(LocalTime startDate, LocalTime endDate, Map<String, List<Ride>> turnusMap) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.turnusMap = turnusMap;
        }
    }

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

    private TurnusDTO vykresliTurnusyPreGUI() {
        List<Turnus> turnusy = model.getVsetkyTurnusy();
        Map<String, List<Ride>> turnusMap = new HashMap<>();
        LocalTime minDate = LocalTime.MAX;
        LocalTime maxDate = LocalTime.MIN;
        if (turnusy != null) {
            for (int i = 0; i < turnusy.size(); i++) {
                Turnus t = turnusy.get(i);
                turnusMap.put("Linka " + t.getID(), TurnusViz.renderTurnus(t));

                var dateMin = t.getPrvaZmena().getPrvySpoj().getCasOdchodu();
                var dateMax = t.getPrvaZmena().getPoslednySpoj().getCasPrichodu();
                if (dateMin.isBefore(minDate)) {
                    minDate = dateMin;
                }
                if (dateMax.isAfter(maxDate)) {
                    maxDate = dateMax;
                }
            }
        }

        return new TurnusDTO(minDate.minusMinutes(minDate.getMinute() % 30),
                maxDate.minusMinutes(maxDate.getMinute() % 30).plusMinutes(30),
                turnusMap);
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
        BusSchedulePanel busSchedulePanel = new BusSchedulePanel(vykresliTurnusyPreGUI());
        vykresliOkno(vykresliTurnusyPreGUI());
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
        //vykresliTurnusy();
        vykresliTurnusyPreGUI();
        return result;
    }

    // 8. panel - Experiment
    public String vykonajExperiment(ArrayList<String[]> informacieObehu,
                                    int pPocetBusov, int pPocetVodicov, int pCasLimit, double pGap)
    {
        return this.model.vykonajExperiment(informacieObehu, pPocetBusov, pPocetVodicov, pCasLimit, pGap);
    }

    public void vykresliOkno(Presenter.TurnusDTO turnusDTO) {
        JFrame frame = new JFrame("Vizualizácia trás autobusov");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600); // uprav veľkosť okna podľa potreby

        BusSchedulePanel panel = new BusSchedulePanel(turnusDTO);
        panel.setPreferredSize(new Dimension(2500, 1000)); // nastav dostatočnú veľkosť pre scroll

        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.add(scrollPane);
        frame.setVisible(true);
    }
}