package dataOld;

/**
 * Trieda reprezentujúca jednu zastávku.
 */
public class ZastavkaOld
{
    /**
     * Jednoznačný idenfikátor zastávky.
     */
    private final int ID;
    /**
     * Názov zastávky.
     */
    private final String nazov;

    /** Parametrický konštruktor.
     * @param pID ID zastávky
     * @param pNazov Názov zastávky
     */
    public ZastavkaOld(int pID, String pNazov)
    {
        this.ID = pID;
        this.nazov = pNazov;
    }

    /** Vráti jednoznačný identifikátor zastávky.
     * @return ID zastávky
     */
    public int getID()
    {
        return this.ID;
    }

    /** Vráti názov zastávky.
     * @return názov zastávky
     */
    public String getNazov()
    {
        return this.nazov;
    }
}
