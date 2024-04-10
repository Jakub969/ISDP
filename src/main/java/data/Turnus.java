package data;

import mvp.Model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Turnus {
    private Zmena prvaZmena;
    private Zmena druhaZmena;

    public Turnus(Zmena pPrvaZmena) {
        this.prvaZmena = pPrvaZmena;
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
}