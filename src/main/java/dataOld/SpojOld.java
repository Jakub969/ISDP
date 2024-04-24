package dataOld;

import mvpOld.ModelOld;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Trieda reprezentujúca jeden spoj.
 */
public class SpojOld
{
    private final int ID;
    private final int idLinky;  //TODO prerobiť na LINKU?
    private final int idSpoja;
    /**
     * Začiatočná zastávka spoja.
     */
    private final ZastavkaOld miestoOdchodu;
    /**
     * Čas, kedy spoj vyráža zo začiatočnej zastávky.
     */
    private final LocalTime casOdchodu;
    /**
     * Konečná zastávka spoja.
     */
    private final ZastavkaOld miestoPrichodu;
    /**
     * Čas, kedy spoj dorazí do koncovej zastávky.
     */
    private final LocalTime casPrichodu;

    private ArrayList<SpojOld> mozneNasledujuceSpoje;
    private ArrayList<SpojOld> moznePredchadzajuceSpoje;
    private ArrayList<SpojOld> mozneNasledujuceSpojeVodic;
    private ArrayList<SpojOld> moznePredchadzajuceSpojeVodic;
    private SpojOld predchadzajuci;
    private SpojOld nasledujuci;
    private boolean koniecZmeny;
    private final int obsadenost;
    private boolean jeObsluzeny;
    public SpojOld(int pID, int pIdLinky, int pIdSpoja,
                   ZastavkaOld pMiestoOdchodu, LocalTime pCasOdchodu, ZastavkaOld pMiestoPrichodu, LocalTime pCasPrichodu,
                   int pObsadenost)
    {
        this.ID = pID;
        this.idLinky = pIdLinky;
        this.idSpoja = pIdSpoja;
        this.miestoOdchodu = pMiestoOdchodu;
        this.casOdchodu = pCasOdchodu;
        this.miestoPrichodu = pMiestoPrichodu;
        this.casPrichodu = pCasPrichodu;
        this.obsadenost = pObsadenost;
        this.mozneNasledujuceSpoje = new ArrayList<>();
        this.moznePredchadzajuceSpoje = new ArrayList<>();
        this.mozneNasledujuceSpojeVodic = new ArrayList<>();
        this.moznePredchadzajuceSpojeVodic = new ArrayList<>();
        this.predchadzajuci = null;
        this.nasledujuci = null;
        this.koniecZmeny = false;
        this.jeObsluzeny = false;
    }

    public int getID()
    {
        return this.ID;
    }
    public int getIdLinky()
    {
        return this.idLinky;
    }
    public int getIdSpoja()
    {
        return this.idSpoja;
    }
    public ZastavkaOld getMiestoOdchodu()
    {
        return this.miestoOdchodu;
    }
    public int getMiestoOdchoduID()
    {
        return this.miestoOdchodu.getID();
    }
    public int getCasOdchoduMinuty()
    {
        return this.casOdchodu.toSecondOfDay() / 60;
    }
    public LocalTime getCasOdchodu()
    {
        return this.casOdchodu;
    }
    public ZastavkaOld getMiestoPrichodu()
    {
        return this.miestoPrichodu;
    }
    public int getMiestoPrichoduID()
    {
        return this.miestoPrichodu.getID();
    }
    public int getCasPrichoduMinuty()
    {
        return this.casPrichodu.toSecondOfDay() / 60;
    }
    public LocalTime getCasPrichodu()
    {
        return this.casPrichodu;
    }
    public int getTrvanieSpoja()
    {
        return Math.abs(this.casPrichodu.toSecondOfDay() - this.casOdchodu.toSecondOfDay()) / 60;   //TODO načítať trvanie?
    }
    public void pridajNaslednostSpojaVodic(SpojOld pSpoj)
    {
        pSpoj.pridajSpojKtoryMozePredchadzatVodic(this);
        this.mozneNasledujuceSpojeVodic.add(pSpoj);
    }
    private void pridajSpojKtoryMozePredchadzatVodic(SpojOld pSpoj)
    {
        this.moznePredchadzajuceSpojeVodic.add(pSpoj);
    }

    public void pridajNaslednostSpoja(SpojOld pSpoj)
    {
        pSpoj.pridajMoznyPredchadzajuciSpoj(this);
        this.mozneNasledujuceSpoje.add(pSpoj);
    }
    private void pridajMoznyPredchadzajuciSpoj(SpojOld pSpoj)
    {
        this.moznePredchadzajuceSpoje.add(pSpoj);
    }
    public ArrayList<SpojOld> getMozneNasledujuceSpoje()
    {
        return this.mozneNasledujuceSpoje;
    }
    public ArrayList<SpojOld> getMoznePredchadzajuceSpoje()
    {
        return this.moznePredchadzajuceSpoje;
    }
    public ArrayList<SpojOld> getMozneNasledujuceSpojeVodic()
    {
        return this.mozneNasledujuceSpojeVodic;
    }
    public ArrayList<SpojOld> getMoznePredchadzajuceSpojeVodic()
    {
        return this.moznePredchadzajuceSpojeVodic;
    }
    public String[] vypis(LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> useky)
    {
        String[] udaje = new String[10];
        udaje[0] = String.valueOf(this.idLinky);
        udaje[1] = String.valueOf(this.idSpoja);
        udaje[2] = String.valueOf(this.miestoOdchodu.getID());
        udaje[3] = this.casOdchodu.toString().substring(0, 5);
        udaje[4] = String.valueOf(this.miestoPrichodu.getID());
        udaje[5] = this.casPrichodu.toString().substring(0, 5);

        if(this.predchadzajuci != null)
        {
            int prejazd = useky.get(new DvojicaOld<>(this.predchadzajuci.getMiestoPrichoduID(), this.miestoOdchodu.getID())).getCasPrejazdu();
            udaje[6] = String.valueOf(prejazd);
        }
        else
        {
            int prejazd = useky.get(new DvojicaOld<>(ModelOld.DEPO, this.getMiestoOdchoduID())).getCasPrejazdu();
            udaje[6] = String.valueOf(prejazd);
        }

        if(this.nasledujuci != null)
        {
            int prejazd = useky.get(new DvojicaOld<>(this.miestoPrichodu.getID(), this.nasledujuci.getMiestoOdchoduID())).getCasPrejazdu();
            udaje[7] = String.valueOf(prejazd);
            int prestavka = this.nasledujuci.getCasOdchoduMinuty() - this.getCasPrichoduMinuty() - prejazd;
            udaje[8] = String.valueOf(prestavka);
        }
        else
        {
            int prejazd = useky.get(new DvojicaOld<>(this.getMiestoPrichoduID(), ModelOld.DEPO)).getCasPrejazdu();
            udaje[7] = String.valueOf(prejazd);
            udaje[8] = String.valueOf(0);
        }

        udaje[9] = String.valueOf(this.obsadenost);
        return udaje;
    }
    public SpojOld getNasledujuci() {
        return nasledujuci;
    }
    public void setNasledujuci(SpojOld nasledujuci) {
        this.nasledujuci = nasledujuci;
    }
    public SpojOld getPredchadzajuci() {
        return predchadzajuci;
    }
    public void setPredchadzajuci(SpojOld predchadzajuci) {
        this.predchadzajuci = predchadzajuci;
    }

    public boolean isKoniecZmeny() {
        return koniecZmeny;
    }

    public void setKoniecZmeny(boolean koniecZmeny) {
        this.koniecZmeny = koniecZmeny;
    }

    public int getObsadenost() {
        return obsadenost;
    }

    public boolean isJeObsluzeny() {
        return jeObsluzeny;
    }

    public void setJeObsluzeny(boolean jeObsluzeny) {
        this.jeObsluzeny = jeObsluzeny;
    }
}
