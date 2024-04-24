package filesOld;

import dataOld.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class LoaderOfDataOld {

    public void nacitajUseky(String pSuborUseky, LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> pUseky, LinkedHashMap<Integer, ZastavkaOld> pZastavky) throws FileNotFoundException
    {
        Scanner scanner = vytvorScanner(pSuborUseky);
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine();
            String[] stlpce = riadok.split("\\s+");
            int u = Integer.parseInt(stlpce[0]);
            int v = Integer.parseInt(stlpce[1]);
            int c = Integer.parseInt(stlpce[2]);

            ZastavkaOld zu = new ZastavkaOld(u, "");
            pZastavky.put(u,zu);

            ZastavkaOld zv = new ZastavkaOld(v, "");      //TODO prerobiť (nie furt nová zastávka sa dáva do úsekov)
            pZastavky.put(v,zv);

            pUseky.put(new DvojicaOld<>(u, v), new UsekOld(zu,zv,c));
            pUseky.put(new DvojicaOld<>(v, u), new UsekOld(zu,zv,c));
            pUseky.put(new DvojicaOld<>(u, u), new UsekOld(zu,zv,0));
            pUseky.put(new DvojicaOld<>(v, v), new UsekOld(zu,zv,0));
        }
        scanner.close();
    }

    public void nacitajSpoje(String pSuborSpoje, LinkedHashMap<Integer, SpojOld> pSpoje,
                             LinkedHashMap<Integer, LinkaOld> pLinky, LinkedHashMap<Integer, ZastavkaOld> pZastavky) throws FileNotFoundException
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        Scanner scanner = vytvorScanner(pSuborSpoje);
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine();
            String[] stlpce = riadok.split("\\s+");

            // Extract trip information
            int idLinky = Integer.parseInt(stlpce[0]);
            int idSpoja = Integer.parseInt(stlpce[1]);

            // Unique ID for the trip
            int id = 10000 * idLinky + idSpoja;

            //začiatočná zastávka
            int miestoOdchoduID = Integer.parseInt(stlpce[2]);
            ZastavkaOld miestoOdchodu = pZastavky.get(miestoOdchoduID);
            LocalTime casOdchodu = LocalTime.parse(stlpce[3], formatter);

            // koncová zastávka
            int miestoPrichoduID = Integer.parseInt(stlpce[4]);
            ZastavkaOld miestoPrichodu = pZastavky.get(miestoPrichoduID);
            LocalTime casPrichodu = LocalTime.parse(stlpce[5], formatter);

            int obsadenostSpoja = Integer.parseInt(stlpce[8]);

            LinkaOld linka;
            if(pLinky.containsKey(idLinky))
                linka = pLinky.get(idLinky);
            else
            {
                linka = new LinkaOld(idLinky);
                pLinky.put(idLinky, linka);
            }
            SpojOld spoj = new SpojOld(id, idLinky, idSpoja, miestoOdchodu, casOdchodu, miestoPrichodu, casPrichodu, obsadenostSpoja);
            pSpoje.put(id, spoj);
            linka.pridajSpoj(spoj);
        }
        scanner.close();
    }

    private Scanner vytvorScanner(String fileName) throws FileNotFoundException
    {
        String decodedWay = URLDecoder.decode(getClass().getResource("/" + fileName).getPath(), StandardCharsets.UTF_8);
        File file = new File(decodedWay);
        return new Scanner(file);
    }
}