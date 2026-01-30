package subory;

import mvp.Model;
import udaje.*;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class NacitavacUdajov
{
    public void nacitajUseky(String pNazovSuboruUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky) throws Exception
    {
        File suborUseky = getSubor(pNazovSuboruUseky);
        nacitajUseky(suborUseky, pUseky);
    }

    public void nacitajUseky(File pSuborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky) throws Exception
    {
        Scanner scanner = new Scanner(pSuborUseky);
        while (scanner.hasNextLine())
        {
            String riadok = scanner.nextLine();
            String[] stlpce = riadok.split("\\s+");
            int u = Integer.parseInt(stlpce[0]);
            int v = Integer.parseInt(stlpce[1]);
            int c = Integer.parseInt(stlpce[2]);

            pUseky.put(new Dvojica<>(u, v), c);
            pUseky.put(new Dvojica<>(v, u), c);
            pUseky.put(new Dvojica<>(u, u), 0);
            pUseky.put(new Dvojica<>(v, v), 0);
        }
        scanner.close();
    }

    public void nacitajSpoje(String pNazovSuboruSpoje, LinkedHashMap<Integer, Spoj> pSpoje,
                             LinkedHashMap<Integer, Linka> pLinky) throws Exception
    {
        File suborSpoje = getSubor(pNazovSuboruSpoje);
        nacitajSpoje(suborSpoje, pSpoje, pLinky);
    }

    public void nacitajSpoje(File pSuborSpoje, LinkedHashMap<Integer, Spoj> pSpoje,
                             LinkedHashMap<Integer, Linka> pLinky) throws Exception
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        Scanner scanner = new Scanner(pSuborSpoje);
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine();
            String[] stlpce = riadok.split("\\s+");

            int idLinky = Integer.parseInt(stlpce[0]);
            int idSpoja = Integer.parseInt(stlpce[1]);

            // vytvor unikátny identifikátor spoja
            int id = Model.K * idLinky + idSpoja;

            // začiatočná zastávka
            int miestoOdchodu = Integer.parseInt(stlpce[2]);
            LocalTime casOdchodu = LocalTime.parse(stlpce[3], formatter);

            // koncová zastávka
            int miestoPrichodu = Integer.parseInt(stlpce[4]);
            LocalTime casPrichodu = LocalTime.parse(stlpce[5], formatter);

            double dlzka = Double.parseDouble(stlpce[7].replace(',', '.'));
            int obsadenostSpoja = Integer.parseInt(stlpce[8]);

            Linka linka;
            if(pLinky.containsKey(idLinky))
                linka = pLinky.get(idLinky);
            else
            {
                linka = new Linka(idLinky);
                pLinky.put(idLinky, linka);
            }

            Spoj spoj = new Spoj(id, idLinky, idSpoja, miestoOdchodu, casOdchodu, miestoPrichodu, casPrichodu, dlzka, obsadenostSpoja);
            pSpoje.put(id, spoj);
            linka.pridajSpoj(spoj);
        }
        scanner.close();
    }

    private File getSubor(String fileName)
    {
        String decodedWay = URLDecoder.decode(getClass().getResource("/" + fileName).getPath(), StandardCharsets.UTF_8);
        return new File(decodedWay);
    }

    public void nacitajSpojeCSV(
            File subor,
            LinkedHashMap<Integer, Spoj> pSpoje,
            LinkedHashMap<Integer, Linka> pLinky) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        Scanner scanner = new Scanner(subor);

        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String[] s = scanner.nextLine().split(",");

            int idLinky = Integer.parseInt(s[0]);
            int idSpoja = Integer.parseInt(s[1]);
            int id = Model.K * idLinky + idSpoja;

            int miestoOdchodu = Integer.parseInt(s[2]);
            LocalTime casOdchodu = LocalTime.parse(s[3], formatter);

            int miestoPrichodu = Integer.parseInt(s[4]);
            LocalTime casPrichodu = LocalTime.parse(s[5], formatter);

            double dlzka = Double.parseDouble(s[7]);
            int obsadenost = Integer.parseInt(s[8]);

            Linka linka = pLinky.computeIfAbsent(idLinky, Linka::new);

            Spoj spoj = new Spoj(id, idLinky, idSpoja,
                    miestoOdchodu, casOdchodu,
                    miestoPrichodu, casPrichodu,
                    dlzka, obsadenost);

            pSpoje.put(id, spoj);
            linka.pridajSpoj(spoj);
        }
        scanner.close();
    }

    public void nacitajUsekyCSV(
            File subor,
            LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky
    ) throws Exception {

        Scanner scanner = new Scanner(subor);
        scanner.nextLine(); // hlavička

        while (scanner.hasNextLine()) {
            String[] s = scanner.nextLine().split(",");

            int u = Integer.parseInt(s[0]);
            int v = Integer.parseInt(s[1]);
            int c = Integer.parseInt(s[2]);

            pUseky.put(new Dvojica<>(u, v), c);
            pUseky.put(new Dvojica<>(v, u), c);
            pUseky.put(new Dvojica<>(u, u), 0);
            pUseky.put(new Dvojica<>(v, v), 0);
        }
        scanner.close();
    }
}
