package dataOld;

public class TurnusOld {
    private ZmenaOld prvaZmena;
    private ZmenaOld druhaZmena;

    public TurnusOld(ZmenaOld pPrvaZmena) {
        this.prvaZmena = pPrvaZmena;
    }

    public void pridajDruhuZmenu(ZmenaOld pZmena)
    {
        this.druhaZmena = pZmena;
    }

    public ZmenaOld getPrvaZmena()
    {
        return this.prvaZmena;
    }
    public ZmenaOld getDruhaZmena()
    {
        return this.druhaZmena;
    }
}