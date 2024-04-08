package data;

import mvp.Model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Trieda reprezentujúca jeden spoj.
 */
public class Spoj
{
    private final int ID;
    private final int idLinky;  //TODO prerobiť na LINKU?
    private final int idSpoja;
    /**
     * Začiatočná zastávka spoja.
     */
    private final Zastavka miestoOdchodu;
    /**
     * Čas, kedy spoj vyráža zo začiatočnej zastávky.
     */
    private final LocalTime casOdchodu;
    /**
     * Konečná zastávka spoja.
     */
    private final Zastavka miestoPrichodu;
    /**
     * Čas, kedy spoj dorazí do koncovej zastávky.
     */
    private final LocalTime casPrichodu;

    private ArrayList<Spoj> mozneNasledujuceSpoje;
    private ArrayList<Spoj> moznePredchadzajuceSpoje;
    private ArrayList<Spoj> mozneNasledujuceSpojeVodic;
    private ArrayList<Spoj> moznePredchadzajuceSpojeVodic;
    private Spoj predchadzajuci;
    private Spoj nasledujuci;
    private boolean koniecZmeny;
    private final int obsadenost;
    private boolean jeObsluzeny;
    public Spoj(int pID, int pIdLinky, int pIdSpoja,
                Zastavka pMiestoOdchodu, LocalTime pCasOdchodu, Zastavka pMiestoPrichodu, LocalTime pCasPrichodu,
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
    public Zastavka getMiestoOdchodu()
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
    public Zastavka getMiestoPrichodu()
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
    public void pridajNaslednostSpojaVodic(Spoj pSpoj)
    {
        pSpoj.pridajSpojKtoryMozePredchadzatVodic(this);
        this.mozneNasledujuceSpojeVodic.add(pSpoj);
    }
    private void pridajSpojKtoryMozePredchadzatVodic(Spoj pSpoj)
    {
        this.moznePredchadzajuceSpojeVodic.add(pSpoj);
    }

    public void pridajNaslednostSpoja(Spoj pSpoj)
    {
        pSpoj.pridajSpojKtoryMozePredchadzat(this);
        this.mozneNasledujuceSpoje.add(pSpoj);
    }
    private void pridajSpojKtoryMozePredchadzat(Spoj pSpoj)
    {
        this.moznePredchadzajuceSpoje.add(pSpoj);
    }
    public ArrayList<Spoj> getMozneNasledujuceSpoje()
    {
        return this.mozneNasledujuceSpoje;
    }
    public ArrayList<Spoj> getMoznePredchadzajuceSpoje()
    {
        return this.moznePredchadzajuceSpoje;
    }
    public ArrayList<Spoj> getMozneNasledujuceSpojeVodic()
    {
        return this.mozneNasledujuceSpojeVodic;
    }
    public ArrayList<Spoj> getMoznePredchadzajuceSpojeVodic()
    {
        return this.moznePredchadzajuceSpojeVodic;
    }
    public String[] vypis(LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky)
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
            int prejazd = useky.get(new Dvojica<>(this.predchadzajuci.getMiestoPrichodu().getID(), this.miestoOdchodu.getID())).getCasPrejazdu();
            udaje[6] = String.valueOf(prejazd);
        }
        else
        {
            int prejazd = useky.get(new Dvojica<>(Model.DEPO, this.getMiestoOdchodu().getID())).getCasPrejazdu();
            udaje[6] = String.valueOf(prejazd);
        }

        if(this.nasledujuci != null)
        {
            int prejazd = useky.get(new Dvojica<>(this.miestoPrichodu.getID(), this.nasledujuci.getMiestoOdchodu().getID())).getCasPrejazdu();
            udaje[7] = String.valueOf(prejazd);
            int prestavka = this.nasledujuci.getCasOdchoduMinuty() - this.getCasPrichoduMinuty() - prejazd;
            udaje[8] = String.valueOf(prestavka);
        }
        else
        {
            int prejazd = useky.get(new Dvojica<>(this.getMiestoPrichodu().getID(), Model.DEPO)).getCasPrejazdu();
            udaje[7] = String.valueOf(prejazd);
            udaje[8] = String.valueOf(0);
        }

        udaje[9] = String.valueOf(this.obsadenost);
        return udaje;
    }
    public Spoj getNasledujuci() {
        return nasledujuci;
    }
    public void setNasledujuci(Spoj nasledujuci) {
        this.nasledujuci = nasledujuci;
    }
    public Spoj getPredchadzajuci() {
        return predchadzajuci;
    }
    public void setPredchadzajuci(Spoj predchadzajuci) {
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
