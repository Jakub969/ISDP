package dataOld;

public class UsekOld {
    private final ZastavkaOld prvaZastavka;
    private final ZastavkaOld druhaZastavka;
    private final int casPrejazdu;
    public UsekOld(ZastavkaOld pPrvaZastavka, ZastavkaOld pDruhaZastavka, int pCasPrejazdu)
    {
        this.prvaZastavka = pPrvaZastavka;
        this.druhaZastavka = pDruhaZastavka;
        this.casPrejazdu = pCasPrejazdu;
    }

    public ZastavkaOld getPrvaZastavka()
    {
        return this.prvaZastavka;
    }

    public ZastavkaOld getDruhaZastavka()
    {
        return this.druhaZastavka;
    }

    public int getCasPrejazdu()
    {
        return this.casPrejazdu; // v minutach
    }
}
