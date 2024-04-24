package udaje;

import mvp.Model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Spoj
{
    private final int ID;
    private final int idLinky;
    private final int idSpoja;
    private final int miestoOdchodu;
    private final LocalTime casOdchodu;
    private final int miestoPrichodu;
    private final LocalTime casPrichodu;
    private final double dlzkaKM;
    private final int obsadenost;

    private ArrayList<Spoj> mozneNasledujuceSpoje;
    private ArrayList<Spoj> moznePredchadzajuceSpoje;
    private ArrayList<Spoj> mozneNasledujuceSpojeSVymenouVodica;
    private ArrayList<Spoj> moznePredchadzajuceSpojeSVymenouVodica;

    private Spoj predchadzajuciSpoj;
    private Spoj nasledujuciSpoj;

    private boolean jeObsluzeny;

    public Spoj(int pID, int pIdLinky, int pIdSpoja,
                int pMiestoOdchodu, LocalTime pCasOdchodu, int pMiestoPrichodu, LocalTime pCasPrichodu,
                double pDlzka, int pObsadenost)
    {
        this.ID = pID;
        this.idLinky = pIdLinky;
        this.idSpoja = pIdSpoja;
        this.miestoOdchodu = pMiestoOdchodu;
        this.casOdchodu = pCasOdchodu;
        this.miestoPrichodu = pMiestoPrichodu;
        this.casPrichodu = pCasPrichodu;
        this.dlzkaKM = pDlzka;
        this.obsadenost = pObsadenost;

        this.mozneNasledujuceSpoje = new ArrayList<>();
        this.moznePredchadzajuceSpoje = new ArrayList<>();
        this.mozneNasledujuceSpojeSVymenouVodica = new ArrayList<>();
        this.moznePredchadzajuceSpojeSVymenouVodica = new ArrayList<>();

        this.predchadzajuciSpoj = null;
        this.nasledujuciSpoj = null;

        this.jeObsluzeny = false;
    }

    //základné atribúty

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
    public int getMiestoOdchodu()
    {
        return this.miestoOdchodu;
    }
    public int getCasOdchoduVMinutach()
    {
        return this.casOdchodu.toSecondOfDay() / 60;
    }
    public LocalTime getCasOdchodu()
    {
        return this.casOdchodu;
    }
    public int getMiestoPrichodu()
    {
        return this.miestoPrichodu;
    }
    public int getCasPrichoduVMinutach()
    {
        return this.casPrichodu.toSecondOfDay() / 60;
    }
    public LocalTime getCasPrichodu()
    {
        return this.casPrichodu;
    }
    public int getObsadenost() {
        return obsadenost;
    }
    public double getDlzka()
    {
        return this.dlzkaKM;
    }
    public int getTrvanieSpoja()
    {
        return Math.abs(this.casPrichodu.toSecondOfDay() - this.casOdchodu.toSecondOfDay()) / 60;
    }

    //vytvorenie mnozin E a F
    public void pripravMnoziny()
    {
        this.mozneNasledujuceSpoje.clear();
        this.moznePredchadzajuceSpoje.clear();
        this.mozneNasledujuceSpojeSVymenouVodica.clear();
        this.moznePredchadzajuceSpojeSVymenouVodica.clear();
    }
    public void pridajMoznyNasledujuciSpoj(Spoj pSpoj)
    {
        this.mozneNasledujuceSpoje.add(pSpoj);
    }
    public void pridajMoznyPredchadzajuciSpoj(Spoj pSpoj)
    {
        this.moznePredchadzajuceSpoje.add(pSpoj);
    }
    public void pridajMoznyNasledujuciSpojSVymenouVodica(Spoj pSpoj)
    {
        this.mozneNasledujuceSpojeSVymenouVodica.add(pSpoj);
    }
    public void pridajMoznyPredchadzajuciSpojSVymenouVodica(Spoj pSpoj)
    {
        this.moznePredchadzajuceSpojeSVymenouVodica.add(pSpoj);
    }

    public ArrayList<Spoj> getMozneNasledujuceSpoje() {
        return this.mozneNasledujuceSpoje;
    }
    public ArrayList<Spoj> getMozneNasledujuceSpojeSVymenouVodica() {
        return this.mozneNasledujuceSpojeSVymenouVodica;
    }
    public ArrayList<Spoj> getMoznePredchadzajuceSpoje() {
        return this.moznePredchadzajuceSpoje;
    }
    public ArrayList<Spoj> getMoznePredchadzajuceSpojeSVymenouVodica() {
        return this.moznePredchadzajuceSpojeSVymenouVodica;
    }

    //vypis
    public String[] vypisInfo()
    {
        String[] udaje = new String[9];
        udaje[0] = String.valueOf(this.idLinky);
        udaje[1] = String.valueOf(this.idSpoja);
        udaje[2] = String.valueOf(this.miestoOdchodu);
        udaje[3] = this.casOdchodu.toString().substring(0, 5);
        udaje[4] = String.valueOf(this.miestoPrichodu);
        udaje[5] = this.casPrichodu.toString().substring(0, 5);
        udaje[6] = String.valueOf(this.dlzkaKM);
        udaje[7] = String.valueOf(this.obsadenost);
        if(this.jeObsluzeny)
            udaje[8] = "áno";
        else
            udaje[8] = "nie";
        return udaje;
    }

    public String[] vypisTurnus(LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky)
    {
        String[] udaje = new String[9];
        udaje[0] = String.valueOf(this.idLinky);
        udaje[1] = String.valueOf(this.idSpoja);
        udaje[2] = String.valueOf(this.miestoOdchodu);
        udaje[3] = this.casOdchodu.toString().substring(0, 5);
        udaje[4] = String.valueOf(this.miestoPrichodu);
        udaje[5] = this.casPrichodu.toString().substring(0, 5);

        if(this.predchadzajuciSpoj != null)
        {
            int prejazd = useky.get(new Dvojica<>(this.predchadzajuciSpoj.getMiestoPrichodu(), this.miestoOdchodu));
            udaje[6] = String.valueOf(prejazd);
        }
        else
        {
            int prejazd = useky.get(new Dvojica<>(Model.DEPO, this.miestoOdchodu));
            udaje[6] = String.valueOf(prejazd);
        }

        if(this.nasledujuciSpoj != null)
        {
            int prejazd = useky.get(new Dvojica<>(this.miestoPrichodu, this.nasledujuciSpoj.getMiestoOdchodu()));
            udaje[7] = String.valueOf(prejazd);
            int prestavka = this.nasledujuciSpoj.getCasOdchoduVMinutach() - this.getCasPrichoduVMinutach() - prejazd;
            udaje[8] = String.valueOf(prestavka);
        }
        else
        {
            int prejazd = useky.get(new Dvojica<>(this.miestoPrichodu, Model.DEPO));
            udaje[7] = String.valueOf(prejazd);
            udaje[8] = String.valueOf(0);
        }

        return udaje;
    }

    //naslednost spojov v turnuse
    public Spoj getPredchadzajuciSpoj() {
        return this.predchadzajuciSpoj;
    }

    public void setPredchadzajuciSpoj(Spoj pPredchadzajuciSpoj) {
        this.predchadzajuciSpoj = pPredchadzajuciSpoj;
    }

    public Spoj getNasledujuciSpoj() {
        return this.nasledujuciSpoj;
    }

    public void setNasledujuciSpoj(Spoj pNasledujuciSpoj) {
        this.nasledujuciSpoj = pNasledujuciSpoj;
    }

    public void nastavObsluzenost(boolean b)
    {
        this.jeObsluzeny = b;
    }
    public boolean getObsluzenost()
    {
        return this.jeObsluzeny;
    }
}