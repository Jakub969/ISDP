package files;

import data.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class LoaderOfData {

    public void nacitajUseky(String pSuborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky, LinkedHashMap<Integer, Zastavka> pZastavky) throws FileNotFoundException
    {
        Scanner scanner = vytvorScanner(pSuborUseky);
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine();
            String[] stlpce = riadok.split("\\s+");
            int u = Integer.parseInt(stlpce[0]);
            int v = Integer.parseInt(stlpce[1]);
            int c = Integer.parseInt(stlpce[2]);

            Zastavka zu = new Zastavka(u, "");
            pZastavky.put(u,zu);

            Zastavka zv = new Zastavka(v, "");      //TODO prerobiť (nie furt nová zastávka sa dáva do úsekov)
            pZastavky.put(v,zv);

            pUseky.put(new Dvojica<>(u, v), new Usek(zu,zv,c));
            pUseky.put(new Dvojica<>(v, u), new Usek(zu,zv,c));
            pUseky.put(new Dvojica<>(u, u), new Usek(zu,zv,0));
            pUseky.put(new Dvojica<>(v, v), new Usek(zu,zv,0));
        }
        scanner.close();
    }

    public void nacitajSpoje(String pSuborSpoje, LinkedHashMap<Integer, Spoj> pSpoje,
                             LinkedHashMap<Integer, Linka> pLinky, LinkedHashMap<Integer, Zastavka> pZastavky) throws FileNotFoundException
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
            Zastavka miestoOdchodu = pZastavky.get(miestoOdchoduID);
            LocalTime casOdchodu = LocalTime.parse(stlpce[3], formatter);

            // koncová zastávka
            int miestoPrichoduID = Integer.parseInt(stlpce[4]);
            Zastavka miestoPrichodu = pZastavky.get(miestoPrichoduID);
            LocalTime casPrichodu = LocalTime.parse(stlpce[5], formatter);

            int obsadenostSpoja = Integer.parseInt(stlpce[8]);

            // Create an array containing trip information
            Linka linka;
            if(pLinky.containsKey(idLinky))
                linka = pLinky.get(idLinky);
            else
            {
                linka = new Linka(idLinky);
                pLinky.put(idLinky, linka);
            }
            Spoj spoj = new Spoj(id, idLinky, idSpoja, miestoOdchodu, casOdchodu, miestoPrichodu, casPrichodu, obsadenostSpoja);
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