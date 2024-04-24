package udaje;

public class Turnus {
    private final int ID;
    private final Zmena prvaZmena;
    private Zmena druhaZmena;

    public Turnus(int id, Zmena pPrvaZmena)
    {
        this.ID = id;
        this.prvaZmena = pPrvaZmena;
    }

    public String[] vypisUdajeOturnuse()
    {
        String[] udaje = new String[3];
        udaje[0] = String.valueOf(this.ID);
        int trvanie;
        if(this.druhaZmena == null)
        {
            udaje[1] = String.valueOf(1);
            trvanie = this.prvaZmena.getTrvanieZmeny();
        }
        else
        {
            udaje[1] = String.valueOf(2);
            trvanie = this.prvaZmena.getTrvanieZmeny() + this.druhaZmena.getTrvanieZmeny();

        }
        if(trvanie % 60 < 10)
            udaje[2] = trvanie / 60 + ":0" + trvanie % 60;
        else
            udaje[2] = trvanie / 60 + ":" + trvanie % 60;
        return udaje;
    }
    public void pridajDruhuZmenu(Zmena pZmena)
    {
        this.druhaZmena = pZmena;
    }

    public Zmena getPrvaZmena()
    {
        return this.prvaZmena;
    }
    public Zmena getDruhaZmena()
    {
        return this.druhaZmena;
    }

    public int getID() {
        return ID;
    }
}
