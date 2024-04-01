package data;

import java.time.LocalTime;
import java.util.ArrayList;

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

    public Spoj(int pID, int pIdLinky, int pIdSpoja,
                Zastavka pMiestoOdchodu, LocalTime pCasOdchodu, Zastavka pMiestoPrichodu, LocalTime pCasPrichodu)
    {
        this.ID = pID;
        this.idLinky = pIdLinky;
        this.idSpoja = pIdSpoja;
        this.miestoOdchodu = pMiestoOdchodu;
        this.casOdchodu = pCasOdchodu;
        this.miestoPrichodu = pMiestoPrichodu;
        this.casPrichodu = pCasPrichodu;
        this.mozneNasledujuceSpoje = new ArrayList<>();
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
    public int getCasOdchodu()
    {
        return this.casOdchodu.toSecondOfDay();
    }
    public Zastavka getMiestoPrichodu()
    {
        return this.miestoPrichodu;
    }
    public int getMiestoPrichoduID()
    {
        return this.miestoPrichodu.getID();
    }
    public int getCasPrichodu()
    {
        return this.casPrichodu.toSecondOfDay();
    }
    public int getTrvanieSpoja()
    {
        return Math.abs(this.casPrichodu.toSecondOfDay() - this.casOdchodu.toSecondOfDay());
    }
    public void pridajSpojKtoryMozeNasledovat(Spoj pSpoj)
    {
        this.mozneNasledujuceSpoje.add(pSpoj);
    }
    public ArrayList<Spoj> getMozneNasledujuceSpoje()
    {
        return this.mozneNasledujuceSpoje;
    }
    public String[] vypis()
    {
        String[] udaje = new String[6];
        udaje[0] = String.valueOf(this.idLinky);
        udaje[1] = String.valueOf(this.idSpoja);
        udaje[2] = String.valueOf(this.miestoOdchodu.getID());
        udaje[3] = this.casOdchodu.toString().substring(0, 5);
        udaje[4] = String.valueOf(this.miestoPrichodu.getID());
        udaje[5] = this.casPrichodu.toString().substring(0, 5);
        return udaje;
    }
}
