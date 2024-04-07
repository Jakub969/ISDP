package data;

public class Usek {
    private final Zastavka prvaZastavka;
    private final Zastavka druhaZastavka;
    private final int casPrejazdu;
    public Usek(Zastavka pPrvaZastavka, Zastavka pDruhaZastavka, int pCasPrejazdu)
    {
        this.prvaZastavka = pPrvaZastavka;
        this.druhaZastavka = pDruhaZastavka;
        this.casPrejazdu = pCasPrejazdu;
    }

    public Zastavka getPrvaZastavka()
    {
        return this.prvaZastavka;
    }

    public Zastavka getDruhaZastavka()
    {
        return this.druhaZastavka;
    }

    public int getCasPrejazdu()
    {
        return this.casPrejazdu; // v minutach
    }
}
